package com.aero.quickfix.client;

import com.aero.quickfix.dto.MarketPriceDto;
import com.aero.quickfix.quant.model.OHLCVData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Finviz-based Market Data Client for fetching stock data.
 * Uses public stock data APIs compatible with Finviz approach.
 * Primary source: Finnhub API (free tier available)
 * Fallback: Alpha Vantage API
 */
@Component
public class FinvizMarketDataClient {

    private static final Logger logger = LoggerFactory.getLogger(FinvizMarketDataClient.class);

    // Finnhub API
    private static final String FINNHUB_BASE_URL = "https://finnhub.io/api/v1";
    
    // Alpha Vantage API (fallback)
    private static final String ALPHA_VANTAGE_BASE_URL = "https://www.alphavantage.co/query";

    // IEX Cloud (alternative - free tier)
    private static final String IEX_BASE_URL = "https://cloud.iexapis.com/stable";

    @Value("${finviz.api.key:}")
    private String finnhubApiKey;

    @Value("${alpha-vantage.api.key:}")
    private String alphaVantageApiKey;

    @Value("${finviz.enabled:true}")
    private boolean enabled;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public FinvizMarketDataClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        logInitialization();
    }

    private void logInitialization() {
        String finnhubPreview = (finnhubApiKey != null && !finnhubApiKey.isEmpty()) 
            ? finnhubApiKey.substring(0, Math.min(4, finnhubApiKey.length())) + "..." 
            : "not-configured";
        logger.info("FinvizMarketDataClient initialized - Finnhub API: {}, Enabled: {}", finnhubPreview, enabled);
    }

    /**
     * Fetch latest price for a symbol using Finnhub API.
     * @param symbol Stock symbol (e.g., AAPL, CRDB)
     * @return MarketPriceDto with current price
     */
    public MarketPriceDto fetchLatestPrice(String symbol) {
        if (!enabled) {
            logger.debug("Market data disabled for symbol: {}", symbol);
            return new MarketPriceDto(symbol, "Market data service is disabled");
        }

        try {
            // Try Finnhub first
            if (finnhubApiKey != null && !finnhubApiKey.isEmpty()) {
                MarketPriceDto result = fetchFromFinnhub(symbol);
                if (result.isValid()) {
                    return result;
                }
            }

            // Try Alpha Vantage as fallback
            if (alphaVantageApiKey != null && !alphaVantageApiKey.isEmpty()) {
                return fetchFromAlphaVantage(symbol);
            }

            logger.warn("No API keys configured for market data. Please set finviz.api.key and/or alpha-vantage.api.key");
            return new MarketPriceDto(symbol, "Market data APIs not configured");

        } catch (Exception e) {
            logger.error("Unexpected error fetching price for {}: {}", symbol, e.getMessage(), e);
            return new MarketPriceDto(symbol, "Error: " + e.getMessage());
        }
    }

    /**
     * Fetch price from Finnhub API.
     */
    private MarketPriceDto fetchFromFinnhub(String symbol) {
        try {
            String url = String.format("%s/quote?symbol=%s&token=%s",
                    FINNHUB_BASE_URL,
                    URLEncoder.encode(symbol, StandardCharsets.UTF_8),
                    finnhubApiKey);

            logger.debug("Fetching price from Finnhub for: {}", symbol);
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("c")) {
                BigDecimal price = new BigDecimal(jsonNode.get("c").asDouble());
                logger.debug("Received price for {} from Finnhub: {}", symbol, price);
                
                MarketPriceDto dto = new MarketPriceDto();
                dto.setSymbol(symbol);
                dto.setPrice(price);
                dto.setLastUpdated(System.currentTimeMillis());
                return dto;
            }

            logger.warn("No price data available from Finnhub for symbol: {}", symbol);
            return new MarketPriceDto(symbol, "No price data available from Finnhub for symbol: " + symbol);

        } catch (HttpClientErrorException e) {
            logger.debug("HTTP error {} fetching price from Finnhub for {}: {}", 
                    e.getStatusCode(), symbol, e.getResponseBodyAsString());
            return new MarketPriceDto(symbol, "Finnhub HTTP " + e.getStatusCode());
        } catch (Exception e) {
            logger.debug("Error fetching from Finnhub for {}: {}", symbol, e.getMessage());
            return new MarketPriceDto(symbol, "Finnhub error: " + e.getMessage());
        }
    }

    /**
     * Fetch price from Alpha Vantage API as fallback.
     */
    private MarketPriceDto fetchFromAlphaVantage(String symbol) {
        try {
            String url = String.format("%s?function=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                    ALPHA_VANTAGE_BASE_URL,
                    URLEncoder.encode(symbol, StandardCharsets.UTF_8),
                    alphaVantageApiKey);

            logger.debug("Fetching price from Alpha Vantage for: {}", symbol);
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("Global Quote")) {
                JsonNode quote = jsonNode.get("Global Quote");
                if (quote.has("05. price")) {
                    BigDecimal price = new BigDecimal(quote.get("05. price").asText());
                    logger.debug("Received price for {} from Alpha Vantage: {}", symbol, price);
                    
                    MarketPriceDto dto = new MarketPriceDto();
                    dto.setSymbol(symbol);
                    dto.setPrice(price);
                    dto.setLastUpdated(System.currentTimeMillis());
                    return dto;
                }
            }

            logger.warn("No price data available from Alpha Vantage for symbol: {}", symbol);
            return new MarketPriceDto(symbol, "No price data available from Alpha Vantage for symbol: " + symbol);

        } catch (Exception e) {
            logger.debug("Error fetching from Alpha Vantage for {}: {}", symbol, e.getMessage());
            return new MarketPriceDto(symbol, "Alpha Vantage error: " + e.getMessage());
        }
    }

    /**
     * Fetch historical OHLCV data for a symbol from Alpha Vantage.
     * @param symbol Stock symbol
     * @param from Start date
     * @param to End date
     * @return List of OHLCVData
     */
    public List<OHLCVData> fetchHistoricalOHLCV(String symbol, LocalDate from, LocalDate to) {
        List<OHLCVData> result = new ArrayList<>();

        if (!enabled) {
            logger.debug("Market data disabled for symbol: {}", symbol);
            return result;
        }

        try {
            if (alphaVantageApiKey == null || alphaVantageApiKey.isEmpty()) {
                logger.warn("Alpha Vantage API key not configured. Cannot fetch historical data. Please set alpha-vantage.api.key");
                return result;
            }

            // Fetch daily data from Alpha Vantage
            result.addAll(fetchHistoricalFromAlphaVantage(symbol, from, to));

            logger.info("Fetched {} historical records for {} from {} to {}", 
                    result.size(), symbol, from, to);

            return result;

        } catch (Exception e) {
            logger.error("Error fetching historical data for {}: {}", symbol, e.getMessage(), e);
            return result;
        }
    }

    /**
     * Fetch historical data from Alpha Vantage API.
     */
    private List<OHLCVData> fetchHistoricalFromAlphaVantage(String symbol, LocalDate from, LocalDate to) {
        List<OHLCVData> result = new ArrayList<>();

        try {
            String url = String.format("%s?function=TIME_SERIES_DAILY&symbol=%s&outputsize=full&apikey=%s",
                    ALPHA_VANTAGE_BASE_URL,
                    URLEncoder.encode(symbol, StandardCharsets.UTF_8),
                    alphaVantageApiKey);

            logger.debug("Fetching historical data from Alpha Vantage for: {}", symbol);
            
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            if (jsonNode.has("Time Series (Daily)")) {
                JsonNode timeSeries = jsonNode.get("Time Series (Daily)");
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

                timeSeries.fields().forEachRemaining(entry -> {
                    try {
                        String dateStr = entry.getKey();
                        LocalDate date = LocalDate.parse(dateStr, formatter);

                        // Only include data within requested range
                        if (!date.isBefore(from) && !date.isAfter(to)) {
                            JsonNode dayData = entry.getValue();

                            OHLCVData ohlcv = new OHLCVData();
                            ohlcv.setSymbol(symbol);
                            ohlcv.setTime(date.atStartOfDay());
                            ohlcv.setOpen(new BigDecimal(dayData.get("1. open").asText()));
                            ohlcv.setHigh(new BigDecimal(dayData.get("2. high").asText()));
                            ohlcv.setLow(new BigDecimal(dayData.get("3. low").asText()));
                            ohlcv.setClose(new BigDecimal(dayData.get("4. close").asText()));
                            ohlcv.setVolume(dayData.get("5. volume").asLong());

                            result.add(ohlcv);
                        }
                    } catch (Exception e) {
                        logger.warn("Error parsing OHLCV data for {}: {}", symbol, e.getMessage());
                    }
                });
            } else if (jsonNode.has("Error Message")) {
                logger.error("Alpha Vantage API error: {}", jsonNode.get("Error Message").asText());
            } else if (jsonNode.has("Note")) {
                logger.warn("Alpha Vantage API rate limit: {}", jsonNode.get("Note").asText());
            }

        } catch (Exception e) {
            logger.error("Error fetching historical data from Alpha Vantage for {}: {}", symbol, e.getMessage(), e);
        }

        return result;
    }

    /**
     * Check if the market data service is enabled and configured.
     */
    public boolean isConfigured() {
        return enabled && (
            (finnhubApiKey != null && !finnhubApiKey.isEmpty()) ||
            (alphaVantageApiKey != null && !alphaVantageApiKey.isEmpty())
        );
    }

    /**
     * Get available API sources.
     */
    public String getAvailableSources() {
        List<String> sources = new ArrayList<>();
        if (finnhubApiKey != null && !finnhubApiKey.isEmpty()) {
            sources.add("Finnhub");
        }
        if (alphaVantageApiKey != null && !alphaVantageApiKey.isEmpty()) {
            sources.add("Alpha Vantage");
        }
        return sources.isEmpty() ? "None" : String.join(", ", sources);
    }
}
