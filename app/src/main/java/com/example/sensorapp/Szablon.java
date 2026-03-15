package com.example.sensorapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Szablon extends AppCompatActivity implements SensorEventListener {

    private static final String TAG = "mysensors";
    TextView text_name;
    TextView text_readings;
    private SensorManager sensorManager;
    private Sensor mSensor;
    boolean isSensorPresent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szablon);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        text_name = findViewById(R.id.text_name);
        text_readings = findViewById(R.id.text_readings);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
            isSensorPresent = true;
            text_name.setText(mSensor.getName());
            Log.v(TAG, "detect ... gravity sensor");
        } else {
            text_name.setText("Sensor is not present");
            isSensorPresent = false;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_GRAVITY) {
            text_readings.setText(String.valueOf((int) sensorEvent.values[0]));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "---------------> onResume()");

        if (mSensor != null) {
            sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
            Log.v(TAG, "---------------> OK sensor registerListener");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensor != null) {
            sensorManager.unregisterListener(this, mSensor);
        }
        Log.v(TAG, "---------------> onPause()");
    }
}
