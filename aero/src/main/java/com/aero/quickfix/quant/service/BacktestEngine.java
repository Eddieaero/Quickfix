package com.aero.quickfix.quant.service;

import com.aero.quickfix.quant.indicators.IndicatorCalculator;
import com.aero.quickfix.quant.model.BacktestResults;
import com.aero.quickfix.quant.model.OHLCVData;
import com.aero.quickfix.quant.model.TradeLog;
import com.aero.quickfix.quant.repository.BacktestResultsRepository;
import com.aero.quickfix.quant.repository.OHLCVDataRepository;
import com.aero.quickfix.quant.repository.TradeLogRepository;
import com.aero.quickfix.quant.strategy.Signal;
import com.aero.quickfix.quant.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Backtest Engine
 * Executes historical backtests of trading strategies
 */
@Service
@Transactional
public class BacktestEngine {

    private static final Logger logger = LoggerFactory.getLogger(BacktestEngine.class);

    @Autowired
    private OHLCVDataRepository ohlcvDataRepository;

    @Autowired
    private BacktestResultsRepository backtestResultsRepository;

    @Autowired
    private TradeLogRepository tradeLogRepository;

    @Autowired
    private IndicatorCalculator indicatorCalculator;

    @Autowired
    private MetricsCalculator metricsCalculator;

    /**
     * Execute a complete backtest for a strategy on a symbol
     */
    public BacktestResults runBacktest(Strategy strategy, String symbol, LocalDate startDate, LocalDate endDate, BigDecimal initialCapital) {
        
        logger.info("Starting backtest - Strategy: {}, Symbol: {}, Period: {} to {}", 
            strategy.getName(), symbol, startDate, endDate);

        if (!strategy.isValid()) {
            throw new IllegalArgumentException("Strategy is not properly configured");
        }

        // Fetch historical data
        List<OHLCVData> priceHistory = ohlcvDataRepository.findBySymbolAndDateRange(symbol, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
        
        if (priceHistory == null || priceHistory.isEmpty()) {
            logger.warn("No price data found for {} between {} and {}", symbol, startDate, endDate);
            return createEmptyBacktestResult(strategy.getName(), symbol, startDate, endDate, initialCapital);
        }

        // Initialize backtest state
        List<TradeLog> trades = new ArrayList<>();
        BigDecimal currentCapital = initialCapital;
        BigDecimal shares = BigDecimal.ZERO;
        boolean hasOpenPosition = false;
        BigDecimal entryPrice = BigDecimal.ZERO;
        LocalDate entryDate = null;
        String entrySignal = "";
        List<BigDecimal> equityCurve = new ArrayList<>();
        equityCurve.add(initialCapital);

        // Process each bar
        for (int i = strategy.getMinimumBars(); i < priceHistory.size(); i++) {
            
            // Get price history up to current bar
            List<OHLCVData> windowPrices = priceHistory.subList(0, i + 1);
            OHLCVData currentBar = windowPrices.get(windowPrices.size() - 1);

            // Calculate indicators
            Map<String, List<Double>> indicators = calculateIndicators(strategy, windowPrices);

            // Generate signal
            Signal signal = strategy.generateSignal(windowPrices, indicators);

            // Process signal
            BigDecimal currentPrice = currentBar.getClose();

            if (signal.isBuySignal() && !hasOpenPosition) {
                // Enter long position
                shares = currentCapital.divide(currentPrice, 4, RoundingMode.HALF_UP);
                currentCapital = BigDecimal.ZERO;
                hasOpenPosition = true;
                entryPrice = currentPrice;
                entryDate = currentBar.getTime().toLocalDate();
                entrySignal = signal.getReason();
                logger.debug("BUY signal at {} - Price: {}, Shares: {}", entryDate, entryPrice, shares);
            }
            else if (signal.isSellSignal() && hasOpenPosition) {
                // Exit long position
                BigDecimal exitPrice = currentPrice;
                BigDecimal grossProceeds = shares.multiply(exitPrice);
                BigDecimal profitLoss = grossProceeds.subtract(shares.multiply(entryPrice));
                BigDecimal profitLossPct = profitLoss.divide(shares.multiply(entryPrice), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

                currentCapital = grossProceeds;
                hasOpenPosition = false;

                // Record trade
                TradeLog trade = new TradeLog();
                trade.setSymbol(symbol);
                trade.setTradeDate(currentBar.getTime().toLocalDate());
                trade.setEntryPrice(entryPrice);
                trade.setExitPrice(exitPrice);
                trade.setQuantity(shares);
                trade.setTradeType("LONG");
                trade.setTradeStatus("CLOSED");
                trade.setProfitLoss(profitLoss);
                trade.setProfitLossPct(profitLossPct);
                trade.setEntrySignal(entrySignal);
                trade.setExitSignal(signal.getReason());
                trades.add(trade);

                logger.debug("SELL signal at {} - Price: {}, P&L: {} ({}%)", 
                    currentBar.getTime().toLocalDate(), exitPrice, profitLoss, profitLossPct);

                shares = BigDecimal.ZERO;
            }

            // Track equity
            BigDecimal barEquity = currentCapital.add(shares.multiply(currentPrice));
            equityCurve.add(barEquity);
        }

        // Close any open position at market close
        if (hasOpenPosition && !priceHistory.isEmpty()) {
            OHLCVData lastBar = priceHistory.get(priceHistory.size() - 1);
            BigDecimal closePrice = lastBar.getClose();
            BigDecimal grossProceeds = shares.multiply(closePrice);
            BigDecimal profitLoss = grossProceeds.subtract(shares.multiply(entryPrice));
            BigDecimal profitLossPct = profitLoss.divide(shares.multiply(entryPrice), 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100"));

            currentCapital = grossProceeds;

            TradeLog trade = new TradeLog();
            trade.setSymbol(symbol);
            trade.setTradeDate(lastBar.getTime().toLocalDate());
            trade.setEntryPrice(entryPrice);
            trade.setExitPrice(closePrice);
            trade.setQuantity(shares);
            trade.setTradeType("LONG");
            trade.setTradeStatus("CLOSED");
            trade.setProfitLoss(profitLoss);
            trade.setProfitLossPct(profitLossPct);
            trade.setEntrySignal(entrySignal);
            trade.setExitSignal("End of backtest period");
            trades.add(trade);

            shares = BigDecimal.ZERO;
        }

        // Calculate metrics
        BacktestResults results = new BacktestResults();
        results.setStrategyName(strategy.getName());
        results.setSymbol(symbol);
        results.setStartDate(startDate);
        results.setEndDate(endDate);
        results.setInitialCapital(initialCapital);
        results.setFinalValue(currentCapital);

        // Performance metrics
        BigDecimal totalReturn = metricsCalculator.calculateTotalReturn(initialCapital, currentCapital);
        results.setTotalReturn(totalReturn);

        int yearsDuration = endDate.getYear() - startDate.getYear();
        if (yearsDuration == 0) yearsDuration = 1;
        BigDecimal annualReturn = metricsCalculator.calculateCAGR(initialCapital, currentCapital, yearsDuration);
        results.setAnnualReturn(annualReturn);

        // Calculate daily returns for Sharpe and Sortino
        List<Double> dailyReturns = calculateDailyReturns(equityCurve);
        results.setSharpeRatio(metricsCalculator.calculateSharpeRatio(dailyReturns));
        results.setSortinoRatio(metricsCalculator.calculateSortinoRatio(dailyReturns, 0.0));

        // Drawdown metrics
        BigDecimal maxDrawdown = metricsCalculator.calculateMaxDrawdown(equityCurve);
        results.setMaxDrawdown(maxDrawdown);

        // Trade metrics
        results.setTotalTrades((int) trades.size());
        long winningTrades = trades.stream().filter(t -> t.getProfitLoss().compareTo(BigDecimal.ZERO) > 0).count();
        results.setWinningTrades((int) winningTrades);
        results.setLosingTrades((int) (trades.size() - winningTrades));

        results.setWinRate(metricsCalculator.calculateWinRate(trades));
        results.setAvgWin(metricsCalculator.calculateAverageWin(trades));
        results.setAvgLoss(metricsCalculator.calculateAverageLoss(trades));
        results.setProfitFactor(metricsCalculator.calculateProfitFactor(trades));

        // Save results and trades
        BacktestResults savedResults = backtestResultsRepository.save(results);
        
        // Associate trades with backtest
        for (TradeLog trade : trades) {
            trade.setBacktestId(savedResults.getId());
            tradeLogRepository.save(trade);
        }

        logger.info("Backtest completed - Strategy: {}, Total Trades: {}, Final Value: {}, Return: {}%", 
            strategy.getName(), trades.size(), currentCapital, totalReturn);

        return savedResults;
    }

    /**
     * Calculate indicators required by strategy for price window
     */
    private Map<String, List<Double>> calculateIndicators(Strategy strategy, List<OHLCVData> priceHistory) {
        Map<String, List<Double>> indicators = new HashMap<>();
        List<Double> prices = priceHistory.stream()
            .map(p -> p.getClose().doubleValue())
            .collect(Collectors.toList());

        // Calculate all requested indicators
        for (String indicator : strategy.getRequiredIndicators()) {
            List<Double> values = new ArrayList<>();

            switch (indicator) {
                case "SMA_50":
                    values = indicatorCalculator.calculateSMA(prices, 50);
                    break;
                case "SMA_200":
                    values = indicatorCalculator.calculateSMA(prices, 200);
                    break;
                case "EMA_12":
                    values = indicatorCalculator.calculateEMA(prices, 12);
                    break;
                case "EMA_26":
                    values = indicatorCalculator.calculateEMA(prices, 26);
                    break;
                case "RSI_14":
                    values = indicatorCalculator.calculateRSI(prices, 14);
                    break;
                case "MACD":
                    // MACD returns a map, extract only MACD line
                    Map<String, List<Double>> macdResult = indicatorCalculator.calculateMACD(prices, 12, 26, 9);
                    values = macdResult.getOrDefault("MACD", new ArrayList<>());
                    break;
                case "BOLLINGER_BANDS":
                    // For now, just track middle band (SMA20)
                    values = indicatorCalculator.calculateSMA(prices, 20);
                    break;
                default:
                    logger.warn("Unknown indicator: {}", indicator);
            }

            if (!values.isEmpty()) {
                indicators.put(indicator, values);
            }
        }

        return indicators;
    }

    /**
     * Calculate daily returns from equity curve
     */
    private List<Double> calculateDailyReturns(List<BigDecimal> equityCurve) {
        List<Double> returns = new ArrayList<>();

        for (int i = 1; i < equityCurve.size(); i++) {
            BigDecimal previousValue = equityCurve.get(i - 1);
            BigDecimal currentValue = equityCurve.get(i);

            if (previousValue.compareTo(BigDecimal.ZERO) > 0) {
                double dailyReturn = currentValue.subtract(previousValue)
                    .divide(previousValue, 6, RoundingMode.HALF_UP)
                    .doubleValue();
                returns.add(dailyReturn);
            }
        }

        return returns;
    }

    /**
     * Create empty backtest result when no data available
     */
    private BacktestResults createEmptyBacktestResult(String strategyName, String symbol, 
                                                      LocalDate startDate, LocalDate endDate, 
                                                      BigDecimal initialCapital) {
        BacktestResults results = new BacktestResults();
        results.setStrategyName(strategyName);
        results.setSymbol(symbol);
        results.setStartDate(startDate);
        results.setEndDate(endDate);
        results.setInitialCapital(initialCapital);
        results.setFinalValue(initialCapital);
        results.setTotalReturn(BigDecimal.ZERO);
        results.setAnnualReturn(BigDecimal.ZERO);
        results.setTotalTrades(0);
        return results;
    }

    /**
     * Get backtest results by ID
     */
    public BacktestResults getBacktestResults(UUID id) {
        return backtestResultsRepository.findById(id).orElse(null);
    }

    /**
     * Get all backtest results for a strategy
     */
    public List<BacktestResults> getBacktestsByStrategy(String strategyName) {
        return backtestResultsRepository.findByStrategyName(strategyName);
    }
}
