package com.pbp;

import java.util.ArrayList;

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
	private int skip = 9;
	private int count = 0;
	
	private ArrayList<Float> accelProfile = new ArrayList<Float>();
	private ArrayList<Long> timingProfile = new ArrayList<Long>();
	
	private boolean done;
	
	TextView xCoord;
	TextView yCoord;
	TextView zCoord;
	
	TextView results;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		xCoord = (TextView) findViewById(R.id.xCoord);
		yCoord = (TextView) findViewById(R.id.yCoord);
		zCoord = (TextView) findViewById(R.id.zCoord);
		
		results = (TextView) findViewById(R.id.results);
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_UI); //.SENSOR_DELAY_NORMAL);

		done = false;
    }
    
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	public void onSensorChanged(SensorEvent event) {
		count++;
		if ((count % skip == 0) && 
			(event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)) {

			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			xCoord.setText("X: " + x);
			yCoord.setText("Y: " + y);
			zCoord.setText("Z: " + z);
			
			if (!done && (Math.abs(y) > 4)) {
				Long timingValue = System.currentTimeMillis();
				
				if (timingProfile.size() > 0) {
					timingValue -= timingProfile.get(0);
				}
				
				timingProfile.add(timingValue);
				accelProfile.add(y);
			}
			
			if (timingProfile.size() == 10) {
				done = true;
				timingProfile.set(0, 0L);
			}
			
			results.setText(timingProfile.toString() +"\n"+ accelProfile.toString());
			
		}
	}

}
