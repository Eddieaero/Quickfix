package com.aero.quickfix.quant.service;

import com.aero.quickfix.quant.model.TradeLog;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MetricsCalculator
 * Computes quantitative performance metrics for backtests and strategies
 */
@Component
public class MetricsCalculator {

    private static final Logger logger = LoggerFactory.getLogger(MetricsCalculator.class);
    private static final double RISK_FREE_RATE = 0.02; // 2% annual risk-free rate
    private static final int TRADING_DAYS_PER_YEAR = 252;

    /**
     * Calculate Sharpe Ratio
     * Higher is better (>1.0 is good, >2.0 is excellent)
     * Formula: (Return - RiskFreeRate) / StdDev(Returns)
     */
    public BigDecimal calculateSharpeRatio(List<Double> dailyReturns) {
        if (dailyReturns == null || dailyReturns.size() < 2) {
            return BigDecimal.ZERO;
        }

        try {
            DescriptiveStatistics stats = new DescriptiveStatistics();
            dailyReturns.forEach(stats::addValue);

            double meanReturn = stats.getMean();
            double stdDev = stats.getStandardDeviation();

            if (stdDev == 0) {
                return BigDecimal.ZERO;
            }

            // Annualize metrics
            double annualizedReturn = meanReturn * TRADING_DAYS_PER_YEAR;
            double annualizedStdDev = stdDev * Math.sqrt(TRADING_DAYS_PER_YEAR);

            double sharpeRatio = (annualizedReturn - RISK_FREE_RATE) / annualizedStdDev;

            return new BigDecimal(sharpeRatio).setScale(4, RoundingMode.HALF_UP);
        } catch (Exception e) {
            logger.warn("Error calculating Sharpe ratio: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calculate Sortino Ratio
     * Similar to Sharpe but only penalizes downside volatility
     * Formula: (Return - TargetReturn) / DownsideStdDev
     */
    public BigDecimal calculateSortinoRatio(List<Double> dailyReturns, double targetReturn) {
        if (dailyReturns == null || dailyReturns.size() < 2) {
            return BigDecimal.ZERO;
        }

        try {
            DescriptiveStatistics stats = new DescriptiveStatistics();
            double meanReturn = dailyReturns.stream().mapToDouble(Double::doubleValue).average().orElse(0);

            // Calculate downside standard deviation (only negative deviations)
            double downsideVariance = dailyReturns.stream()
                .mapToDouble(r -> Math.pow(Math.min(r - targetReturn, 0), 2))
                .average()
                .orElse(0);

            double downsideStdDev = Math.sqrt(downsideVariance);

            if (downsideStdDev == 0) {
                return BigDecimal.ZERO;
            }

            double annualizedReturn = meanReturn * TRADING_DAYS_PER_YEAR;
            double annualizedDownsideStdDev = downsideStdDev * Math.sqrt(TRADING_DAYS_PER_YEAR);

            double sortinoRatio = (annualizedReturn - targetReturn) / annualizedDownsideStdDev;

            return new BigDecimal(sortinoRatio).setScale(4, RoundingMode.HALF_UP);
        } catch (Exception e) {
            logger.warn("Error calculating Sortino ratio: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calculate Maximum Drawdown
     * Percentage loss from peak to trough
     * Range: 0 to -100%
     */
    public BigDecimal calculateMaxDrawdown(List<BigDecimal> equityCurve) {
        if (equityCurve == null || equityCurve.size() < 2) {
            return BigDecimal.ZERO;
        }

        BigDecimal maxDrawdown = BigDecimal.ZERO;
        BigDecimal peak = equityCurve.get(0);

        for (BigDecimal value : equityCurve) {
            if (value.compareTo(peak) > 0) {
                peak = value;
            }

            BigDecimal drawdown = value.subtract(peak).divide(peak, 4, RoundingMode.HALF_UP);
            if (drawdown.compareTo(maxDrawdown) < 0) {
                maxDrawdown = drawdown;
            }
        }

        // Convert to percentage
        return maxDrawdown.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate Win Rate
     * Percentage of profitable trades
     * Range: 0% to 100%
     */
    public BigDecimal calculateWinRate(List<TradeLog> trades) {
        if (trades == null || trades.isEmpty()) {
            return BigDecimal.ZERO;
        }

        long winningTrades = trades.stream()
            .filter(t -> t.getProfitLoss() != null && t.getProfitLoss().compareTo(BigDecimal.ZERO) > 0)
            .count();

        double winRate = (double) winningTrades / trades.size() * 100;
        return new BigDecimal(winRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate Profit Factor
     * Gross profit / Gross loss (higher is better, >1.5 is good)
     */
    public BigDecimal calculateProfitFactor(List<TradeLog> trades) {
        if (trades == null || trades.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal grossProfit = trades.stream()
            .filter(t -> t.getProfitLoss() != null && t.getProfitLoss().compareTo(BigDecimal.ZERO) > 0)
            .map(TradeLog::getProfitLoss)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal grossLoss = trades.stream()
            .filter(t -> t.getProfitLoss() != null && t.getProfitLoss().compareTo(BigDecimal.ZERO) < 0)
            .map(t -> t.getProfitLoss().abs())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (grossLoss.compareTo(BigDecimal.ZERO) == 0) {
            return grossProfit.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("999.99") : BigDecimal.ZERO;
        }

        return grossProfit.divide(grossLoss, 4, RoundingMode.HALF_UP);
    }

    /**
     * Calculate Compound Annual Growth Rate (CAGR)
     * Formula: (EndValue / BeginValue)^(1/Years) - 1
     */
    public BigDecimal calculateCAGR(BigDecimal startValue, BigDecimal endValue, int years) {
        if (startValue == null || endValue == null || startValue.compareTo(BigDecimal.ZERO) <= 0 || years <= 0) {
            return BigDecimal.ZERO;
        }

        try {
            double ratio = endValue.doubleValue() / startValue.doubleValue();
            double cagr = Math.pow(ratio, 1.0 / years) - 1;
            return new BigDecimal(cagr * 100).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            logger.warn("Error calculating CAGR: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calculate Recovery Factor
     * Gross Profit / Max Drawdown (higher is better, >3 is good)
     */
    public BigDecimal calculateRecoveryFactor(BigDecimal grossProfit, BigDecimal maxDrawdownAmount) {
        if (maxDrawdownAmount == null || maxDrawdownAmount.compareTo(BigDecimal.ZERO) == 0) {
            return grossProfit.compareTo(BigDecimal.ZERO) > 0 ? new BigDecimal("999.99") : BigDecimal.ZERO;
        }

        return grossProfit.divide(maxDrawdownAmount.abs(), 4, RoundingMode.HALF_UP);
    }

    /**
     * Calculate average win from winning trades
     */
    public BigDecimal calculateAverageWin(List<TradeLog> trades) {
        if (trades == null || trades.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<BigDecimal> winningTrades = trades.stream()
            .filter(t -> t.getProfitLoss() != null && t.getProfitLoss().compareTo(BigDecimal.ZERO) > 0)
            .map(TradeLog::getProfitLoss)
            .collect(Collectors.toList());

        if (winningTrades.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = winningTrades.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(winningTrades.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate average loss from losing trades
     */
    public BigDecimal calculateAverageLoss(List<TradeLog> trades) {
        if (trades == null || trades.isEmpty()) {
            return BigDecimal.ZERO;
        }

        List<BigDecimal> losingTrades = trades.stream()
            .filter(t -> t.getProfitLoss() != null && t.getProfitLoss().compareTo(BigDecimal.ZERO) < 0)
            .map(t -> t.getProfitLoss().abs())
            .collect(Collectors.toList());

        if (losingTrades.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = losingTrades.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(new BigDecimal(losingTrades.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate total return percentage
     */
    public BigDecimal calculateTotalReturn(BigDecimal startValue, BigDecimal endValue) {
        if (startValue == null || startValue.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal profit = endValue.subtract(startValue);
        return profit.divide(startValue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));
    }

    /**
     * Calculate variance of returns
     */
    public BigDecimal calculateVariance(List<Double> returns) {
        if (returns == null || returns.size() < 2) {
            return BigDecimal.ZERO;
        }

        try {
            DescriptiveStatistics stats = new DescriptiveStatistics();
            returns.forEach(stats::addValue);
            return new BigDecimal(stats.getVariance()).setScale(6, RoundingMode.HALF_UP);
        } catch (Exception e) {
            logger.warn("Error calculating variance: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }

    /**
     * Calculate standard deviation of returns
     */
    public BigDecimal calculateStdDev(List<Double> returns) {
        if (returns == null || returns.size() < 2) {
            return BigDecimal.ZERO;
        }

        try {
            DescriptiveStatistics stats = new DescriptiveStatistics();
            returns.forEach(stats::addValue);
            return new BigDecimal(stats.getStandardDeviation()).setScale(6, RoundingMode.HALF_UP);
        } catch (Exception e) {
            logger.warn("Error calculating standard deviation: {}", e.getMessage());
            return BigDecimal.ZERO;
        }
    }
}
