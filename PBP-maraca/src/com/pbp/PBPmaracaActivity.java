package com.pbp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class PBPmaracaActivity extends Activity {
	
	public static String passwordInput;
	public static String login = "rkd";
	public static Password password = null;
	public static ArrayList<Long> timingProfile = new ArrayList<Long>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button trainButton = (Button) findViewById(R.id.train);
        
        trainButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Toast toast = Toast.makeText(getApplicationContext(), "Train", Toast.LENGTH_SHORT);
		    	toast.show();
			}
		});
        
        Button recordButton = (Button) findViewById(R.id.record);
        
        recordButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// Start a new Shake Recorder
				Intent intent = new Intent(getApplicationContext(), ShakeRecorder.class);
				startActivity(intent);
			}
		});
        
        Button resetAllButton = (Button) findViewById(R.id.resetAll);
        
        resetAllButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Toast toast = Toast.makeText(getApplicationContext(), "All Things Reset", Toast.LENGTH_SHORT);
		    	toast.show();
			}
		});
    }
}