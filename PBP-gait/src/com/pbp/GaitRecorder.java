package com.pbp;

import java.util.ArrayList;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class GaitRecorder extends Activity implements SensorEventListener {
	
	private SensorManager sensorManager;
	private float minY;
	private float maxY;
	private long start;
	private boolean started;
	private boolean initialized;
	
	private ArrayList<Long> timingProfile = new ArrayList<Long>();
	private Timer timer = new Timer();
	
	private boolean done;
	
	TextView xCoord;
	TextView yCoord;
	TextView zCoord;
	
	TextView yMin;
	TextView yMax;
	
	TextView results;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		xCoord = (TextView) findViewById(R.id.xCoord);
		yCoord = (TextView) findViewById(R.id.yCoord);
		zCoord = (TextView) findViewById(R.id.zCoord);
		
		yMin = (TextView) findViewById(R.id.yMin);
		yMax = (TextView) findViewById(R.id.yMax);
		
		results = (TextView) findViewById(R.id.results);
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION), SensorManager.SENSOR_DELAY_NORMAL);
		minY = 0;
		maxY = 0;
		
		initialized = false;
		started = false;
		done = false;
    }
    
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			xCoord.setText("X: " + x);
			yCoord.setText("Y: " + y);
			zCoord.setText("Z: " + z);
			
			if (y < minY) {
				minY = y;
			}
			
			if (y > maxY) {
				maxY = y;
			}
			
			yMin.setText("Y Min: " + minY);
			yMax.setText("Y Max: " + maxY);

			// Give an initialization period until we have good min and max values
			if ((minY < -2.5) && (maxY > 2.5) && !initialized) {
				if (started) {
					if (System.currentTimeMillis() - start > 5000) {
						initialized = true;
						timer.start();
					}
				} else {
					start = System.currentTimeMillis();
					started = true;
				}
			}
			
			// Once we have initialized good min and max values
			
			System.out.println(timer.getTime());
			
			if (initialized && !done) {
				if (Math.abs(maxY - y) < 0.5) {
					timingProfile.add(timer.getTime());
					results.setText(timingProfile.toString());
				}
			}
			
			if (timingProfile.size() == 5) {
				done = true;
				results.setText(timingProfile.toString());
			}
			
		}
	}

}
