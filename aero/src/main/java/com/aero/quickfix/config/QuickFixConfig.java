package com.aero.quickfix.config;

import com.aero.quickfix.repository.TradeDataRepository;
import com.aero.quickfix.websocket.TradeWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import quickfix.*;

import java.io.IOException;

/**
 * Configuration class for QuickFIX/J client setup.
 * Initializes and manages the FIX protocol initiator connection.
 */
@Configuration
public class QuickFixConfig {

    private static final Logger log = LoggerFactory.getLogger(QuickFixConfig.class);

    @Value("${quickfix.config.file:classpath:quickfix-client.cfg}")
    private String configFile;

    @Value("${quickfix.enabled:true}")
    private boolean quickfixEnabled;

    private final ResourceLoader resourceLoader;

    public QuickFixConfig(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /**
     * Creates a SocketInitiator bean for FIX protocol communication.
     * This initiator connects to a FIX server.
     */
    @Bean
    public SocketInitiator socketInitiator(TradeDataRepository tradeDataRepository, TradeWebSocketHandler webSocketHandler) throws ConfigError, IOException {
        if (!quickfixEnabled) {
            log.info("QuickFIX is disabled in configuration");
            return null;
        }

        try {
            var resource = resourceLoader.getResource(configFile);
            var sessionSettings = new SessionSettings(resource.getInputStream());

            var messageFactory = new DefaultMessageFactory();
            var application = new QuickFixApplicationAdapter(tradeDataRepository, webSocketHandler);
            
            var storeFactory = new FileStoreFactory(sessionSettings);
            var logFactory = new ScreenLogFactory(true, true, true, true);

            var initiator = new SocketInitiator(
                    application,
                    storeFactory,
                    sessionSettings,
                    logFactory,
                    messageFactory
            );

            log.info("SocketInitiator created successfully");
            return initiator;
        } catch (Exception e) {
            log.error("Failed to initialize SocketInitiator", e);
            throw new ConfigError("Failed to initialize SocketInitiator: " + e.getMessage(), e);
        }
    }

}
