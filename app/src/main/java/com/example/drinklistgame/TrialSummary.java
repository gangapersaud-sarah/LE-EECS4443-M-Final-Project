package com.example.drinklistgame;

public class TrialSummary {
    private double completionTimeSec;
    private double errorRate;

    public TrialSummary(double completionTimeSec, double errorRate) {
        this.completionTimeSec = completionTimeSec;
        this.errorRate = errorRate;
    }

    public double getCompletionTimeSec() {
        return completionTimeSec;
    }

    public double getErrorRate() {
        return errorRate;
    }
}