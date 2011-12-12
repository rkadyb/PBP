package com.pbp;

import java.util.ArrayList;

public class Password 
{
	// Create the values that store time timed data values.
	private boolean debug = false;
	private double[] mu, sigma;
	private int[][] attempts;
	private String login = "";
	private int updates = 0, passHash = 0, length = 0, initCount = 10, updateCount = 20;
	private boolean initialized = false;
	
	/**
	 * The constructor for a new password.
	 * @param password The password for the user.
	 * @param times The timed values associated with the password.
	 */
	public Password(String login, String password, ArrayList<Integer> times)
	{
		// Initialize all of the private variables.
		this.login = login;
		this.length = 2*(password.length()-1);
		this.passHash = password.hashCode();
		this.attempts = new int[this.updateCount][this.length];
		this.mu = new double[this.length];
		this.sigma = new double[this.length];
		
		// Populate the attempts list with the initial login.
		for (int i = 0; i < this.length; i++)
		{
			this.attempts[this.updates][i] = times.get(i);
			this.mu[i] = times.get(i);
			this.sigma[i] = 0.0;
		}
		
		// Increment the update counter.
		this.updates++;
	}
	
	/**
	 * This functions initializes the password to a steady state.
	 * @param password The current password that was supplied.
	 * @param times The values for each of the timed data points.
	 */
	public boolean initialize(String login, String password, ArrayList<Integer> times)
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
				this.initialized = true;
			
			return true;
		}
		
		return false;
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
	public boolean check(String login, String password, ArrayList<Integer> times)
	{

		// If timing was messed up, don't allow.
		if (times.size() != this.length)
			return false;
		
		// FOR DEBUGGING ONLY
		if (this.debug)
		{
			System.out.println("Check: ");
			for (int i = 0; i < this.length; i++)
				System.out.println(times.get(i) + " " + (int)this.mu[i] + " " + (int)this.sigma[i]);
			System.out.println("");
		}

		// Check to verify that the password was fully initialized.
		if (!this.initialized)
			return false;
		
		// Check that the hash of the current password matches.
		if (this.login.equals(login) && password.hashCode() != this.passHash)
			return false;
		
		// Make sure each of the timed values in within the acceptable range.
		for (int i = 0; i < this.length; i++)
		{
			if (times.get(i) < this.mu[i] - 3.0*this.sigma[i])
				return false;
			if (times.get(i) > this.mu[i] + 3.0*this.sigma[i])
				return false;
		}
		
		// Update the stored value list.
		this.update(times);
		
		// Return 'True' because the password matched.
		return true;
	}
	
	/**
	 * This function updates the stored value list for each successful login attempt.
	 * @param times The timed value for the current login.
	 */
	private void update(ArrayList<Integer> times)
	{
		// If timing was messed up, don't allow.
		if (times.size() != this.length)
			return;
		
		// Iterate through each timed value.
		for (int i = 0; i < this.length; i++)
		{
			// Populate the stored success list with FIFO.
			this.attempts[this.updates % this.updateCount][i] = times.get(i);
			
			// Calculate the mean and variance of the stored successes.
			int count = (this.updates < this.updateCount) ? this.updates+1 : this.updateCount;
			double sum = 0, squares = 0;
			for (int j = 0; j < count; j++)
			{
				int val = this.attempts[j][i];
				sum += val;
				squares += val*val;
			}
			
			// Update the stored bounds for each timed value.
			this.mu[i] = sum/count;
			this.sigma[i] = Math.sqrt(squares/count - this.mu[i]*this.mu[i]);
			this.sigma[i] = (this.sigma[i] > 15) ? 15 : this.sigma[i];
		}
		
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
