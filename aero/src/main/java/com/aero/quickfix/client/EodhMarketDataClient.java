package com.aero.quickfix.client;

import com.aero.quickfix.dto.EodhCompanyDataResponse;
import com.aero.quickfix.dto.EodhPriceResponse;
import com.aero.quickfix.dto.MarketPriceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * EODHD API client for fetching market data.
 * Handles authentication and API calls to EODHD endpoints.
 */
@Component
public class EodhMarketDataClient {

    private static final String EODHD_BASE_URL = "https://eodhd.com/api";
    private static final String FUNDAMENTAL_ENDPOINT = "/fundamentals/%s";
    private static final String EOD_ENDPOINT = "/eod/%s";
    private static final Logger logger = LoggerFactory.getLogger(EodhMarketDataClient.class);

    @Value("${eodhd.api.key:demo}")
    private String apiKey;

    @Value("${eodhd.enabled:true}")
    private boolean enabled;

    private final RestTemplate restTemplate;

    public EodhMarketDataClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        logInitialization();
    }

    private void logInitialization() {
        String keyPreview = (apiKey != null && !"demo".equals(apiKey)) 
            ? apiKey.substring(0, Math.min(4, apiKey.length())) + "..." 
            : apiKey;
        logger.info("EodhMarketDataClient initialized - API Key: {}, Enabled: {}", keyPreview, enabled);
    }

    /**
     * Fetch latest end-of-day price for a symbol.
     * @param symbol Stock symbol (e.g., CRDB.TZ for Tanzania stocks)
     * @return MarketPriceDto with current price and metadata
     */
    public MarketPriceDto fetchLatestPrice(String symbol) {
        if (!enabled) {
            logger.debug("Market data disabled for symbol: {}", symbol);
            return new MarketPriceDto(symbol, "Market data service is disabled");
        }

        try {
            // Normalize symbol format for EODHD API
            String formattedSymbol = normalizeSymbol(symbol);
            String url = String.format("%s%s?api_token=%s&fmt=json",
                    EODHD_BASE_URL,
                    String.format(EOD_ENDPOINT, formattedSymbol),
                    apiKey);

            logger.debug("Fetching price from EODHD for: {} (formatted as: {})", symbol, formattedSymbol);
            
            // EODHD returns an array, get the first (latest) element
            EodhPriceResponse[] responses = restTemplate.getForObject(url, EodhPriceResponse[].class);

            if (responses != null && responses.length > 0) {
                EodhPriceResponse response = responses[0];
                if (response.getClose() != null) {
                    logger.debug("Received price for {}: {}", symbol, response.getClose());
                    return mapToMarketPriceDto(symbol, response);
                }
            }

            logger.warn("No price data available for symbol: {}", symbol);
            return new MarketPriceDto(symbol, "No price data available for symbol: " + symbol);
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Symbol not found in EODHD API: {} (HTTP 404) - Symbol may not be supported", symbol);
            return new MarketPriceDto(symbol, "Symbol not supported by EODHD: " + symbol);
        } catch (HttpClientErrorException e) {
            logger.warn("HTTP error {} fetching price for {}: {}", e.getStatusCode(), symbol, e.getResponseBodyAsString());
            return new MarketPriceDto(symbol, "HTTP " + e.getStatusCode() + ": " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error fetching price for {}: {}", symbol, e.getMessage(), e);
            return new MarketPriceDto(symbol, "Error: " + e.getMessage());
        }
    }

    /**
     * Normalize symbol format for EODHD API.
     * EODHD requires specific formats: CRDB.TZ, AAPL, GOOGL, etc.
     */
    private String normalizeSymbol(String symbol) {
        if (symbol == null) return symbol;
        
        symbol = symbol.trim().toUpperCase();
        
        // If already has exchange code (contains dot), return as-is
        if (symbol.contains(".")) {
            return symbol;
        }
        
        // Check if it's a known DSE stock without exchange code, add .TZ
        if (isDseStock(symbol)) {
            logger.debug("Recognized DSE stock: {}, adding .TZ suffix", symbol);
            return symbol + ".TZ";
        }
        
        // For US/international stocks, EODHD usually accepts without exchange code
        // Examples: AAPL, GOOGL work without .US suffix
        logger.debug("International stock format: {} (using as-is)", symbol);
        return symbol;
    }

    /**
     * Check if symbol is a known DSE (Tanzania) stock.
     */
    private boolean isDseStock(String symbol) {
        return switch (symbol) {
            case "CRDB", "NMB", "TBL", "TPLF", "SIMBA", "UCHM", "SWALLOW",
                 "KCB", "AIRTEL", "VODACOM", "TTCL", "TANZCEM", "MOGAS", "DACB" -> true;
            default -> false;
        };
    }

    /**
     * Fetch company fundamental data including dividends and P/E ratio.
     * @param symbol Stock symbol
     * @return EodhCompanyDataResponse with company fundamentals
     */
    public EodhCompanyDataResponse fetchCompanyFundamentals(String symbol) {
        if (!enabled || "demo".equals(apiKey)) {
            return createDemoFundamentals(symbol);
        }

        try {
            String url = String.format("%s%s?api_token=%s&fmt=json",
                    EODHD_BASE_URL,
                    String.format(FUNDAMENTAL_ENDPOINT, symbol),
                    apiKey);

            return restTemplate.getForObject(url, EodhCompanyDataResponse.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Validate if a symbol exists in the market.
     * @param symbol Stock symbol to validate
     * @return true if symbol is valid, false otherwise
     */
    public boolean validateSymbol(String symbol) {
        if (!enabled || "demo".equals(apiKey)) {
            return true; // Demo mode accepts all
        }

        try {
            MarketPriceDto price = fetchLatestPrice(symbol);
            return price.isValid();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Map EODHD response to MarketPriceDto.
     */
    private MarketPriceDto mapToMarketPriceDto(String symbol, EodhPriceResponse response) {
        MarketPriceDto dto = new MarketPriceDto(symbol, response.getClose() != null ? new BigDecimal(response.getClose()) : null);
        if (response.getHigh() != null) dto.setHigh(new BigDecimal(response.getHigh()));
        if (response.getLow() != null) dto.setLow(new BigDecimal(response.getLow()));
        if (response.getVolume() != null) dto.setVolume(new BigDecimal(response.getVolume()));
        return dto;
    }

    /**
     * Create demo price data for testing without API key.
     */
    private MarketPriceDto createDemoPrice(String symbol) {
        // Demo prices for Tanzania DSE stocks
        BigDecimal demoPrice = switch (symbol.toUpperCase()) {
            // Major Banks
            case "CRDB.TZ" -> new BigDecimal("1150.00");
            case "NMB.TZ" -> new BigDecimal("720.00");
            case "TBL.TZ" -> new BigDecimal("32.50");
            case "TPLF.TZ" -> new BigDecimal("2800.00");
            case "SIMBA.TZ" -> new BigDecimal("1050.00");
            case "UCHM.TZ" -> new BigDecimal("18.50");
            case "SWALLOW.TZ" -> new BigDecimal("850.00");
            case "KCB.TZ" -> new BigDecimal("28.00");
            case "AIRTEL.TZ" -> new BigDecimal("950.00");
            case "VODACOM.TZ" -> new BigDecimal("850.00");
            case "TTCL.TZ" -> new BigDecimal("22.50");
            case "TANZCEM.TZ" -> new BigDecimal("3500.00");
            case "MOGAS.TZ" -> new BigDecimal("2100.00");
            case "DACB.TZ" -> new BigDecimal("850.00");
            case "Tanzania.TZ" -> new BigDecimal("850.00");
            // International stocks
            case "AAPL" -> new BigDecimal("192.50");
            case "GOOGL" -> new BigDecimal("140.75");
            default -> new BigDecimal("100.00");
        };

        MarketPriceDto dto = new MarketPriceDto(symbol, demoPrice);
        dto.setExchange(symbol.endsWith(".TZ") ? "DSE" : "NASDAQ");
        dto.setCurrency(symbol.endsWith(".TZ") ? "TZS" : "USD");
        dto.setValid(true);
        return dto;
    }

    /**
     * Create demo fundamentals for testing without API key.
     */
    private EodhCompanyDataResponse createDemoFundamentals(String symbol) {
        EodhCompanyDataResponse response = new EodhCompanyDataResponse();

        EodhCompanyDataResponse.General general = new EodhCompanyDataResponse.General();
        general.setCode(symbol);
        general.setExchange(symbol.endsWith(".TZ") ? "DSE" : "NASDAQ");
        general.setCurrencyCode(symbol.endsWith(".TZ") ? "TZS" : "USD");
        general.setName(symbol + " Company");
        general.setSector("Financial Services");
        response.setGeneral(general);

        EodhCompanyDataResponse.Highlights highlights = new EodhCompanyDataResponse.Highlights();
        highlights.setDividendYield(new BigDecimal("0.045")); // 4.5% dividend
        highlights.setPeRatio(new BigDecimal("8.5"));
        response.setHighlights(highlights);

        return response;
    }
}
