#!/usr/bin/env python3
"""
Simple QuickFIX Test Server - No Docker required
Listens on port 9878 and accepts FIX connections
"""

import socket
import threading
import time
import logging
from datetime import datetime

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)

class SimpleFIXServer:
    def __init__(self, host='127.0.0.1', port=9878):
        self.host = host
        self.port = port
        self.server_socket = None
        self.running = False
        self.clients = []

    def start(self):
        self.server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.server_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.server_socket.bind((self.host, self.port))
        self.server_socket.listen(5)
        self.running = True
        
        logger.info(f"QuickFIX Test Server listening on {self.host}:{self.port}")
        
        # Accept connections in a separate thread
        accept_thread = threading.Thread(target=self._accept_connections, daemon=True)
        accept_thread.start()
        
        try:
            while self.running:
                time.sleep(1)
        except KeyboardInterrupt:
            self.stop()

    def _accept_connections(self):
        while self.running:
            try:
                client_socket, addr = self.server_socket.accept()
                logger.info(f"Client connected from {addr}")
                self.clients.append(client_socket)
                
                # Handle client in separate thread
                client_thread = threading.Thread(
                    target=self._handle_client,
                    args=(client_socket, addr),
                    daemon=True
                )
                client_thread.start()
            except Exception as e:
                if self.running:
                    logger.error(f"Error accepting connection: {e}")

    def _handle_client(self, client_socket, addr):
        try:
            logger.info(f"Handling client from {addr}")
            
            # Send Logon response (FIX message)
            logon_msg = self._create_logon_message()
            client_socket.send(logon_msg.encode())
            logger.info(f"Sent Logon to {addr}: {logon_msg}")
            
            # Keep connection alive and send periodic messages
            counter = 1
            symbols = ['AAPL', 'GOOGL', 'MSFT', 'TSLA', 'AMZN']
            sides = ['1', '2']  # 1=BUY, 2=SELL
            
            for i in range(60):  # Send messages for 5 minutes
                time.sleep(5)
                
                # Send heartbeat
                heartbeat_msg = self._create_heartbeat_message(2 + counter)
                try:
                    client_socket.send(heartbeat_msg.encode())
                    logger.info(f"Sent Heartbeat to {addr}")
                except:
                    break
                counter += 1
                
                # Send order every 2 iterations
                if i % 2 == 0:
                    order_msg = self._create_new_order_message(
                        order_id=f"ORD{1000+i}",
                        symbol=symbols[i % len(symbols)],
                        side=sides[i % 2],
                        qty=50 + (i * 10) % 200,
                        price=100 + (i * 2) % 50,
                        seq=3 + counter
                    )
                    try:
                        client_socket.send(order_msg.encode())
                        logger.info(f"Sent New Order to {addr}: {order_msg[:80]}")
                    except:
                        break
                    counter += 1
                
                # Send execution report every 3 iterations
                if i % 3 == 0:
                    exec_msg = self._create_execution_report(
                        order_id=f"ORD{1000+i}",
                        symbol=symbols[i % len(symbols)],
                        side=sides[i % 2],
                        qty=50 + (i * 10) % 200,
                        price=100 + (i * 2) % 50,
                        exec_qty=25 + (i * 5) % 100,
                        exec_price=100 + (i * 2) % 50,
                        ord_status='1',  # Partially filled
                        seq=3 + counter
                    )
                    try:
                        client_socket.send(exec_msg.encode())
                        logger.info(f"Sent Execution Report to {addr}: {exec_msg[:80]}")
                    except:
                        break
                    counter += 1
                
        except Exception as e:
            logger.error(f"Error handling client {addr}: {e}")
        finally:
            try:
                client_socket.close()
                logger.info(f"Client disconnected: {addr}")
            except:
                pass

    def _create_logon_message(self):
        """Create a simple FIX Logon message"""
        return "8=FIX.4.2|9=100|35=A|49=TARGET|56=SENDER|34=1|52=20250101-00:00:00|108=30|10=000|\n"

    def _create_heartbeat_message(self, seq=2):
        """Create a simple FIX Heartbeat message"""
        timestamp = datetime.utcnow().strftime('%Y%m%d-%H:%M:%S')
        return f"8=FIX.4.2|9=50|35=0|49=TARGET|56=SENDER|34={seq}|52={timestamp}|10=000|\n"

    def _create_new_order_message(self, order_id, symbol, side, qty, price, seq):
        """Create a FIX New Order message (35=D) with proper format"""
        timestamp = datetime.utcnow().strftime('%Y%m%d-%H:%M:%S')
        # FIX format: tag=value|tag=value
        # 35=D (New Order Single)
        # 11=ClOrdID (Client Order ID)
        # 55=Symbol
        # 54=Side (1=Buy, 2=Sell)
        # 38=OrderQty
        # 40=OrdType (2=Limit)
        # 44=Price
        body = f"35=D|49=TARGET|56=SENDER|34={seq}|52={timestamp}|11={order_id}|55={symbol}|54={side}|38={int(qty)}|40=2|44={price:.2f}"
        
        # Calculate simple checksum (sum of all characters mod 256)
        checksum = sum(ord(c) for c in body) % 256
        
        # Build complete message with BeginString, BodyLength, and Checksum
        begin_string = "8=FIX.4.2"
        body_length = len(body)
        msg = f"{begin_string}|9={body_length}|{body}|10={checksum:03d}|\n"
        return msg

    def _create_execution_report(self, order_id, symbol, side, qty, price, exec_qty, exec_price, ord_status, seq):
        """Create a FIX Execution Report message (35=8) with proper format"""
        timestamp = datetime.utcnow().strftime('%Y%m%d-%H:%M:%S')
        # 35=8 (Execution Report)
        # 37=OrderID (exchange order ID)
        # 39=OrdStatus (0=New, 1=Partially filled, 2=Filled, 4=Cancelled, 8=Rejected)
        # 150=ExecType (2=Trade)
        # 151=LeavesQty
        # 14=CumQty
        # 32=LastQty
        # 31=LastPx
        body = f"35=8|49=TARGET|56=SENDER|34={seq}|52={timestamp}|37={order_id}|55={symbol}|54={side}|38={int(qty)}|40=2|44={price:.2f}|39={ord_status}|150=2|151={int(qty-exec_qty)}|14={int(exec_qty)}|32={int(exec_qty)}|31={exec_price:.2f}"
        
        checksum = sum(ord(c) for c in body) % 256
        begin_string = "8=FIX.4.2"
        body_length = len(body)
        msg = f"{begin_string}|9={body_length}|{body}|10={checksum:03d}|\n"
        return msg

    def _create_test_message(self):
        """Create a test FIX message (New Order)"""
        timestamp = datetime.utcnow().strftime('%Y%m%d-%H:%M:%S')
        return f"8=FIX.4.2|9=150|35=D|49=TARGET|56=SENDER|34=3|52={timestamp}|11=ORDER123|55=AAPL|54=1|38=100|40=2|44=150.50|10=000|\n"

    def stop(self):
        logger.info("Shutting down server...")
        self.running = False
        for client in self.clients:
            try:
                client.close()
            except:
                pass
        if self.server_socket:
            self.server_socket.close()
        logger.info("Server stopped")

if __name__ == "__main__":
    server = SimpleFIXServer()
    server.start()
