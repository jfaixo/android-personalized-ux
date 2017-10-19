package com.singular_dreams.pux.model;

public class PuxPrediction {

    private String predictedLabel;
    private double[] probabilities;

    public PuxPrediction(String predictedLabel, double[] probabilities) {
        this.setPredictedLabel(predictedLabel);
        this.setProbabilities(probabilities);
    }

    public String getPredictedLabel() {
        return predictedLabel;
    }

    public void setPredictedLabel(String predictedLabel) {
        this.predictedLabel = predictedLabel;
    }

    public double[] getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(double[] probabilities) {
        this.probabilities = probabilities;
    }
}
