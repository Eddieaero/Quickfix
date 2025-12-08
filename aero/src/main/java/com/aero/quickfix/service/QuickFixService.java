package com.aero.quickfix.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import quickfix.ConfigError;
import quickfix.SessionNotFound;
import quickfix.SocketInitiator;

/**
 * Service class for managing QuickFIX connections and operations.
 * Handles lifecycle management of the FIX protocol initiator.
 */
@Service
public class QuickFixService {

    private static final Logger log = LoggerFactory.getLogger(QuickFixService.class);

    private final SocketInitiator socketInitiator;
    private boolean isConnected = false;

    public QuickFixService() {
        this.socketInitiator = null;
    }

    @Autowired(required = false)
    public QuickFixService(SocketInitiator socketInitiator) {
        this.socketInitiator = socketInitiator;
    }

    /**
     * Start the QuickFIX connection.
     */
    public void startConnection() throws ConfigError, InterruptedException {
        if (socketInitiator == null) {
            log.warn("QuickFIX is disabled. SocketInitiator is not available.");
            return;
        }

        if (!isConnected) {
            try {
                socketInitiator.start();
                isConnected = true;
                log.info("QuickFIX connection started successfully");
            } catch (Exception e) {
                log.error("Failed to start QuickFIX connection", e);
                throw e;
            }
        } else {
            log.warn("QuickFIX connection is already active");
        }
    }

    /**
     * Stop the QuickFIX connection.
     */
    public void stopConnection() throws SessionNotFound {
        if (socketInitiator == null) {
            log.warn("QuickFIX is disabled. SocketInitiator is not available.");
            return;
        }

        if (isConnected) {
            try {
                socketInitiator.stop();
                isConnected = false;
                log.info("QuickFIX connection stopped successfully");
            } catch (Exception e) {
                log.error("Failed to stop QuickFIX connection", e);
                throw e;
            }
        } else {
            log.warn("QuickFIX connection is not active");
        }
    }

    /**
     * Check if QuickFIX is connected.
     */
    public boolean isConnected() {
        return isConnected && socketInitiator != null;
    }

}
