package com.aero.quickfix.config;

import com.aero.quickfix.model.TradeData;
import com.aero.quickfix.repository.TradeDataRepository;
import com.aero.quickfix.websocket.TradeWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.MsgType;

/**
 * QuickFIX/J Application adapter that handles FIX protocol callbacks.
 * Implements the core Application interface for message handling.
 */
public class QuickFixApplicationAdapter implements Application {

    private static final Logger log = LoggerFactory.getLogger(QuickFixApplicationAdapter.class);
    private final TradeDataRepository tradeDataRepository;
    private final TradeWebSocketHandler webSocketHandler;

    public QuickFixApplicationAdapter(TradeDataRepository tradeDataRepository, TradeWebSocketHandler webSocketHandler) {
        this.tradeDataRepository = tradeDataRepository;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void onCreate(SessionID sessionID) {
        log.info("FIX Session created: {}", sessionID);
    }

    @Override
    public void onLogon(SessionID sessionID) {
        log.info("FIX Logon successful: {}", sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        log.info("FIX Logout: {}", sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
        log.debug("Sending admin message: {}", message);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        log.debug("Received admin message: {}", message);
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        log.debug("Sending app message to {}: {}", sessionID, message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID)
            throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        log.info("Received message from {}: {}", sessionID, message);
        
        // Extract and store trade data from FIX messages
        try {
            String msgType = message.getHeader().getString(MsgType.FIELD);
            
            // Handle Execution Report (35=8) - Order execution/status update
            if ("8".equals(msgType)) {
                extractExecutionReport(message);
            }
            // Handle New Order (35=D) - New order
            else if ("D".equals(msgType)) {
                extractNewOrder(message);
            }
        } catch (Exception e) {
            log.debug("Could not extract trade data from message: {}", e.getMessage());
        }
    }

    private void extractExecutionReport(Message message) throws FieldNotFound {
        try {
            String orderId = message.getString(quickfix.field.OrderID.FIELD);
            String symbol = message.getString(quickfix.field.Symbol.FIELD);
            String side = message.getString(quickfix.field.Side.FIELD);
            double orderQty = message.getDouble(quickfix.field.OrderQty.FIELD);
            double price = message.getDouble(quickfix.field.Price.FIELD);
            String ordStatus = message.getString(quickfix.field.OrdStatus.FIELD);
            
            double lastQty = 0;
            double lastPx = 0;
            
            try {
                lastQty = message.getDouble(quickfix.field.LastQty.FIELD);
                lastPx = message.getDouble(quickfix.field.LastPx.FIELD);
            } catch (FieldNotFound e) {
                // Field not present, that's ok
            }
            
            TradeData tradeData = new TradeData(orderId, symbol, "1".equals(side) ? "SELL" : "BUY", 
                                                orderQty, price, mapOrderStatus(ordStatus), "ExecutionReport");
            tradeData.setExecutedQty(lastQty);
            if (lastQty > 0) {
                tradeData.setExecutedPrice(lastPx);
            }
            
            tradeDataRepository.save(tradeData);
            webSocketHandler.broadcastTradeUpdate();
            log.info("Stored execution report: OrderID={}, Symbol={}, Status={}, ExecQty={}", 
                     orderId, symbol, ordStatus, lastQty);
        } catch (Exception e) {
            log.debug("Could not parse execution report: {}", e.getMessage());
        }
    }

    private void extractNewOrder(Message message) throws FieldNotFound {
        try {
            String orderId = message.getString(quickfix.field.ClOrdID.FIELD);
            String symbol = message.getString(quickfix.field.Symbol.FIELD);
            String side = message.getString(quickfix.field.Side.FIELD);
            double orderQty = message.getDouble(quickfix.field.OrderQty.FIELD);
            double price = message.getDouble(quickfix.field.Price.FIELD);
            
            TradeData tradeData = new TradeData(orderId, symbol, "1".equals(side) ? "SELL" : "BUY", 
                                                orderQty, price, "NEW", "NewOrder");
            
            tradeDataRepository.save(tradeData);
            webSocketHandler.broadcastTradeUpdate();
            log.info("Stored new order: OrderID={}, Symbol={}, Side={}, Qty={}, Price={}", 
                     orderId, symbol, side, orderQty, price);
        } catch (Exception e) {
            log.debug("Could not parse new order: {}", e.getMessage());
        }
    }

    private String mapOrderStatus(String fixStatus) {
        return switch(fixStatus) {
            case "0" -> "NEW";
            case "1" -> "PARTIALLY_FILLED";
            case "2" -> "FILLED";
            case "4" -> "CANCELLED";
            case "8" -> "REJECTED";
            default -> fixStatus;
        };
    }
}