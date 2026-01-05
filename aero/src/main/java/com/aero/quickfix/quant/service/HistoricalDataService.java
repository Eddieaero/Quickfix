package com.aero.quickfix.quant.service;

import com.aero.quickfix.quant.model.OHLCVData;
import com.aero.quickfix.quant.repository.OHLCVDataRepository;
import com.aero.quickfix.client.FinvizMarketDataClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * HistoricalDataService
 * Manages fetching, validating, and storing historical OHLCV data
 */
@Service
public class HistoricalDataService {

    private static final Logger logger = LoggerFactory.getLogger(HistoricalDataService.class);
    
    private final FinvizMarketDataClient finvizClient;
    private final OHLCVDataRepository ohlcvRepository;

    public HistoricalDataService(FinvizMarketDataClient finvizClient, OHLCVDataRepository ohlcvRepository) {
        this.finvizClient = finvizClient;
        this.ohlcvRepository = ohlcvRepository;
    }

    /**
     * Fetch and store historical OHLCV data for a symbol
     */
    @Transactional
    public HistoricalDataResult fetchAndStoreHistoricalData(String symbol, LocalDate from, LocalDate to) {
        try {
            logger.info("Starting historical data fetch for {} from {} to {}", symbol, from, to);

            // Fetch data from Finviz-based APIs
            List<OHLCVData> rawData = finvizClient.fetchHistoricalOHLCV(symbol, from, to);
            logger.info("Fetched {} records from Finviz APIs for {}", rawData.size(), symbol);

            if (rawData.isEmpty()) {
                logger.warn("No data received from Finviz APIs for symbol: {}", symbol);
                return new HistoricalDataResult(symbol, 0, 0, "No data available from API");
            }

            // Validate data
            List<OHLCVData> validatedData = validateData(rawData, symbol);
            logger.info("Validated {} records (removed {} invalid records)", validatedData.size(), 
                rawData.size() - validatedData.size());

            // Remove duplicates
            List<OHLCVData> dedupedData = deduplicateData(validatedData);
            logger.info("After deduplication: {} records", dedupedData.size());

            // Save to database
            List<OHLCVData> savedData = ohlcvRepository.saveAll(dedupedData);
            logger.info("Successfully saved {} records to database for {}", savedData.size(), symbol);

            return new HistoricalDataResult(symbol, savedData.size(), 0, "Success");

        } catch (Exception e) {
            logger.error("Error fetching historical data for {}: {}", symbol, e.getMessage(), e);
            return new HistoricalDataResult(symbol, 0, 0, "Error: " + e.getMessage());
        }
    }

    /**
     * Validate OHLCV data quality
     */
    private List<OHLCVData> validateData(List<OHLCVData> data, String symbol) {
        return data.stream()
            .filter(record -> {
                // Check basic validations
                if (record.getClose() == null || record.getClose().signum() <= 0) {
                    logger.debug("Invalid close price for {}: {}", symbol, record.getClose());
                    return false;
                }

                // Validate OHLC relationships: Low <= Close <= High
                if (record.getLow().compareTo(record.getClose()) > 0 || 
                    record.getClose().compareTo(record.getHigh()) > 0) {
                    logger.debug("Invalid OHLC relationship for {} on {}: O={} H={} L={} C={}", 
                        symbol, record.getTime(), record.getOpen(), record.getHigh(), 
                        record.getLow(), record.getClose());
                    return false;
                }

                // Validate volume
                if (record.getVolume() == null || record.getVolume() < 0) {
                    logger.debug("Invalid volume for {}: {}", symbol, record.getVolume());
                    return false;
                }

                return true;
            })
            .toList();
    }

    /**
     * Remove duplicate records (same symbol and date)
     */
    private List<OHLCVData> deduplicateData(List<OHLCVData> data) {
        return data.stream()
            .distinct()
            .toList();
    }

    /**
     * Get count of records for a symbol
     */
    public long getRecordCount(String symbol) {
        return ohlcvRepository.countBySymbol(symbol);
    }

    /**
     * Check if data exists for a symbol on a specific date
     */
    public boolean hasDataForDate(String symbol, LocalDate date) {
        return ohlcvRepository.countBySymbolAndDate(symbol, date) > 0;
    }

    /**
     * Get all available symbols in database
     */
    public List<String> getAllSymbols() {
        return ohlcvRepository.findAllSymbols();
    }

    /**
     * Result object for historical data operations
     */
    public static class HistoricalDataResult {
        private final String symbol;
        private final int recordsInserted;
        private final int recordsUpdated;
        private final String message;

        public HistoricalDataResult(String symbol, int recordsInserted, int recordsUpdated, String message) {
            this.symbol = symbol;
            this.recordsInserted = recordsInserted;
            this.recordsUpdated = recordsUpdated;
            this.message = message;
        }

        public String getSymbol() { return symbol; }
        public int getRecordsInserted() { return recordsInserted; }
        public int getRecordsUpdated() { return recordsUpdated; }
        public String getMessage() { return message; }
        public boolean isSuccess() { return recordsInserted > 0; }
    }
}
