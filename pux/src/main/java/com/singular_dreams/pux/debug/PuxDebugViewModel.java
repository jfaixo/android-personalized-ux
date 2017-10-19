package com.singular_dreams.pux.debug;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.singular_dreams.pux.BR;

public class PuxDebugViewModel extends BaseObservable {

    private PuxDebugFragment controller;

    private long updateDelay;
    private float[] orientationAngles = new float[3];
    private float[] speedVector = new float[3];
    private float distance;

    private String consoleText = "";
    private String predictedClass = "";

    private String C = "1";
    private String gamma = "0.3";

    @Bindable
    public float[] getSpeedVector() {
        return speedVector;
    }

    public void setSpeedVector(float[] speedVector) {
        this.speedVector = speedVector;
        notifyPropertyChanged(BR.speedVector);
    }

    @Bindable
    public String getC() {
        return C;
    }

    public void setC(String c) {
        C = c;
        notifyPropertyChanged(BR.c);
    }

    @Bindable
    public String getGamma() {
        return gamma;
    }

    public void setGamma(String gamma) {
        this.gamma = gamma;
        notifyPropertyChanged(BR.gamma);
    }

    @Bindable
    public String getPredictedClass() {
        return predictedClass;
    }

    public void setPredictedClass(String predictedClass) {
        this.predictedClass = "Predicted usage context : " + predictedClass;
        notifyPropertyChanged(BR.predictedClass);
    }

    @Bindable
    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
        notifyPropertyChanged(BR.distance);
    }

    public PuxDebugViewModel(PuxDebugFragment controller) {
        this.controller = controller;
    }

    @Bindable
    public String getConsoleText() {
        return consoleText;
    }

    public void setConsoleText(String consoleText) {
        this.consoleText += "\n" + consoleText;
        notifyPropertyChanged(BR.consoleText);
    }

    @Bindable
    public float[] getOrientationAngles() {
        return orientationAngles;
    }

    public void setOrientationAngles(float[] orientationAngles) {
        this.orientationAngles = orientationAngles;
        notifyPropertyChanged(BR.orientationAngles);
    }

    @Bindable
    public long getUpdateDelay() {
        return updateDelay;
    }

    public void setUpdateDelay(long updateDelay) {
        this.updateDelay = updateDelay;
        notifyPropertyChanged(BR.updateDelay);
    }

    public void onCollectClick(View view) {
        setConsoleText("Starting to collect data...");
        controller.collectData();
    }

    public void onCollectEnded(int dataPointCount) {
        setConsoleText("Data collection ended");
        setConsoleText("Collected data points number: " + dataPointCount);
    }

    public void onTrainClick(View view) {
        setConsoleText("Starting to train the classifier...");
        controller.train();
    }

    public void onTrainingEnded(int trainingTime) {
        setConsoleText("Training ended");
        setConsoleText("Training time: " + trainingTime);
    }
}
