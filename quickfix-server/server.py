#!/usr/bin/env python3
"""
Simple QuickFIX Test Server
Accepts connections and sends test FIX messages
"""

import quickfix
import quickfix as fix
import logging
import sys
import time
from datetime import datetime

logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

class QuickFixServer(fix.Application):
    def __init__(self):
        fix.Application.__init__(self)
        self.session_id = None
        self.message_count = 0

    def onCreate(self, sessionID):
        logger.info(f"Session Created: {sessionID}")
        self.session_id = sessionID

    def onLogon(self, sessionID):
        logger.info(f"Logon: {sessionID}")
        self.session_id = sessionID
        
    def onLogout(self, sessionID):
        logger.info(f"Logout: {sessionID}")

    def toAdmin(self, message, sessionID):
        logger.info(f"ToAdmin: {message}")

    def fromAdmin(self, message, sessionID):
        logger.info(f"FromAdmin: {message}")

    def toApp(self, message, sessionID):
        logger.info(f"ToApp: {message}")

    def fromApp(self, message, sessionID):
        logger.info(f"FromApp: {message}")
        
        # Send a test response
        if self.session_id:
            try:
                response = fix.Message()
                response.getHeader().setField(fix.BeginString("FIX.4.2"))
                response.getHeader().setField(fix.MsgType("0"))  # Heartbeat
                fix.Session.sendToTarget(response, self.session_id)
                logger.info(f"Sent test response: {response}")
            except Exception as e:
                logger.error(f"Error sending response: {e}")

def main():
    try:
        # Create server settings
        settings = fix.SessionSettings("server.cfg")
        
        # Create store and log factories
        storeFactory = fix.FileStoreFactory(settings)
        logFactory = fix.ScreenLogFactory(True, True, True, True)
        
        # Create application
        app = QuickFixServer()
        
        # Create initiator (acting as acceptor for incoming connections)
        initiator = fix.SocketInitiator(app, storeFactory, settings, logFactory)
        
        logger.info("QuickFIX Test Server starting...")
        initiator.start()
        
        logger.info("Server is running. Press Ctrl+C to stop.")
        while True:
            time.sleep(1)
            
    except Exception as e:
        logger.error(f"Error: {e}", exc_info=True)
    finally:
        try:
            initiator.stop()
        except:
            pass

if __name__ == "__main__":
    main()
