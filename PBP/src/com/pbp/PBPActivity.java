package com.pbp;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class PBPActivity extends Activity {
	
	// Used to hold the current login sequence
	private String passwordInput;
	private String login = "rkd";
	private Timer timer;
	private ArrayList<Long> profile;
	private Password password = null;
	
	// Used to block all but the first move event across a button,
	// resets when we move off to the next button		
	private int blocked = 0;
	
	/**
	 * Called at the start of the Activity, initializes the buttons and adds listeners
	 */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize our login sequence to an empty string, initialize our timer, initialize the profile
        passwordInput = "";
        timer = new Timer();
        profile = new ArrayList<Long>();
        
        
        setContentView(R.layout.main);
        
        // Assign the reset button
        Button resetButton = (Button) findViewById(R.id.reset);
        
        resetButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				password = null;
				passwordInput = "";
				profile.clear();
				
				Toast toast = Toast.makeText(getApplicationContext(), "Password Reset", Toast.LENGTH_SHORT);
		    	toast.show();
			}
		});
        
        ImageButton main = (ImageButton) findViewById(R.id.main);
        
        main.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				int action = event.getAction();
				
				//Start of swipe
				if (action == MotionEvent.ACTION_DOWN) {
					System.out.println("Started");
					timer.start();				
				}
				// End of swipe
				else if (action == MotionEvent.ACTION_UP) {
					System.out.println("Ended");
					timer.stop();
					login();
				}
				
				// During swipe
				else if (action == MotionEvent.ACTION_MOVE) {
					//System.out.println("X: "+event.getX());
					//System.out.println("Y: "+event.getY());
					
					float X = event.getX();
					float Y = event.getY();
					
					// Area 1
					if (X>38 && X<110 && Y>38 && Y<106 && blocked!=1) {

						System.out.println("Swiped 1");
						blocked = 1;
						profile.add(System.currentTimeMillis());
						passwordInput += "1";
					}
					
					// Area 2
					else if (X>205 && X<275 && Y>35 && Y<108 && blocked!=2) {

						System.out.println("Swiped 2");
						blocked = 2;
						profile.add(System.currentTimeMillis());
						passwordInput += "2";
					}
					
					// Area 3
					else if (X>376 && X<451 && Y>35 && Y<106 && blocked!=3) {

						System.out.println("Swiped 3");
						blocked = 3;
						profile.add(System.currentTimeMillis());
						passwordInput += "3";
					}
					
					// Area 4
					else if (X>48 && X<110 && Y>199 && Y<267 && blocked!=4) {

						System.out.println("Swiped 4");
						blocked = 4;
						profile.add(System.currentTimeMillis());
						passwordInput += "4";
					}
					
					// Area 5
					else if (X>199 && X<271 && Y>199 && Y<267 && blocked!=5) {

						System.out.println("Swiped 5");
						blocked = 5;
						profile.add(System.currentTimeMillis());
						passwordInput += "5";
					}
					
					// Area 6
					else if (X>376 && X<446 && Y>199 && Y<267 && blocked!=6) {

						System.out.println("Swiped 6");
						blocked = 6;
						profile.add(System.currentTimeMillis());
						passwordInput += "6";
					}
					
					// Area 7
					else if (X>37 && X<102 && Y>370 && Y<445 && blocked!=7) {

						System.out.println("Swiped 7");
						blocked = 7;
						profile.add(System.currentTimeMillis());
						passwordInput += "7";
					}
					
					// Area 8
					else if (X>219 && X<268 && Y>370 && Y<445 && blocked!=8) {

						System.out.println("Swiped 8");
						blocked = 8;
						profile.add(System.currentTimeMillis());
						passwordInput += "8";
					}
					
					// Area 9
					else if (X>380 && X<447 && Y>370 && Y<445 && blocked!=9) {

						System.out.println("Swiped 9");
						blocked = 9;
						profile.add(System.currentTimeMillis());
						passwordInput += "9";
					}				
				}
				return false;
			}
		});

        
    }
        
    
    public void login() {
    	boolean access = false;
		
    	if (passwordInput != "") {
    		
    		// Remove the first time point
    		profile.remove(0);
    		
    		// Normalize the remaining time points
    		for (int i=0;i<profile.size();i++) {
    			profile.set(i, profile.get(i) - timer.start);
    		}
    		
    		// First time, no password set yet
    		if (password == null) {
    			password = new Password(login, passwordInput, profile);
    		} else {
    			// Continue the initialization process
    			if (!password.isInitialized()) {
    				password.initialize(login, passwordInput, profile);
    			} else {
    				access = password.check(login, passwordInput, profile);
    			}
    		}
    	}
		
		// DEBUG
		TextView debug = (TextView) findViewById(R.id.debug);
		debug.setText(passwordInput+" "+profile.toString()+"\n"+"Granted: "+access+" init: "+password.isInitialized());
		
		// Reset our variables
		passwordInput = "";
		profile.clear();
		blocked = 0;
		
		// Handle login success/failure
		login(access);
    }
    
    
    public void login(boolean access) {
    	
    	String text = "Access Denied";
    	
    	if (access) {
    		text = "Access Granted";
    	}
    	
    	Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG);
    	toast.show();
    }
    
}