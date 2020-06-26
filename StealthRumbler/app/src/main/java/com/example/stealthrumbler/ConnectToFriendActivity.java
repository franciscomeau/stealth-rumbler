package com.example.stealthrumbler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobScheduler;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.SeekBar;
import android.widget.Toast;

import static android.content.Context.VIBRATOR_SERVICE;


public class ConnectToFriendActivity extends AppCompatActivity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor sensor;
    private JobScheduler job;
    private Vibrator v;
    private double ax,ay,az;   // these are the acceleration in x,y and z axis
    static private final double baseSensitivity = 9.;
    static private final double sensitivityFactor = 0.9;
    private double sensitivity;
    private SeekBar sensitivitySeekBar;

    public ConnectToFriendActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_friend);

        PackageManager pm = this.getPackageManager();
        if(pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            Toast.makeText(this, "Has sensor accelerometer!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No sensor accelerometer!", Toast.LENGTH_LONG).show();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        //sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        sensitivity = baseSensitivity;
        sensitivitySeekBar = (SeekBar)findViewById(R.id.sensitivitySeekBar);
        sensitivitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              sensitivity = baseSensitivity - (((progress-50)/100.) * sensitivityFactor);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }



    @Override
    public void onSensorChanged(SensorEvent event) {
        //v.vibrate(500);
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];


            if (Math.max(Math.max(ax,ay),az)>=sensitivity) {
                v.vibrate(500);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
