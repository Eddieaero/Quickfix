package com.aero.quickfix.quant.strategy;

import com.aero.quickfix.quant.model.OHLCVData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * BaseStrategy
 * Abstract base class for all quantitative trading strategies
 * Provides common functionality and utilities
 */
public abstract class BaseStrategy implements Strategy {

    protected static final Logger logger = LoggerFactory.getLogger(BaseStrategy.class);

    protected String name;
    protected String description;
    protected int minimumBars;
    protected List<String> requiredIndicators;

    public BaseStrategy(String name, String description, int minimumBars, List<String> requiredIndicators) {
        this.name = name;
        this.description = description;
        this.minimumBars = minimumBars;
        this.requiredIndicators = new ArrayList<>(requiredIndicators);
    }

    /**
     * Abstract method to be implemented by concrete strategies
     */
    @Override
    public abstract Signal generateSignal(List<OHLCVData> priceHistory, Map<String, List<Double>> indicators);

    /**
     * Helper method to validate minimum bars requirement
     */
    protected boolean validateMinimumBars(List<OHLCVData> priceHistory) {
        if (priceHistory == null || priceHistory.size() < minimumBars) {
            logger.warn("Strategy {} requires minimum {} bars, but got {}", 
                name, minimumBars, priceHistory == null ? 0 : priceHistory.size());
            return false;
        }
        return true;
    }

    /**
     * Helper method to validate required indicators are present
     */
    protected boolean validateIndicators(Map<String, List<Double>> indicators) {
        if (indicators == null) {
            logger.warn("Strategy {} has no indicators", name);
            return false;
        }

        for (String required : requiredIndicators) {
            if (!indicators.containsKey(required) || indicators.get(required) == null || indicators.get(required).isEmpty()) {
                logger.warn("Strategy {} missing required indicator: {}", name, required);
                return false;
            }
        }
        return true;
    }

    /**
     * Get the latest value of an indicator
     */
    protected Double getLatestIndicatorValue(Map<String, List<Double>> indicators, String indicatorName) {
        List<Double> values = indicators.get(indicatorName);
        if (values == null || values.isEmpty()) {
            return null;
        }
        return values.get(values.size() - 1);
    }

    /**
     * Get the previous value of an indicator
     */
    protected Double getPreviousIndicatorValue(Map<String, List<Double>> indicators, String indicatorName, int barsBack) {
        List<Double> values = indicators.get(indicatorName);
        if (values == null || values.size() < barsBack) {
            return null;
        }
        return values.get(values.size() - barsBack);
    }

    /**
     * Get latest close price
     */
    protected double getLatestClose(List<OHLCVData> priceHistory) {
        if (priceHistory == null || priceHistory.isEmpty()) {
            return 0.0;
        }
        return priceHistory.get(priceHistory.size() - 1).getClose().doubleValue();
    }

    /**
     * Get previous close price
     */
    protected double getPreviousClose(List<OHLCVData> priceHistory, int barsBack) {
        if (priceHistory == null || priceHistory.size() < barsBack) {
            return 0.0;
        }
        return priceHistory.get(priceHistory.size() - barsBack).getClose().doubleValue();
    }

    /**
     * Get latest high price
     */
    protected double getLatestHigh(List<OHLCVData> priceHistory) {
        if (priceHistory == null || priceHistory.isEmpty()) {
            return 0.0;
        }
        return priceHistory.get(priceHistory.size() - 1).getHigh().doubleValue();
    }

    /**
     * Get latest low price
     */
    protected double getLatestLow(List<OHLCVData> priceHistory) {
        if (priceHistory == null || priceHistory.isEmpty()) {
            return 0.0;
        }
        return priceHistory.get(priceHistory.size() - 1).getLow().doubleValue();
    }

    /**
     * Check if indicator crossed above threshold
     */
    protected boolean crossedAbove(List<Double> indicators, double threshold) {
        if (indicators == null || indicators.size() < 2) {
            return false;
        }

        int size = indicators.size();
        double previous = indicators.get(size - 2);
        double current = indicators.get(size - 1);

        return previous <= threshold && current > threshold;
    }

    /**
     * Check if indicator crossed below threshold
     */
    protected boolean crossedBelow(List<Double> indicators, double threshold) {
        if (indicators == null || indicators.size() < 2) {
            return false;
        }

        int size = indicators.size();
        double previous = indicators.get(size - 2);
        double current = indicators.get(size - 1);

        return previous >= threshold && current < threshold;
    }

    /**
     * Check if one indicator crossed above another
     */
    protected boolean crossedAbove(List<Double> indicator1, List<Double> indicator2) {
        if (indicator1 == null || indicator2 == null || indicator1.size() < 2 || indicator2.size() < 2) {
            return false;
        }

        int size = indicator1.size();
        double prev1 = indicator1.get(size - 2);
        double curr1 = indicator1.get(size - 1);
        double prev2 = indicator2.get(size - 2);
        double curr2 = indicator2.get(size - 1);

        return prev1 <= prev2 && curr1 > curr2;
    }

    /**
     * Check if one indicator crossed below another
     */
    protected boolean crossedBelow(List<Double> indicator1, List<Double> indicator2) {
        if (indicator1 == null || indicator2 == null || indicator1.size() < 2 || indicator2.size() < 2) {
            return false;
        }

        int size = indicator1.size();
        double prev1 = indicator1.get(size - 2);
        double curr1 = indicator1.get(size - 1);
        double prev2 = indicator2.get(size - 2);
        double curr2 = indicator2.get(size - 1);

        return prev1 >= prev2 && curr1 < curr2;
    }

    /**
     * Create a BUY signal
     */
    protected Signal createBuySignal(double confidence, String reason) {
        return new Signal(Signal.Action.BUY, confidence, reason);
    }

    /**
     * Create a SELL signal
     */
    protected Signal createSellSignal(double confidence, String reason) {
        return new Signal(Signal.Action.SELL, confidence, reason);
    }

    /**
     * Create a HOLD signal
     */
    protected Signal createHoldSignal(String reason) {
        return new Signal(Signal.Action.HOLD, 0.0, reason);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int getMinimumBars() {
        return minimumBars;
    }

    @Override
    public List<String> getRequiredIndicators() {
        return new ArrayList<>(requiredIndicators);
    }

    @Override
    public boolean isValid() {
        return name != null && !name.isEmpty() &&
               description != null && !description.isEmpty() &&
               minimumBars > 0 &&
               requiredIndicators != null && !requiredIndicators.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("Strategy{name='%s', description='%s', minimumBars=%d, requiredIndicators=%s}",
            name, description, minimumBars, requiredIndicators);
    }
}
