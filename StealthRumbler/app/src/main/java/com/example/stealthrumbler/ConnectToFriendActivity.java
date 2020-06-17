package com.example.stealthrumbler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobScheduler;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;


public class ConnectToFriendActivity extends AppCompatActivity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor sensor;
    private JobScheduler job;
    private Vibrator v;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_friend);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        v.vibrate(500);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
