-- TimescaleDB initialization script for Aero QuickFIX Quant Platform
-- Creates hypertables for OHLCV data and other time-series data

-- Enable TimescaleDB extension
CREATE EXTENSION IF NOT EXISTS timescaledb CASCADE;

-- ============================================================================
-- OHLCV Data Table (Open, High, Low, Close, Volume)
-- ============================================================================
CREATE TABLE IF NOT EXISTS ohlcv_data (
    time TIMESTAMPTZ NOT NULL,
    symbol TEXT NOT NULL,
    open DECIMAL(15, 8) NOT NULL,
    high DECIMAL(15, 8) NOT NULL,
    low DECIMAL(15, 8) NOT NULL,
    close DECIMAL(15, 8) NOT NULL,
    volume BIGINT NOT NULL,
    adjusted_close DECIMAL(15, 8),
    dividend DECIMAL(15, 8),
    split_coefficient DECIMAL(15, 8),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Create TimescaleDB hypertable for OHLCV data
SELECT create_hypertable('ohlcv_data', 'time', 
    chunk_time_interval => INTERVAL '1 day',
    if_not_exists => TRUE);

-- Create composite index for efficient queries by symbol and time
CREATE INDEX IF NOT EXISTS idx_ohlcv_symbol_time 
    ON ohlcv_data (symbol, time DESC);

-- ============================================================================
-- Technical Indicators Cache Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS technical_indicators (
    time TIMESTAMPTZ NOT NULL,
    symbol TEXT NOT NULL,
    period INTEGER NOT NULL,
    indicator_type TEXT NOT NULL,
    indicator_name TEXT NOT NULL,
    value DECIMAL(15, 8),
    signal_value DECIMAL(15, 8),
    histogram DECIMAL(15, 8),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Create hypertable for indicators
SELECT create_hypertable('technical_indicators', 'time',
    chunk_time_interval => INTERVAL '7 days',
    if_not_exists => TRUE);

-- Composite index for fast lookups
CREATE INDEX IF NOT EXISTS idx_indicators_symbol_type
    ON technical_indicators (symbol, indicator_type, time DESC);

-- ============================================================================
-- Backtest Results Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS backtest_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    strategy_name TEXT NOT NULL,
    symbol TEXT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    initial_capital DECIMAL(15, 2) NOT NULL,
    final_value DECIMAL(15, 2) NOT NULL,
    total_return DECIMAL(10, 4) NOT NULL,
    annual_return DECIMAL(10, 4),
    sharpe_ratio DECIMAL(10, 4),
    sortino_ratio DECIMAL(10, 4),
    max_drawdown DECIMAL(10, 4),
    win_rate DECIMAL(10, 4),
    profit_factor DECIMAL(10, 4),
    total_trades INTEGER,
    winning_trades INTEGER,
    losing_trades INTEGER,
    avg_win DECIMAL(15, 2),
    avg_loss DECIMAL(15, 2),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ============================================================================
-- Trade Log Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS trade_log (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    backtest_id UUID,
    symbol TEXT NOT NULL,
    trade_date DATE NOT NULL,
    entry_price DECIMAL(15, 8) NOT NULL,
    exit_price DECIMAL(15, 8),
    quantity DECIMAL(15, 8) NOT NULL,
    trade_type TEXT NOT NULL, -- LONG or SHORT
    profit_loss DECIMAL(15, 2),
    profit_loss_pct DECIMAL(10, 4),
    trade_status TEXT NOT NULL, -- OPEN or CLOSED
    entry_signal TEXT,
    exit_signal TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    FOREIGN KEY (backtest_id) REFERENCES backtest_results(id)
);

-- ============================================================================
-- Portfolio Snapshot Table (for multi-asset tracking)
-- ============================================================================
CREATE TABLE IF NOT EXISTS portfolio_snapshot (
    time TIMESTAMPTZ NOT NULL,
    backtest_id UUID,
    total_value DECIMAL(15, 2) NOT NULL,
    cash DECIMAL(15, 2) NOT NULL,
    positions_value DECIMAL(15, 2) NOT NULL,
    drawdown DECIMAL(10, 4),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    FOREIGN KEY (backtest_id) REFERENCES backtest_results(id)
);

-- Create hypertable for portfolio snapshots
SELECT create_hypertable('portfolio_snapshot', 'time',
    chunk_time_interval => INTERVAL '1 day',
    if_not_exists => TRUE);

-- ============================================================================
-- Market Events Table (news, earnings, splits, etc.)
-- ============================================================================
CREATE TABLE IF NOT EXISTS market_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    event_date DATE NOT NULL,
    symbol TEXT NOT NULL,
    event_type TEXT NOT NULL, -- EARNINGS, DIVIDEND, SPLIT, NEWS, etc.
    description TEXT,
    impact DECIMAL(10, 4),
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- ============================================================================
-- Strategy Metadata Table
-- ============================================================================
CREATE TABLE IF NOT EXISTS strategies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name TEXT NOT NULL UNIQUE,
    description TEXT,
    strategy_type TEXT NOT NULL, -- MEAN_REVERSION, TREND_FOLLOWING, STATISTICAL_ARBITRAGE
    parameters JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- ============================================================================
-- Create indexes for performance
-- ============================================================================
CREATE INDEX IF NOT EXISTS idx_backtest_strategy ON backtest_results(strategy_name);
CREATE INDEX IF NOT EXISTS idx_backtest_symbol ON backtest_results(symbol);
CREATE INDEX IF NOT EXISTS idx_trade_log_backtest ON trade_log(backtest_id);
CREATE INDEX IF NOT EXISTS idx_trade_log_symbol ON trade_log(symbol);
CREATE INDEX IF NOT EXISTS idx_market_events_symbol ON market_events(symbol, event_date DESC);
CREATE INDEX IF NOT EXISTS idx_market_events_type ON market_events(event_type);

-- Grant permissions to application user
GRANT CONNECT ON DATABASE aero_quant TO aero_user;
GRANT USAGE ON SCHEMA public TO aero_user;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO aero_user;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO aero_user;
GRANT ALL PRIVILEGES ON ALL FUNCTIONS IN SCHEMA public TO aero_user;

-- ============================================================================
-- Create views for common queries
-- ============================================================================

-- View for latest prices by symbol
CREATE OR REPLACE VIEW latest_prices AS
SELECT DISTINCT ON (symbol) 
    time, symbol, close, volume, created_at
FROM ohlcv_data
ORDER BY symbol, time DESC;

-- View for recent trades
CREATE OR REPLACE VIEW recent_trades AS
SELECT *
FROM trade_log
WHERE trade_status = 'OPEN'
ORDER BY trade_date DESC;

-- View for backtest statistics
CREATE OR REPLACE VIEW backtest_stats AS
SELECT 
    strategy_name,
    COUNT(*) as num_backtests,
    AVG(total_return) as avg_return,
    AVG(sharpe_ratio) as avg_sharpe,
    MAX(max_drawdown) as worst_drawdown,
    AVG(win_rate) as avg_win_rate
FROM backtest_results
GROUP BY strategy_name;
