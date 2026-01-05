package com.aero.quickfix.quant.indicators;

import java.util.*;
import org.springframework.stereotype.Component;

/**
 * Core IndicatorCalculator for technical analysis indicators.
 * Provides moving averages, momentum indicators, and volatility measures.
 */
@Component
public class IndicatorCalculator {

    /**
     * Calculate Simple Moving Average (SMA)
     */
    public static List<Double> calculateSMA(List<Double> prices, int period) {
        if (prices.size() < period) {
            throw new IllegalArgumentException("Insufficient data for SMA calculation");
        }

        List<Double> sma = new ArrayList<>();
        
        for (int i = 0; i < prices.size(); i++) {
            if (i < period - 1) {
                sma.add(null); // Not enough data
            } else {
                double sum = 0;
                for (int j = i - period + 1; j <= i; j++) {
                    sum += prices.get(j);
                }
                sma.add(sum / period);
            }
        }
        return sma;
    }

    /**
     * Calculate Exponential Moving Average (EMA)
     * More weight to recent prices
     */
    public static List<Double> calculateEMA(List<Double> prices, int period) {
        if (prices.size() < period) {
            throw new IllegalArgumentException("Insufficient data for EMA calculation");
        }

        List<Double> ema = new ArrayList<>();
        double multiplier = 2.0 / (period + 1);

        // First EMA is SMA
        double sum = 0;
        for (int i = 0; i < period; i++) {
            sum += prices.get(i);
        }
        double firstEMA = sum / period;
        ema.add(firstEMA);

        // Calculate subsequent EMAs
        for (int i = period; i < prices.size(); i++) {
            double currentEMA = (prices.get(i) - firstEMA) * multiplier + firstEMA;
            ema.add(currentEMA);
            firstEMA = currentEMA;
        }

        return ema;
    }

    /**
     * Calculate Relative Strength Index (RSI)
     * Momentum oscillator measuring speed and change of price movements
     * Range: 0-100 (typically 30 = oversold, 70 = overbought)
     */
    public static List<Double> calculateRSI(List<Double> prices, int period) {
        if (prices.size() < period + 1) {
            throw new IllegalArgumentException("Insufficient data for RSI calculation");
        }

        List<Double> rsi = new ArrayList<>();
        List<Double> gains = new ArrayList<>();
        List<Double> losses = new ArrayList<>();

        // Calculate price changes
        for (int i = 1; i < prices.size(); i++) {
            double change = prices.get(i) - prices.get(i - 1);
            gains.add(Math.max(0, change));
            losses.add(Math.max(0, -change));
        }

        // Calculate first average gain and loss
        double avgGain = gains.stream().limit(period).mapToDouble(Double::doubleValue).average().orElse(0);
        double avgLoss = losses.stream().limit(period).mapToDouble(Double::doubleValue).average().orElse(0);

        // Add nulls for insufficient data
        for (int i = 0; i < period; i++) {
            rsi.add(null);
        }

        // Calculate RSI
        for (int i = period; i < gains.size(); i++) {
            avgGain = (avgGain * (period - 1) + gains.get(i)) / period;
            avgLoss = (avgLoss * (period - 1) + losses.get(i)) / period;

            double rs = avgLoss == 0 ? 100 : avgGain / avgLoss;
            double rsiValue = 100 - (100 / (1 + rs));
            rsi.add(rsiValue);
        }

        return rsi;
    }

    /**
     * Calculate MACD (Moving Average Convergence Divergence)
     * Returns [MACD line, Signal line, Histogram]
     */
    public static Map<String, List<Double>> calculateMACD(List<Double> prices, int fast, int slow, int signal) {
        List<Double> ema12 = calculateEMA(prices, fast);
        List<Double> ema26 = calculateEMA(prices, slow);

        List<Double> macdLine = new ArrayList<>();
        for (int i = 0; i < prices.size(); i++) {
            if (ema12.get(i) != null && ema26.get(i) != null) {
                macdLine.add(ema12.get(i) - ema26.get(i));
            } else {
                macdLine.add(null);
            }
        }

        List<Double> signalLine = calculateEMA(
            macdLine.stream().filter(Objects::nonNull).toList(),
            signal
        );

        List<Double> histogram = new ArrayList<>();
        for (int i = 0; i < macdLine.size(); i++) {
            if (macdLine.get(i) != null && i - (slow - 1) >= 0 && i - (slow - 1) < signalLine.size()) {
                Double signal_value = signalLine.get(i - (slow - 1));
                if (signal_value != null) {
                    histogram.add(macdLine.get(i) - signal_value);
                } else {
                    histogram.add(null);
                }
            } else {
                histogram.add(null);
            }
        }

        Map<String, List<Double>> result = new HashMap<>();
        result.put("macd", macdLine);
        result.put("signal", signalLine);
        result.put("histogram", histogram);
        return result;
    }

    /**
     * Calculate Bollinger Bands
     * Returns [Upper Band, Middle Band (SMA), Lower Band]
     */
    public static Map<String, List<Double>> calculateBollingerBands(List<Double> prices, int period, double stdDevs) {
        List<Double> sma = calculateSMA(prices, period);
        List<Double> upperBand = new ArrayList<>();
        List<Double> lowerBand = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {
            if (i < period - 1) {
                upperBand.add(null);
                lowerBand.add(null);
            } else {
                // Calculate standard deviation
                double mean = sma.get(i);
                double variance = 0;
                for (int j = i - period + 1; j <= i; j++) {
                    variance += Math.pow(prices.get(j) - mean, 2);
                }
                double stdDev = Math.sqrt(variance / period);

                upperBand.add(mean + (stdDevs * stdDev));
                lowerBand.add(mean - (stdDevs * stdDev));
            }
        }

        Map<String, List<Double>> result = new HashMap<>();
        result.put("upper", upperBand);
        result.put("middle", sma);
        result.put("lower", lowerBand);
        return result;
    }

    /**
     * Calculate Average True Range (ATR)
     * Volatility indicator
     */
    public static List<Double> calculateATR(List<Double> highs, List<Double> lows, List<Double> closes, int period) {
        if (highs.size() < period || lows.size() < period || closes.size() < period) {
            throw new IllegalArgumentException("Insufficient data for ATR calculation");
        }

        List<Double> trueRanges = new ArrayList<>();

        // Calculate True Range for each period
        for (int i = 0; i < highs.size(); i++) {
            double tr;
            if (i == 0) {
                tr = highs.get(i) - lows.get(i);
            } else {
                double hl = highs.get(i) - lows.get(i);
                double hc = Math.abs(highs.get(i) - closes.get(i - 1));
                double lc = Math.abs(lows.get(i) - closes.get(i - 1));
                tr = Math.max(hl, Math.max(hc, lc));
            }
            trueRanges.add(tr);
        }

        // Calculate ATR as EMA of True Range
        List<Double> atr = new ArrayList<>();
        for (int i = 0; i < trueRanges.size(); i++) {
            if (i < period - 1) {
                atr.add(null);
            } else if (i == period - 1) {
                double sum = trueRanges.stream().limit(period).mapToDouble(Double::doubleValue).sum();
                atr.add(sum / period);
            } else {
                double prevATR = atr.get(i - 1);
                double currentATR = (prevATR * (period - 1) + trueRanges.get(i)) / period;
                atr.add(currentATR);
            }
        }

        return atr;
    }

    /**
     * Calculate Rate of Change (ROC)
     * Momentum indicator
     */
    public static List<Double> calculateROC(List<Double> prices, int period) {
        List<Double> roc = new ArrayList<>();

        for (int i = 0; i < prices.size(); i++) {
            if (i < period) {
                roc.add(null);
            } else {
                double change = prices.get(i) - prices.get(i - period);
                double rocValue = (change / prices.get(i - period)) * 100;
                roc.add(rocValue);
            }
        }

        return roc;
    }

    /**
     * Calculate returns (percentage change)
     */
    public static List<Double> calculateReturns(List<Double> prices) {
        List<Double> returns = new ArrayList<>();
        returns.add(null); // First day has no previous price

        for (int i = 1; i < prices.size(); i++) {
            double ret = ((prices.get(i) - prices.get(i - 1)) / prices.get(i - 1)) * 100;
            returns.add(ret);
        }

        return returns;
    }

    /**
     * Calculate cumulative returns
     */
    public static List<Double> calculateCumulativeReturns(List<Double> prices) {
        if (prices.isEmpty()) {
            return new ArrayList<>();
        }

        List<Double> cumReturns = new ArrayList<>();
        double initialPrice = prices.get(0);

        for (Double price : prices) {
            cumReturns.add(((price - initialPrice) / initialPrice) * 100);
        }

        return cumReturns;
    }
}
