package com.pbp;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class GaitRecorder extends Activity implements SensorEventListener {
	
	private SensorManager sensorManager;
	
	TextView xCoord;
	TextView yCoord;
	TextView zCoord;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
		setContentView(R.layout.record);
		xCoord = (TextView) findViewById(R.id.xCoord);
		yCoord = (TextView) findViewById(R.id.yCoord);
		zCoord = (TextView) findViewById(R.id.zCoord);
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }
    
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
	}

	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];

			xCoord.setText("X: " + x);
			yCoord.setText("Y: " + y);
			zCoord.setText("Z: " + z);
		}
	}

}
