package com.pbp;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PBPActivity extends Activity {
	
	// Used to hold the current login sequence
	private String login;
	private Timer timer;
	private ArrayList<Long> profile;
	
	/**
	 * Called at the start of the Activity, initializes the buttons and adds listeners
	 */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Initialize our login sequence to an empty string, initialize our timer, initialize the profile
        login = "";
        timer = new Timer();
        profile = new ArrayList<Long>();
        
        
        setContentView(R.layout.main);
        
        Button button1 = (Button) findViewById(R.id.button1);
        Button button2 = (Button) findViewById(R.id.button2);
        Button button3 = (Button) findViewById(R.id.button3);
        Button button4 = (Button) findViewById(R.id.button4);
        Button button5 = (Button) findViewById(R.id.button5);
        Button button6 = (Button) findViewById(R.id.button6);
        Button button7 = (Button) findViewById(R.id.button7);
        Button button8 = (Button) findViewById(R.id.button8);
        Button button9 = (Button) findViewById(R.id.button9);
        
        
        // Add all of the onClickListeners
        
        button1.setOnClickListener(new OnClickListener() {     	
			public void onClick(View v) {
				onClickInternal();
				login += "1";
			}
		});
        
        button2.setOnClickListener(new OnClickListener() {     	
			public void onClick(View v) {
				onClickInternal();
				login += "2";
			}
		});
        
        button3.setOnClickListener(new OnClickListener() {     	
			public void onClick(View v) {
				onClickInternal();
				login += "3";
			}
		});
        
        button4.setOnClickListener(new OnClickListener() {     	
			public void onClick(View v) {
				onClickInternal();
				login += "4";
			}
		});
        
        button5.setOnClickListener(new OnClickListener() {     	
			public void onClick(View v) {
				onClickInternal();
				login += "5";
			}
		});
        
        button6.setOnClickListener(new OnClickListener() {     	
			public void onClick(View v) {
				onClickInternal();
				login += "6";
			}
		});
        
        button7.setOnClickListener(new OnClickListener() {     	
			public void onClick(View v) {
				onClickInternal();
				login += "7";
			}
		});
        
        button8.setOnClickListener(new OnClickListener() {     	
			public void onClick(View v) {
				onClickInternal();
				login += "8";
			}
		});
        
        button9.setOnClickListener(new OnClickListener() {     	
			public void onClick(View v) {
				onClickInternal();
				login += "9";
			}
		});
        
        Button loginButton = (Button) findViewById(R.id.login); 
        
        loginButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				
				// Reset our variables
				login = "";
				profile.clear();
				timer = new Timer();
				
			}
		});
        
        // Set the colors for the buttons
        button1.getBackground().setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP));
        button3.getBackground().setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP));
        button5.getBackground().setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP));
        button7.getBackground().setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP));
        button9.getBackground().setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP));


    }
    
    public void onClickInternal() {
		timer.stop();
		if (timer.start != 0) {
			profile.add(timer.getTime());
		}
		timer.start();
    }
}