package com.aero.quickfix.service;

import com.aero.quickfix.client.FinvizMarketDataClient;
import com.aero.quickfix.dto.MarketPriceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing market data operations.
 * Fetches and caches market prices from Finviz-based APIs (Finnhub, Alpha Vantage).
 */
@Service
public class MarketDataService {

    @Autowired
    private FinvizMarketDataClient finvizClient;

    // In-memory cache for prices (key: symbol, value: MarketPriceDto)
    private final Map<String, MarketPriceDto> priceCache = new ConcurrentHashMap<>();

    // Cache expiration time in milliseconds (60 minutes)
    private static final long CACHE_EXPIRATION_MS = 60 * 60 * 1000;

    /**
     * Get current market price for a symbol.
     * Uses cache if available and not expired.
     * @param symbol Stock symbol (e.g., AAPL, CRDB)
     * @return MarketPriceDto with current price
     */
    public MarketPriceDto getCurrentPrice(String symbol) {
        MarketPriceDto cachedPrice = priceCache.get(symbol);

        // Return cached price if still valid
        if (cachedPrice != null && !isCacheExpired(cachedPrice)) {
            return cachedPrice;
        }

        // Fetch fresh price from Finviz APIs
        MarketPriceDto freshPrice = finvizClient.fetchLatestPrice(symbol);

        // Cache the result
        if (freshPrice.isValid()) {
            priceCache.put(symbol, freshPrice);
        }

        return freshPrice;
    }

    /**
     * Validate if a symbol is valid by attempting to fetch its price.
     * @param symbol Stock symbol to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidSymbol(String symbol) {
        MarketPriceDto price = getCurrentPrice(symbol);
        return price.isValid();
    }

    /**
     * Calculate real-time portfolio value based on current market prices.
     * @param investmentSymbol Stock symbol
     * @param quantity Number of shares
     * @return Current market value of holdings
     */
    public BigDecimal calculateMarketValue(String investmentSymbol, BigDecimal quantity) {
        MarketPriceDto priceData = getCurrentPrice(investmentSymbol);

        if (!priceData.isValid()) {
            return BigDecimal.ZERO;
        }

        return priceData.getPrice().multiply(quantity);
    }

    /**
     * Calculate gains/losses compared to purchase price.
     * @param purchasePrice Original purchase price per unit
     * @param currentPrice Current market price per unit
     * @param quantity Number of shares
     * @return Gains/losses amount
     */
    public BigDecimal calculateGains(BigDecimal purchasePrice, BigDecimal currentPrice, BigDecimal quantity) {
        BigDecimal priceGain = currentPrice.subtract(purchasePrice);
        return priceGain.multiply(quantity);
    }

    /**
     * Note: Dividend income calculation requires additional data sources.
     * Currently not supported with free APIs.
     * @param symbol Stock symbol
     * @param principal Investment amount
     * @return Zero (not currently calculated)
     */
    public BigDecimal calculateDividendIncome(String symbol, BigDecimal principal) {
        // Dividend data requires paid subscriptions
        // Use alternative sources or manual input
        return BigDecimal.ZERO;
    }

    /**
     * Clear price cache (useful for manual refresh).
     */
    public void clearCache() {
        priceCache.clear();
    }

    /**
     * Get cache size for monitoring.
     */
    public int getCacheSize() {
        return priceCache.size();
    }

    /**
     * Get all cached prices.
     */
    public Map<String, MarketPriceDto> getAllCachedPrices() {
        return new HashMap<>(priceCache);
    }

    /**
     * Check if a cached price has expired.
     */
    private boolean isCacheExpired(MarketPriceDto priceData) {
        long age = System.currentTimeMillis() - priceData.getLastUpdated();
        return age > CACHE_EXPIRATION_MS;
    }
}
