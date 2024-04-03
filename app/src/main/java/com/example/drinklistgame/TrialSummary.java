package com.example.drinklistgame;

public class TrialSummary {
    private double completionTimeSec;
    private double errorRate;

    private final String type;

    public TrialSummary(double completionTimeSec, double errorRate, String type) {
        this.completionTimeSec = completionTimeSec;
        this.errorRate = errorRate;
        this.type = type;
    }

    public double getCompletionTimeSec() {
        return completionTimeSec;
    }

    public double getErrorRate() {
        return errorRate;
    }

    public String getType(){return type;}
}