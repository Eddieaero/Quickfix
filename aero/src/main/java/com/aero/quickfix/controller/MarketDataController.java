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
                "cacheExpirationMs", 5 * 60 * 1000,
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
     * Returns common Tanzania stocks.
     * @return List of DSE stocks with current prices
     */
    @GetMapping("/dse/stocks")
    public ResponseEntity<Map<String, MarketPriceDto>> getDseStocks() {
        String[] tanzaniaStocks = {"CRDB.TZ", "NMB.TZ", "TBL.TZ", "JHL.TZ"};
        Map<String, MarketPriceDto> stocks = new java.util.HashMap<>();

        for (String stock : tanzaniaStocks) {
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
}
