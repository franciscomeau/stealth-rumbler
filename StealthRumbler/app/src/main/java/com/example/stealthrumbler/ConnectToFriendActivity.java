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
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.Toast;

import static android.content.Context.VIBRATOR_SERVICE;


public class ConnectToFriendActivity extends AppCompatActivity implements SensorEventListener {


    private SensorManager sensorManager;
    private Sensor sensor;
    private JobScheduler job;
    private Vibrator v;
    private double ax, ay, az, magnitude;   // acceleration in x, y, and z axis' as well as the magnitude of the vector
    static private double baseSensitivity = 10.; //originally 9.
    static private final double sensitivityFactor = 0.9;
    private double sensitivity;
    static private double stoppedVelocity = 9.6;
    private SeekBar sensitivitySeekBar;
    private Switch onOffSwitch;
    private boolean isOn = false;
    private boolean reachedHighVelocity = false;
    private boolean hasReachedStop = false;
    private boolean isCalibrating = false;

    //calibration variables
    private double peak1 = -1., peak2 = -1., peak3 = -1., low1 = -1., low2 = -1., low3 = -1.;


    public ConnectToFriendActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_friend);

        PackageManager pm = this.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
            Toast.makeText(this, "Has sensor accelerometer!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "No sensor accelerometer!", Toast.LENGTH_LONG).show();
        }

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        //sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        sensitivity = baseSensitivity;
        sensitivitySeekBar = (SeekBar) findViewById(R.id.sensitivitySeekBar);
        sensitivitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sensitivity = baseSensitivity - (((progress - 50) / 100.) * sensitivityFactor);
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        onOffSwitch = (Switch) findViewById(R.id.onOffSwitch);
        onOffSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isOn = true;
                } else {
                    isOn = false;
                }
            }
        });
    }



    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType()!=Sensor.TYPE_ACCELEROMETER) return; //we are only looking at the accelerometer

        ax = event.values[0];
        ay = event.values[1];
        az = event.values[2];
        magnitude = Math.sqrt(
                Math.pow(ax, 2.) + Math.pow(ay, 2.) + Math.pow(az, 2.)
        );
        System.out.println("Magnitude is: "+magnitude);

        if (isCalibrating) {
            calibrate(magnitude);
            return;
        }

        if (shouldVibrate(magnitude)) v.vibrate(250); //previously 500
    }

    private void calibrate(Double magnitude) {
        if (peak1==-1.) {
            peak1 = magnitude;
            return;
        }

        if (low1==-1.) {
            if (magnitude >= peak1) peak1 = magnitude;
            else low1 = magnitude;
            return;
        }

        if (peak2==-1.) {
            if (magnitude <= low1) low1 = magnitude;
            else peak2 = magnitude;
            return;
        }

        if (low2==-1.) {
            if (magnitude >= peak2) peak2 = magnitude;
            else low2 = magnitude;
            return;
        }

        if (peak3==-1.) {
            if (magnitude <= low3) low3 = magnitude;
            else peak3 = magnitude;
            return;
        }

        if (low3==-1.) {
            if (magnitude >= peak3) peak3 = magnitude;
            else low3 = magnitude;
            return;
        }

        if (magnitude<low3) low3 = magnitude; //further calibration of low3
        else {
            baseSensitivity = (peak1+peak2+peak3)/3.;
            stoppedVelocity = (low1+low2+low3)/3.;

            isCalibrating = false;
            Toast.makeText(this, "Calibration finished. Please press the calibrate button again if unsatisfied", Toast.LENGTH_LONG).show();
        }

        return;
    }

    private boolean shouldVibrate(Double magnitude) {

        if (!isOn) return false;

        if (magnitude>=sensitivity) reachedHighVelocity = true;
        else if (reachedHighVelocity && magnitude <= stoppedVelocity) hasReachedStop = true;

        if (reachedHighVelocity && hasReachedStop) { //wait for movement to stop before vibrating
            reachedHighVelocity = false;
            hasReachedStop = false;
            return true; //previously 500
        }

        return false;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void initiateCalibration(View view) {
        peak1 = -1.; peak2 = -1.; peak3 = -1.; low1 = -1.; low2 = -1.; low3 = -1.; //reset calibration values;
        isCalibrating = true;
        Toast.makeText(this, "Now starting calibration. Please make 3 similar movements", Toast.LENGTH_LONG).show();
    }
}
