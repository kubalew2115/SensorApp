package com.example.sensorapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private List<Sensor> deviceSensors;

    private TextView  tvLight, tvGyro, tvAccel, tvVector, tvMag, tvSteps, tvOrientation;
    private TextView tvLightLower, tvAccelLower, tvGyroLower, tvMagLower, tvLinearLower, tvGravLower, tvProximityLower, tvOrientationLower, tvSignificantLower, tvStepDetectorLower, tvStepCounterLower;

    private Sensor mLight, mGyro, mAccel, mVector, mMag, mSteps, mLinearAccel, mGravity, mProximity, mStepDetector, mSignificantMotion;
    private TriggerEventListener mTriggerEventListener;

    private float[] accelerometerReading = new float[3];
    private float[] magnetometerReading = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientationAngles = new float[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        tvLight = findViewById(R.id.tv_light);
        tvGyro = findViewById(R.id.tv_gyro);
        tvAccel = findViewById(R.id.tv_accel);
        tvVector = findViewById(R.id.tv_vector);
        tvMag = findViewById(R.id.tv_mag);
        tvSteps = findViewById(R.id.tv_steps);
        tvOrientation = findViewById(R.id.tv_orientation);

        tvLightLower = findViewById(R.id.tv_light_lower);
        tvAccelLower = findViewById(R.id.tv_accel_lower);
        tvGyroLower = findViewById(R.id.tv_gyro_lower);
        tvMagLower = findViewById(R.id.tv_mag_lower);
        tvLinearLower = findViewById(R.id.tv_linear_lower);
        tvGravLower = findViewById(R.id.tv_grav_lower);
        tvProximityLower = findViewById(R.id.tv_proximity_lower);
        tvOrientationLower = findViewById(R.id.tv_orientation_lower);
        tvSignificantLower = findViewById(R.id.tv_significant_lower);
        tvStepDetectorLower = findViewById(R.id.tv_step_detector_lower);
        tvStepCounterLower = findViewById(R.id.tv_step_counter_lower);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        deviceSensors = sensorManager.getSensorList(Sensor.TYPE_ALL);
        List<String> sensorNames = new ArrayList<>();
        for (Sensor s : deviceSensors) {
            sensorNames.add(s.getName());
            Log.v(Config.TAG, "-----------> " + s.getName());
        }

        ListView listView = findViewById(R.id.sensor_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sensorNames);
        listView.setAdapter(adapter);

        checkGravitySensors();
        checkSensorMagnetic();

        initSensors();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 100);
            }
        }
    }

    private void initSensors() {
        mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSteps = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        mLinearAccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mStepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        mSignificantMotion = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);

        if (mSignificantMotion != null) {
            mTriggerEventListener = new TriggerEventListener() {
                @Override
                public void onTrigger(TriggerEvent event) {
                    if (tvSignificantLower != null) tvSignificantLower.setText("Significant: Wykryto ruch!");
                    Log.v(Config.TAG, "Significant Motion Detected!");
                }
            };
        }
    }

    private void checkGravitySensors() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null) {
            List<Sensor> gravSensors = sensorManager.getSensorList(Sensor.TYPE_GRAVITY);
            for (Sensor s : gravSensors) {
                Log.v(Config.TAG, "Vendor: " + s.getVendor() + " name: " + s.getName() + "\n");
            }
        }
    }

    private void checkSensorMagnetic() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            Log.v(Config.TAG, "Success! There's a magnetometer. " + sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD).getName());
        } else {
            Log.v(Config.TAG, "Failure! No magnetometer.");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        String sValues = formatValues(event.values);

        if (type == Sensor.TYPE_LIGHT) {
            if (tvLight != null) tvLight.setText(String.format(Locale.getDefault(), "Light: %s", sValues));
            if (tvLightLower != null) tvLightLower.setText(String.format(Locale.getDefault(), "ŚWIATŁO: %s", sValues));
            Log.v(Config.TAG, "reading: " + sValues);
        } else if (type == Sensor.TYPE_GYROSCOPE) {
            if (tvGyro != null) tvGyro.setText(String.format(Locale.getDefault(), "Gyroscope: %d", (int)event.values[0]));
            if (tvGyroLower != null) tvGyroLower.setText(String.format(Locale.getDefault(), "ŻYROSKOP: %s", sValues));
        } else if (type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.length);
            updateOrientationAngles();
            if (tvAccel != null) tvAccel.setText(String.format(Locale.getDefault(), "Accelerometr: %d stopni", (int)Math.toDegrees(orientationAngles[1])));
            if (tvAccelLower != null) tvAccelLower.setText(String.format(Locale.getDefault(), "Akcelometr: %s", sValues));
        } else if (type == Sensor.TYPE_ROTATION_VECTOR) {
            if (tvVector != null) tvVector.setText(String.format(Locale.getDefault(), "Vector: %s", sValues));
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.length);
            updateOrientationAngles();
            if (tvMag != null) tvMag.setText(String.format(Locale.getDefault(), "Magnetometr: %d", (int)event.values[0]));
            if (tvMagLower != null) tvMagLower.setText(String.format(Locale.getDefault(), "Pole Magnetyczne: %s", sValues));
        } else if (type == Sensor.TYPE_STEP_COUNTER) {
            if (tvSteps != null) tvSteps.setText(String.format(Locale.getDefault(), "StepCounter: %d", (int)event.values[0]));
            if (tvStepCounterLower != null) tvStepCounterLower.setText(String.format(Locale.getDefault(), "Licznik kroków: %d", (int)event.values[0]));
        } else if (type == Sensor.TYPE_LINEAR_ACCELERATION) {
            if (tvLinearLower != null) tvLinearLower.setText(String.format(Locale.getDefault(), "PRZYŚPIESZENIE LINIOWE: %s", sValues));
        } else if (type == Sensor.TYPE_GRAVITY) {
            if (tvGravLower != null) tvGravLower.setText(String.format(Locale.getDefault(), "GRAWITACJA: %s", sValues));
        } else if (type == Sensor.TYPE_PROXIMITY) {
            if (tvProximityLower != null) tvProximityLower.setText(String.format(Locale.getDefault(), "Zbliżeniowy: %.1f", event.values[0]));
        } else if (type == Sensor.TYPE_STEP_DETECTOR) {
            if (tvStepDetectorLower != null) tvStepDetectorLower.setText("Czujnik kroków: Wykryto!");
        }
        
        if (tvOrientation != null) tvOrientation.setText(String.format(Locale.getDefault(), "Orientation: %d", (int)Math.toDegrees(orientationAngles[0])));
    }

    private String formatValues(float[] values) {
        StringBuilder sb = new StringBuilder();
        for (float v : values) {
            sb.append(String.format(Locale.getDefault(), "%.3f ", v));
        }
        return sb.toString().trim();
    }

    public void updateOrientationAngles() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        SensorManager.getOrientation(rotationMatrix, orientationAngles);
        
        float azimuth = (float) Math.toDegrees(orientationAngles[0]);
        float pitch = (float) Math.toDegrees(orientationAngles[1]);
        float roll = (float) Math.toDegrees(orientationAngles[2]);
        
        if (tvOrientationLower != null) tvOrientationLower.setText(String.format(Locale.getDefault(), "ORIENTACJA: %.1f, %.1f, %.1f", azimuth, pitch, roll));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        register(mLight);
        register(mGyro);
        register(mAccel);
        register(mVector);
        register(mMag);
        register(mSteps);
        register(mLinearAccel);
        register(mGravity);
        register(mProximity);
        register(mStepDetector);
        
        if (mSignificantMotion != null) {
            sensorManager.requestTriggerSensor(mTriggerEventListener, mSignificantMotion);
        }
    }

    private void register(Sensor s) {
        if (s != null) {
            sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initSensors();
            }
        }
    }
}
