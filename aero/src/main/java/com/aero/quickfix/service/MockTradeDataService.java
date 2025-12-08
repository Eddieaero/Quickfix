package com.aero.quickfix.service;

import com.aero.quickfix.model.TradeData;
import com.aero.quickfix.repository.TradeDataRepository;
import org.springframework.stereotype.Service;
import java.util.Random;

/**
 * Service for generating mock trade data for demonstration purposes.
 */
@Service
public class MockTradeDataService {
    
    private final TradeDataRepository tradeDataRepository;
    private final Random random = new Random();
    private static final String[] SYMBOLS = {"AAPL", "GOOGL", "MSFT", "TSLA", "AMZN", "META", "NVDA"};
    private static final String[] SIDES = {"BUY", "SELL"};
    
    public MockTradeDataService(TradeDataRepository tradeDataRepository) {
        this.tradeDataRepository = tradeDataRepository;
        generateMockData();
    }
    
    private void generateMockData() {
        // Generate some initial mock trades for display
        for (int i = 0; i < 15; i++) {
            String symbol = SYMBOLS[random.nextInt(SYMBOLS.length)];
            String side = SIDES[random.nextInt(SIDES.length)];
            double qty = 50 + (random.nextDouble() * 200);
            double price = 80 + (random.nextDouble() * 200);
            double execQty = qty * (0.3 + random.nextDouble() * 0.7);
            double execPrice = price + (random.nextDouble() * 10 - 5);
            String status = random.nextDouble() > 0.5 ? "FILLED" : "PARTIALLY_FILLED";
            
            TradeData trade = new TradeData(
                "ORD" + (1000 + i),
                symbol,
                side,
                qty,
                price,
                status,
                random.nextDouble() > 0.6 ? "ExecutionReport" : "NewOrder"
            );
            trade.setExecutedQty(execQty);
            trade.setExecutedPrice(execPrice);
            trade.setTimestamp(System.currentTimeMillis() - (i * 30000)); // Spread over time
            
            tradeDataRepository.save(trade);
        }
    }
    
    public void addMockTrade() {
        String symbol = SYMBOLS[random.nextInt(SYMBOLS.length)];
        String side = SIDES[random.nextInt(SIDES.length)];
        double qty = 50 + (random.nextDouble() * 200);
        double price = 80 + (random.nextDouble() * 200);
        
        TradeData trade = new TradeData(
            "ORD" + System.currentTimeMillis(),
            symbol,
            side,
            qty,
            price,
            "NEW",
            "NewOrder"
        );
        
        tradeDataRepository.save(trade);
    }
}
