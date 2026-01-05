package com.aero.quickfix.quant.strategy.impl;

import com.aero.quickfix.quant.model.OHLCVData;
import com.aero.quickfix.quant.strategy.BaseStrategy;
import com.aero.quickfix.quant.strategy.Signal;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * SMA Crossover Strategy
 * A classic moving average crossover strategy
 * 
 * Logic:
 * - BUY when SMA50 crosses above SMA200
 * - SELL when SMA50 crosses below SMA200
 * - Confidence increases with price distance from moving averages
 */
@Component
public class SmaCrossoverStrategy extends BaseStrategy {

    private static final int SMA_FAST = 50;
    private static final int SMA_SLOW = 200;
    private static final String INDICATOR_SMA_50 = "SMA_50";
    private static final String INDICATOR_SMA_200 = "SMA_200";

    public SmaCrossoverStrategy() {
        super(
            "SMA Crossover",
            "Simple Moving Average Crossover Strategy - BUY on SMA50 > SMA200 crossover, SELL on crossover below",
            201, // Minimum 201 bars to calculate SMA200
            Arrays.asList(INDICATOR_SMA_50, INDICATOR_SMA_200)
        );
    }

    @Override
    public Signal generateSignal(List<OHLCVData> priceHistory, Map<String, List<Double>> indicators) {
        
        // Validate input
        if (!validateMinimumBars(priceHistory)) {
            return createHoldSignal("Insufficient price history");
        }

        if (!validateIndicators(indicators)) {
            return createHoldSignal("Missing required indicators");
        }

        // Get indicator lists
        List<Double> sma50Values = indicators.get(INDICATOR_SMA_50);
        List<Double> sma200Values = indicators.get(INDICATOR_SMA_200);

        // Validate indicator lists have sufficient data
        if (sma50Values == null || sma50Values.size() < 2 || 
            sma200Values == null || sma200Values.size() < 2) {
            return createHoldSignal("Indicators not fully populated");
        }

        // Get current and previous values
        double currentSMA50 = getLatestIndicatorValue(indicators, INDICATOR_SMA_50);
        double currentSMA200 = getLatestIndicatorValue(indicators, INDICATOR_SMA_200);
        double previousSMA50 = getPreviousIndicatorValue(indicators, INDICATOR_SMA_50, 2);
        double previousSMA200 = getPreviousIndicatorValue(indicators, INDICATOR_SMA_200, 2);
        double currentPrice = getLatestClose(priceHistory);

        // Check for crossover conditions
        if (previousSMA50 <= previousSMA200 && currentSMA50 > currentSMA200) {
            // BUY signal: SMA50 crossed above SMA200
            double priceAboveSMA = (currentPrice - currentSMA200) / currentSMA200;
            double confidence = Math.min(0.9, 0.5 + (priceAboveSMA * 2)); // Increase confidence with distance
            
            String reason = String.format(
                "SMA50 (%.2f) crossed above SMA200 (%.2f). Price at %.2f",
                currentSMA50, currentSMA200, currentPrice
            );
            
            logger.info("SMA Crossover Strategy - BUY signal: {}", reason);
            return createBuySignal(Math.max(0.5, confidence), reason);
        }

        if (previousSMA50 >= previousSMA200 && currentSMA50 < currentSMA200) {
            // SELL signal: SMA50 crossed below SMA200
            double priceBelowSMA = (currentSMA200 - currentPrice) / currentSMA200;
            double confidence = Math.min(0.9, 0.5 + (priceBelowSMA * 2)); // Increase confidence with distance
            
            String reason = String.format(
                "SMA50 (%.2f) crossed below SMA200 (%.2f). Price at %.2f",
                currentSMA50, currentSMA200, currentPrice
            );
            
            logger.info("SMA Crossover Strategy - SELL signal: {}", reason);
            return createSellSignal(Math.max(0.5, confidence), reason);
        }

        // No crossover: HOLD
        String trend = currentSMA50 > currentSMA200 ? "UPTREND" : "DOWNTREND";
        String reason = String.format(
            "In %s - SMA50 (%.2f) %s SMA200 (%.2f). No crossover",
            trend, currentSMA50, currentSMA50 > currentSMA200 ? ">" : "<", currentSMA200
        );
        
        return createHoldSignal(reason);
    }

    /**
     * Validate strategy parameters
     */
    public boolean validateParameters() {
        return SMA_FAST > 0 && SMA_SLOW > SMA_FAST && 
               INDICATOR_SMA_50 != null && INDICATOR_SMA_200 != null;
    }

    /**
     * Get fast SMA period
     */
    public int getFastPeriod() {
        return SMA_FAST;
    }

    /**
     * Get slow SMA period
     */
    public int getSlowPeriod() {
        return SMA_SLOW;
    }
}
