package com.aero.quickfix.quant.strategy;

import java.io.Serializable;

/**
 * Signal
 * Represents a trading signal from a strategy
 */
public class Signal implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Action {
        BUY("BUY"),
        SELL("SELL"),
        HOLD("HOLD");

        private final String value;

        Action(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static Action fromString(String value) {
            for (Action action : Action.values()) {
                if (action.value.equalsIgnoreCase(value)) {
                    return action;
                }
            }
            return HOLD;
        }
    }

    private Action action;
    private double confidence; // 0.0 to 1.0
    private String reason; // Human-readable reason
    private long timestamp; // Signal generation time in millis

    public Signal() {
        this.action = Action.HOLD;
        this.confidence = 0.0;
        this.timestamp = System.currentTimeMillis();
    }

    public Signal(Action action, double confidence, String reason) {
        this();
        this.action = action;
        this.confidence = Math.max(0, Math.min(1, confidence)); // Clamp to 0-1
        this.reason = reason;
    }

    // Getters and Setters
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = Math.max(0, Math.min(1, confidence));
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isBuySignal() {
        return action == Action.BUY && confidence > 0.5;
    }

    public boolean isSellSignal() {
        return action == Action.SELL && confidence > 0.5;
    }

    public boolean isHoldSignal() {
        return action == Action.HOLD;
    }

    @Override
    public String toString() {
        return String.format("Signal{action=%s, confidence=%.2f, reason='%s'}", 
            action, confidence, reason);
    }
}
