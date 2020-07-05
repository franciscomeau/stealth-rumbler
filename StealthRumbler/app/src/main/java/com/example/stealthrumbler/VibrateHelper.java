package com.example.stealthrumbler;

public class VibrateHelper {
    private double baseSensitivity = 10.; //originally 9.
    private double sensitivity = baseSensitivity;
    private double stoppedVelocity = 9.6;
    private boolean reachedHighVelocity = false;
    private boolean hasReachedStop = false;


    public void VibrateHelper() {
    }

    public void setSensitivity(double sensitivity) {
        this.sensitivity = sensitivity;
    }

    public void setStoppedVelocity(double stoppedVelocity) {
        this.stoppedVelocity = stoppedVelocity;
    }

    public boolean vibrateAlgorithm(Double magnitude) {

        if (magnitude>=sensitivity) reachedHighVelocity = true;
        else if (reachedHighVelocity && magnitude <= stoppedVelocity) hasReachedStop = true;

        if (reachedHighVelocity && hasReachedStop) { //wait for movement to stop before vibrating
            reachedHighVelocity = false;
            hasReachedStop = false;
            return true; //previously 500
        }

        return false;
    }

    public double getSensitivity() {
        return this.sensitivity;
    }

    public double getStoppedVelocity() {
        return this.stoppedVelocity;
    }

    public void setBaseSensitivity(double baseSensitivity) {
        this.baseSensitivity = baseSensitivity;
    }

    public double getBaseSensitivity() {
        return this.baseSensitivity;
    }



}
