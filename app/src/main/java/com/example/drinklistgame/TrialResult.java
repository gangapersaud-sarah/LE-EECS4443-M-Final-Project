package com.example.drinklistgame;

public class TrialResult {
    private long completionTimeMs;
    private int errorCount;

    public TrialResult(long completionTimeMs, int errorCount) {
        this.completionTimeMs = completionTimeMs;
        this.errorCount = errorCount;
    }

    public long getCompletionTimeMs() {
        return completionTimeMs;
    }

    public int getErrorCount() {
        return errorCount;
    }
}