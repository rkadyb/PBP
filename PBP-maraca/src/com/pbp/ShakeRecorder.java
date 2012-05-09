package com.pbp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class ShakeRecorder extends Activity implements SensorEventListener {
	
	private SensorManager sensorManager;

	// Even at the slowest setting, the sensor is too fast for what we are trying to do
	private int count = 18;
	
	//private ArrayList<Float> accelProfile = new ArrayList<Float>();
	//private ArrayList<Long> timingProfile = new ArrayList<Long>();
	
	private boolean done;
	private boolean wait;
	private boolean access;
	
	TextView xCoord;
	TextView yCoord;
	TextView zCoord;
	TextView accessBox;
	
	TextView results;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		xCoord = (TextView) findViewById(R.id.xCoord);
		yCoord = (TextView) findViewById(R.id.yCoord);
		zCoord = (TextView) findViewById(R.id.zCoord);
		
		results = (TextView) findViewById(R.id.results);
		
		accessBox = (TextView) findViewById(R.id.access);
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI); //.SENSOR_DELAY_NORMAL);

		done = false;
		wait = false;
		
		// Clear out our variables
		PBPmaracaActivity.passwordInput = "";
		PBPmaracaActivity.timingProfile.clear();
    }
    
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	public void onSensorChanged(SensorEvent event) {
		if (wait) {
			count--;
			if (count == 0) {
				wait = false;
			}
		}
		if (!wait && 
			(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)) {

			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			xCoord.setText("X: " + x);
			yCoord.setText("Y: " + y);
			zCoord.setText("Z: " + z);
			
			if (!done && (Math.abs(y) > 4)) {
				wait = true;
				count = 15;
				Long timingValue = System.currentTimeMillis();
				
				if (PBPmaracaActivity.timingProfile.size() > 0) {
					timingValue -= PBPmaracaActivity.timingProfile.get(0);
				}
				
				PBPmaracaActivity.timingProfile.add(timingValue);
				
				String value = (y > 0) ? "1" : "0";
				PBPmaracaActivity.passwordInput += value;
			}
			
			if (PBPmaracaActivity.timingProfile.size() == 5) {
				done = true;
				PBPmaracaActivity.timingProfile.set(0, 0L);
			}
			
			results.setText(PBPmaracaActivity.timingProfile.toString() +"\n"+ PBPmaracaActivity.passwordInput);
			
		}
		
		if (done) {
    		// First time, no password set yet
    		if (PBPmaracaActivity.password == null) {
    			PBPmaracaActivity.password = new Password(PBPmaracaActivity.login, PBPmaracaActivity.passwordInput, PBPmaracaActivity.timingProfile);
    		} else {
    			// Continue the initialization process
    			if (!PBPmaracaActivity.password.isInitialized()) {
    				PBPmaracaActivity.password.initialize(PBPmaracaActivity.login, PBPmaracaActivity.passwordInput, PBPmaracaActivity.timingProfile);
    			} else {
    				access = PBPmaracaActivity.password.check(PBPmaracaActivity.login, PBPmaracaActivity.passwordInput, PBPmaracaActivity.timingProfile);
    				accessBox.setText(String.valueOf(access));
    			}
    		}
		}
	}

}
