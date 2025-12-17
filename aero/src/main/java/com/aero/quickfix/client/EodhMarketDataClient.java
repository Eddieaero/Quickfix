package com.aero.quickfix.client;

import com.aero.quickfix.dto.EodhCompanyDataResponse;
import com.aero.quickfix.dto.EodhPriceResponse;
import com.aero.quickfix.dto.MarketPriceDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.http.HttpStatus;

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

    @Value("${eodhd.api.key:demo}")
    private String apiKey;

    @Value("${eodhd.enabled:true}")
    private boolean enabled;

    private final RestTemplate restTemplate;

    public EodhMarketDataClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetch latest end-of-day price for a symbol.
     * @param symbol Stock symbol (e.g., CRDB.TZ for Tanzania stocks)
     * @return MarketPriceDto with current price and metadata
     */
    public MarketPriceDto fetchLatestPrice(String symbol) {
        if (!enabled || "demo".equals(apiKey)) {
            return createDemoPrice(symbol);
        }

        try {
            String url = String.format("%s%s?api_token=%s&fmt=json",
                    EODHD_BASE_URL,
                    String.format(EOD_ENDPOINT, symbol),
                    apiKey);

            EodhPriceResponse response = restTemplate.getForObject(url, EodhPriceResponse.class);

            if (response != null && response.getClose() != null) {
                return mapToMarketPriceDto(symbol, response);
            }

            return new MarketPriceDto(symbol, "No price data available");
        } catch (HttpClientErrorException.NotFound e) {
            return new MarketPriceDto(symbol, "Symbol not found: " + symbol);
        } catch (Exception e) {
            return new MarketPriceDto(symbol, "Error fetching price: " + e.getMessage());
        }
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
        MarketPriceDto dto = new MarketPriceDto(symbol, response.getClose());
        dto.setHigh(response.getHigh());
        dto.setLow(response.getLow());
        dto.setVolume(new BigDecimal(response.getVolume() != null ? response.getVolume() : 0));
        return dto;
    }

    /**
     * Create demo price data for testing without API key.
     */
    private MarketPriceDto createDemoPrice(String symbol) {
        // Demo prices for Tanzania stocks
        BigDecimal demoPrice = switch (symbol.toUpperCase()) {
            case "CRDB.TZ" -> new BigDecimal("1150.00");
            case "NMB.TZ" -> new BigDecimal("720.00");
            case "TBL.TZ" -> new BigDecimal("32.50");
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
