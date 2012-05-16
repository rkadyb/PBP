package com.pbp;


import java.util.ArrayList;

public class Password
{
	// Global Variables
	private int VARIANCE_THRESH = 15;
	private final int BAD_FAIL_RATE = 4;
	private double GAUSS_WIDTH = 3.0;
	private final double VAR_ADJUST = 800.0;
	
	// Set how we are entering values into the timing profile, either 
	// in-between values or the overall running times
	public enum TimingMode {
		BETWEEN, TOTAL
	}
	
	// Default timing mode
	private TimingMode tm = TimingMode.TOTAL;
	
	// Create the values that store time timed data values.
	private boolean debug = false;
	private double[] mu, sigma;
	private long[][] attempts;
	private String login = "";
	private int updates = 0, passHash = 0, length = 0, initCount = 3, updateCount = 3;
	private double mean = 0.0;
	private boolean initialized = false;
	
	/**
	 * The constructor for a new password.
	 * @param login The username/login
	 * @param password The password for the user.
	 * @param times The timed values associated with the password.
	 */
	public Password(String login, String password, ArrayList<Long> times)
	{
		// Initialize all of the private variables.
		this.login = login;
		this.length = password.length() - 1;
		this.passHash = password.hashCode();
		this.attempts = new long[this.updateCount][this.length];
		this.mu = new double[this.length];
		this.sigma = new double[this.length];
		
		// Populate the attempts list with the initial login.
		for (int i = 0; i < this.length; i++)
		{
			this.attempts[this.updates][i] = times.get(i);
			this.mu[i] = times.get(i);
			this.sigma[i] = 0.0;
			this.mean += this.mu[i];
		}
		this.mean /= this.length;

		// Increment the update counter.
		this.updates++;
	}
	
	/**
	 * This functions initializes the password to a steady state.
	 * @param password The current password that was supplied.
	 * @param times The values for each of the timed data points.
	 */
	public boolean initialize(String login, String password, ArrayList<Long> times)
	{
		// If timing was messed up, don't allow.
		if (times.size() != this.length)
			return false;
		
		// If the password has not been fully initialized, update it.
		if (this.updates < this.initCount && password.hashCode() == this.passHash && this.login.equals(login))
		{
			this.update(times);
		
			// If the password has been fully initialized, save its state.
			if (this.updates >= this.initCount)
			{
				this.initialized = true;
				// Train the Variance and Gaussian Width to fit our data set
				setConstants(tm);
				
			}
			return true;
		}
		
		return false;
	}
	
	/**
	 * A private function that configures our Variance and Gaussian Width Parameters
	 * given the training data.
	 * 
	 */
	private void setConstants(TimingMode tm) {
		double [] interval_mu = new double[this.length];
		if (tm == TimingMode.TOTAL) {
			for (int i=this.length-1; i>=0; i--) {
				if (i == 0) {
					interval_mu[0] = this.mu[0];
				} else {
					interval_mu[i] = this.mu[i] - this.mu[i-1];
				}
			}
		} else {
			interval_mu = this.mu;
		}
		
		double mean_interval_mu = getMean(interval_mu);
		
		double median_interval_mu = getMedian(interval_mu);
		
		double range_mu = getRange(interval_mu);
		
		double deciding_factor;
		
		// if our timing inputs vary by 2 or more orders of magnitude,
		// assume that using the median gets rid of outliers
		if (range_mu > 50) {
			deciding_factor = median_interval_mu;
		} else {
			deciding_factor = mean_interval_mu;
		}
		
		double log_val = Math.log10(deciding_factor);
		log_val = (log_val > 1.0) ? log_val : 1.0;
		
		GAUSS_WIDTH = 4.0 - log_val;
		GAUSS_WIDTH = (GAUSS_WIDTH > 1.0) ? GAUSS_WIDTH : 1.0;
		VARIANCE_THRESH = (int) (16.0 - log_val);
		
		if (debug) {
			System.out.println("mean "+mean_interval_mu);
			System.out.println("median "+median_interval_mu);
			System.out.println("range "+range_mu);
			System.out.println("log val "+log_val);
			System.out.println("GW "+GAUSS_WIDTH);
			System.out.println("Var "+VARIANCE_THRESH);
		}
	}
	
	private static double getMean(double[] values) {
		double mean = 0;
		for (int i=0; i<values.length; i++) {
			mean += values[i];
		}
		mean /= values.length;
		return mean;
	}
	
	private static double getMedian(double[] values) {
		MergeSort ms = new MergeSort();
		ms.sort(values);
		double median = values[values.length/2]; 
		return median;
	}
	
	/**
	 * Returns the scaling difference between the largest and smallest values
	 * @param values
	 * @return
	 */
	private static double getRange(double[] values) {
		MergeSort ms = new MergeSort();
		ms.sort(values);
		return Math.abs(values[values.length-1]/values[0]);
	}
	
	/**
	 * A public function to access the private variable.
	 * @return 'True' if the password has been initialized, 'False' otherwise.
	 */
	public boolean isInitialized()
	{
		// Return the password state.
		return this.initialized;
	}
		
	/**
	 * Checks if a given password and the timing is correct.
	 * @param password The password supplied by the user.
	 * @param times The timed values for this input of the password.
	 * @return 'True' if password and timing data are a match and 'False' otherwise.
	 */
	public boolean check(String login, String password, ArrayList<Long> times)
	{

		// If timing was messed up, don't allow.
		if (times.size() != this.length)
			return false;
		
		// FOR DEBUGGING ONLY
		if (this.debug)
		{
			System.out.println("Check: ");
			for (int i = 0; i < this.length; i++)
				System.out.println((int) (times.get(i) - this.mu[i]) + " " + (int)this.mu[i] + " " + (int)this.sigma[i]);
			System.out.println("");
		}

		// Check to verify that the password was fully initialized.
		if (!this.initialized)
			return false;
		
		// Check that the hash of the current password matches.
		if (this.login.equals(login) && password.hashCode() != this.passHash)
			return false;
		
		// Make sure each of the timed values in within the acceptable range.
		double variance = 0.0;
		int failed = 0;
		
		for (int i = 0; i < this.length; i++)
		{
			if (times.get(i) < this.mu[i] - GAUSS_WIDTH*this.sigma[i])
				failed++;
			if (times.get(i) > this.mu[i] + GAUSS_WIDTH*this.sigma[i])
				failed++;
			
			//variance += Math.pow(((double)times.get(i) - this.mu[i]), 2.0);
			//variance += Math.pow(1+Math.abs(((double)times.get(i) - this.mu[i]))/this.sigma[i], 4.0);
			variance += Math.pow(((double)times.get(i) - this.mu[i])/this.sigma[i], 2.0);
		}
		variance /= this.length;
		variance *= this.length * this.length;
		variance *= 16.0;
		
		// FOR DUBUGGING ONLY.
		if (this.debug)
		{
			System.out.println("Variance: " + (int)(Math.sqrt(variance)/this.length));
			System.out.println("Variance: " + (int)(Math.sqrt(variance)/this.length + failed + (VAR_ADJUST/this.mean)));
		}
		
		// If the overall variance is too high, should not pass.
		if ((int)(Math.sqrt(variance)/this.length + failed + (VAR_ADJUST/this.mean)) > VARIANCE_THRESH || failed > (int)(this.length/BAD_FAIL_RATE))
			return false;
		
		// Update the stored value list.
		this.update(times);
		
		// Return 'True' because the password matched.
		return true;
	}
	
	/**
	 * This function updates the stored value list for each successful login attempt.
	 * @param times The timed value for the current login.
	 */
	private void update(ArrayList<Long> times)
	{
		// If timing was messed up, don't allow.
		if (times.size() != this.length)
			return;
		
		// Iterate through each timed value.
		double mean_sum = 0.0;
		for (int i = 0; i < this.length; i++)
		{
			// Populate the stored success list with FIFO.
			this.attempts[this.updates % this.updateCount][i] = times.get(i);
			
			// Calculate the mean and variance of the stored successes.
			int count = (this.updates < this.updateCount) ? this.updates+1 : this.updateCount;
			double sum = 0, squares = 0;
			for (int j = 0; j < count; j++)
			{
				long val = this.attempts[j][i];
				sum += val;
				squares += val*val;
			}
			
			// Update the stored bounds for each timed value.
			this.mu[i] = sum/count;
			this.sigma[i] = Math.sqrt(squares/count - this.mu[i]*this.mu[i]);
			this.sigma[i] = (this.sigma[i] > this.mu[i]/10.0) ? this.mu[i]/10.0 : this.sigma[i];
			mean_sum += this.mu[i];
		}
		this.mean = mean_sum / this.length;
		
		// FOR DEBUGGING ONLY
		if (this.debug)
		{
			System.out.println("Update: ");
			for (int i = 0; i < this.length; i++)
				System.out.println((int)this.mu[i] + " " + (int)this.sigma[i]);
			System.out.println("");
		}
		
		// Increment the update counter.
		this.updates++;
	}
}
