package com.aero.quickfix.quant.strategy;

import com.aero.quickfix.quant.model.OHLCVData;

import java.util.List;
import java.util.Map;

/**
 * Strategy Interface
 * Defines contract for all quantitative trading strategies
 */
public interface Strategy {

    /**
     * Generate trading signal based on price history and indicators
     * 
     * @param priceHistory Historical OHLCV data (minimum 20 bars)
     * @param indicators Map of indicator name to list of values (in chronological order)
     * @return Signal with action (BUY, SELL, HOLD) and confidence
     */
    Signal generateSignal(List<OHLCVData> priceHistory, Map<String, List<Double>> indicators);

    /**
     * Get strategy name
     */
    String getName();

    /**
     * Get strategy description
     */
    String getDescription();

    /**
     * Get required minimum bars for signal generation
     */
    int getMinimumBars();

    /**
     * Get list of indicator names required by this strategy
     */
    List<String> getRequiredIndicators();

    /**
     * Validate that strategy is properly configured
     */
    boolean isValid();
}
