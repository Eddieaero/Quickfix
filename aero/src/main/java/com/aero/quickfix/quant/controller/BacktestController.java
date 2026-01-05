package com.aero.quickfix.quant.controller;

import com.aero.quickfix.quant.dto.BacktestRequestDTO;
import com.aero.quickfix.quant.dto.BacktestResultDTO;
import com.aero.quickfix.quant.model.BacktestResults;
import com.aero.quickfix.quant.service.BacktestEngine;
import com.aero.quickfix.quant.strategy.Strategy;
import com.aero.quickfix.quant.strategy.impl.SmaCrossoverStrategy;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Backtest REST API Controller
 * Endpoints for running and retrieving backtests
 */
@RestController
@RequestMapping("/api/quant")
public class BacktestController {

    private static final Logger logger = LoggerFactory.getLogger(BacktestController.class);

    @Autowired
    private BacktestEngine backtestEngine;

    @Autowired
    private SmaCrossoverStrategy smaCrossoverStrategy;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * GET /api/quant/strategies
     * List all available strategies
     */
    @GetMapping("/strategies")
    public ResponseEntity<?> getAvailableStrategies() {
        try {
            List<Map<String, Object>> strategies = new ArrayList<>();

            Map<String, Object> smaStrategy = new HashMap<>();
            smaStrategy.put("name", smaCrossoverStrategy.getName());
            smaStrategy.put("description", smaCrossoverStrategy.getDescription());
            smaStrategy.put("minimumBars", smaCrossoverStrategy.getMinimumBars());
            smaStrategy.put("requiredIndicators", smaCrossoverStrategy.getRequiredIndicators());
            strategies.add(smaStrategy);

            return ResponseEntity.ok(Map.of(
                "count", strategies.size(),
                "strategies", strategies
            ));
        } catch (Exception e) {
            logger.error("Error fetching strategies", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * POST /api/quant/backtest
     * Run a new backtest
     */
    @PostMapping("/backtest")
    public ResponseEntity<?> runBacktest(@RequestBody BacktestRequestDTO request) {
        try {
            // Validate request
            if (request.getStrategyName() == null || request.getStrategyName().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "strategyName is required"));
            }
            if (request.getSymbol() == null || request.getSymbol().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "symbol is required"));
            }
            if (request.getStartDate() == null || request.getEndDate() == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "startDate and endDate are required"));
            }
            if (request.getInitialCapital() == null || request.getInitialCapital().doubleValue() <= 0) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "initialCapital must be greater than 0"));
            }

            // Get strategy
            Strategy strategy = getStrategy(request.getStrategyName());
            if (strategy == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Unknown strategy: " + request.getStrategyName()));
            }

            logger.info("Running backtest - Strategy: {}, Symbol: {}, Period: {} to {}", 
                request.getStrategyName(), request.getSymbol(), request.getStartDate(), request.getEndDate());

            // Run backtest
            BacktestResults results = backtestEngine.runBacktest(
                strategy,
                request.getSymbol(),
                request.getStartDate(),
                request.getEndDate(),
                request.getInitialCapital()
            );

            // Convert to DTO
            BacktestResultDTO resultDTO = modelMapper.map(results, BacktestResultDTO.class);

            return ResponseEntity.ok(resultDTO);

        } catch (Exception e) {
            logger.error("Error running backtest", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/quant/backtest/{id}
     * Get backtest results by ID
     */
    @GetMapping("/backtest/{id}")
    public ResponseEntity<?> getBacktestResults(@PathVariable String id) {
        try {
            BacktestResults results = backtestEngine.getBacktestResults(UUID.fromString(id));
            if (results == null) {
                return ResponseEntity.notFound().build();
            }

            BacktestResultDTO resultDTO = modelMapper.map(results, BacktestResultDTO.class);
            return ResponseEntity.ok(resultDTO);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Invalid backtest ID format"));
        } catch (Exception e) {
            logger.error("Error fetching backtest results", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/quant/backtest/strategy/{strategyName}
     * Get all backtests for a specific strategy
     */
    @GetMapping("/backtest/strategy/{strategyName}")
    public ResponseEntity<?> getBacktestsByStrategy(@PathVariable String strategyName) {
        try {
            List<BacktestResults> results = backtestEngine.getBacktestsByStrategy(strategyName);
            List<BacktestResultDTO> resultDTOs = results.stream()
                .map(r -> modelMapper.map(r, BacktestResultDTO.class))
                .collect(Collectors.toList());

            return ResponseEntity.ok(Map.of(
                "count", resultDTOs.size(),
                "results", resultDTOs
            ));

        } catch (Exception e) {
            logger.error("Error fetching backtest results", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * GET /api/quant/health
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "component", "Quantitative Analysis Engine",
            "features", Arrays.asList(
                "Technical Indicators",
                "Strategy Framework",
                "Backtesting Engine",
                "Performance Metrics"
            )
        ));
    }

    /**
     * Helper method to get strategy by name
     */
    private Strategy getStrategy(String strategyName) {
        switch (strategyName.toLowerCase()) {
            case "sma crossover":
            case "sma_crossover":
                return smaCrossoverStrategy;
            default:
                return null;
        }
    }
}
