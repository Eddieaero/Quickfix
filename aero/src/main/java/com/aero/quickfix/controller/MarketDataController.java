package com.aero.quickfix.controller;

import com.aero.quickfix.dto.MarketPriceDto;
import com.aero.quickfix.service.InvestmentService;
import com.aero.quickfix.service.MarketDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for market data operations.
 * Provides endpoints for fetching real-time market prices and company fundamentals.
 */
@RestController
@RequestMapping("/api/market")
@CrossOrigin("*")
public class MarketDataController {

    @Autowired
    private MarketDataService marketDataService;

    @Autowired
    private InvestmentService investmentService;

    /**
     * Get current market price for a symbol.
     * @param symbol Stock symbol (e.g., CRDB.TZ for Tanzania stocks)
     * @return MarketPriceDto with current price and metadata
     */
    @GetMapping("/price/{symbol}")
    public ResponseEntity<MarketPriceDto> getMarketPrice(@PathVariable String symbol) {
        MarketPriceDto price = marketDataService.getCurrentPrice(symbol);
        return ResponseEntity.ok(price);
    }

    /**
     * Validate if a symbol is valid and tradeable.
     * @param symbol Stock symbol to validate
     * @return Validation result
     */
    @GetMapping("/validate/{symbol}")
    public ResponseEntity<Map<String, Object>> validateSymbol(@PathVariable String symbol) {
        boolean isValid = investmentService.isValidSymbol(symbol);
        return ResponseEntity.ok(Map.of(
                "symbol", symbol,
                "valid", isValid,
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Get all cached prices.
     * Useful for monitoring cache status and debugging.
     * @return List of all cached prices
     */
    @GetMapping("/cache/prices")
    public ResponseEntity<List<MarketPriceDto>> getCachedPrices() {
        List<MarketPriceDto> prices = investmentService.getCachedPrices();
        return ResponseEntity.ok(prices);
    }

    /**
     * Get cache statistics.
     * @return Cache size and status information
     */
    @GetMapping("/cache/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        int cacheSize = marketDataService.getCacheSize();
        return ResponseEntity.ok(Map.of(
                "cacheSize", cacheSize,
                "cacheExpirationMs", 5 * 360 * 1000,
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Clear market data cache.
     * Useful for forcing a price refresh.
     * @return Success message
     */
    @PostMapping("/cache/clear")
    public ResponseEntity<Map<String, Object>> clearCache() {
        investmentService.clearMarketDataCache();
        return ResponseEntity.ok(Map.of(
                "message", "Cache cleared successfully",
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Get prices for multiple symbols.
     * @param symbols Comma-separated symbols
     * @return Map of symbol to price
     */
    @GetMapping("/prices")
    public ResponseEntity<Map<String, MarketPriceDto>> getMultiplePrices(@RequestParam String symbols) {
        String[] symbolArray = symbols.split(",");
        Map<String, MarketPriceDto> prices = new java.util.HashMap<>();

        for (String symbol : symbolArray) {
            prices.put(symbol.trim(), marketDataService.getCurrentPrice(symbol.trim()));
        }

        return ResponseEntity.ok(prices);
    }

    /**
     * Get DSE (Tanzania stock exchange) specific data.
     * Returns all Tanzania DSE listed stocks.
     * @return Map of DSE stocks with current prices
     */
    @GetMapping("/dse/stocks")
    public ResponseEntity<Map<String, MarketPriceDto>> getDseStocks() {
        // All DSE listed companies
        String[] dseStocks = {
            // Major Banks & Financial
            "CRDB.TZ",      // CRDB Bank
            "NMB.TZ",       // National Microfinance Bank
            "TBL.TZ",       // Tanzania Breweries Limited
            "TPLF.TZ",      // Tanzania Portland Cement
            "SIMBA.TZ",     // Simba Cement
            "UCHM.TZ",      // Uchumi Supermarkets
            "SWALLOW.TZ",   // Swallow Automobiles
            "KCB.TZ",       // Kenya Commercial Bank Tanzania
            "AIRTEL.TZ",    // Airtel Tanzania
            "VODACOM.TZ",   // Vodacom Tanzania
            "TTCL.TZ",      // Tanzania Telecommunications Company
            "TANZCEM.TZ",   // Tanzania Cement Company
            "MOGAS.TZ",     // Mogassham
            "DACB.TZ",      // Dar es Salaam Community Bank
            "Tanzania.TZ"   // Tanzania National
        };

        Map<String, MarketPriceDto> stocks = new java.util.HashMap<>();
        for (String stock : dseStocks) {
            stocks.put(stock, marketDataService.getCurrentPrice(stock));
        }

        return ResponseEntity.ok(stocks);
    }

    /**
     * Get top US stocks with real market prices from EODHD.
     * @return List of popular US stock prices
     */
    @GetMapping("/us/stocks")
    public ResponseEntity<Map<String, MarketPriceDto>> getUsStocks() {
        // Popular US stocks for demonstration
        String[] usStocks = {
            "AAPL",     // Apple Inc.
            "MSFT",     // Microsoft Corporation
            "GOOGL",    // Alphabet Inc.
            "AMZN",     // Amazon.com Inc.
            "TSLA",     // Tesla Inc.
            "NVDA",     // NVIDIA Corporation
            "META",     // Meta Platforms Inc.
            "NFLX",     // Netflix Inc.
            "COIN",     // Coinbase Global Inc.
            "AMD"       // Advanced Micro Devices
        };

        Map<String, MarketPriceDto> stocks = new java.util.HashMap<>();
        for (String stock : usStocks) {
            stocks.put(stock, marketDataService.getCurrentPrice(stock));
        }

        return ResponseEntity.ok(stocks);
    }

    /**
     * Health check endpoint for market data service.
     * @return Service status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "service", "market-data",
                "timestamp", System.currentTimeMillis()
        ));
    }

    /**
     * Debug endpoint to check configuration loading.
     * @return Configuration values for debugging
     */
    @GetMapping("/debug/config")
    public ResponseEntity<Map<String, String>> getConfig() {
        Map<String, String> config = new java.util.HashMap<>();
        config.put("EODHD_API_KEY", System.getProperty("EODHD_API_KEY", "NOT SET"));
        config.put("MARKET_DATA_ENABLED", System.getProperty("MARKET_DATA_ENABLED", "NOT SET"));
        config.put("CACHE_EXPIRATION_MINUTES", System.getProperty("CACHE_EXPIRATION_MINUTES", "NOT SET"));
        config.put("USER_DIR", System.getProperty("user.dir"));
        config.put("JAVA_VERSION", System.getProperty("java.version"));
        return ResponseEntity.ok(config);
    }
}
