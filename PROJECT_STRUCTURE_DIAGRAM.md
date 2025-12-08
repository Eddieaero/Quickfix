# Aero QuickFIX Trading Dashboard - Project Structure Diagram

## Overall Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         AERO QUICKFIX TRADING SYSTEM                        │
└─────────────────────────────────────────────────────────────────────────────┘

                              User Browser
                                  │
                    ┌─────────────┼─────────────┐
                    │             │             │
                    ▼             ▼             ▼
            ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
            │   Login UI   │ │  Dashboard   │ │  WebSocket   │
            │  (Login.tsx) │ │  (page.tsx)  │ │ Connection   │
            └──────┬───────┘ └──────┬───────┘ └──────┬───────┘
                   │                │                 │
                   └────────────────┼─────────────────┘
                                    │
                    ┌───────────────▼────────────────┐
                    │  Next.js Frontend (Port 3000)  │
                    │  ├─ React Components           │
                    │  ├─ TypeScript                 │
                    │  ├─ Tailwind CSS (Dark Mode)   │
                    │  └─ shadcn/ui Components       │
                    └───────────────┬────────────────┘
                                    │
                    HTTP/REST & WebSocket
                                    │
        ┌───────────────────────────▼───────────────────────────┐
        │   Spring Boot Backend (Port 8080)                      │
        │   ├─ REST API Endpoints                               │
        │   ├─ WebSocket Endpoint (/ws/trades)                 │
        │   ├─ Authentication & Security                        │
        │   └─ FIX Protocol Processing                          │
        └───────────┬─────────────────────┬──────────────────────┘
                    │                     │
        ┌───────────▼──────────┐  ┌──────▼────────────────┐
        │  FIX Protocol        │  │  External Services   │
        │  Connection          │  │  ├─ JWT Generation   │
        │  (Port 9878)         │  │  ├─ Password Hash    │
        │  ├─ SocketInitiator  │  │  └─ Security        │
        │  ├─ Message Parser   │  │                      │
        │  └─ Trade Capture    │  └──────────────────────┘
        └───────────┬──────────┘
                    │
        ┌───────────▼──────────────────┐
        │  FIX Test Server (Port 9878) │
        │  ├─ Logon Messages (35=A)    │
        │  ├─ Heartbeats (35=0)        │
        │  ├─ New Orders (35=D)        │
        │  └─ Execution Reports (35=8) │
        └──────────────────────────────┘
```

---

## Backend Architecture (Spring Boot)

```
┌──────────────────────────────────────────────────────────────────┐
│                    SPRING BOOT APPLICATION                       │
│                   (aero-quickfix-1.0.0.jar)                      │
└──────────────────────────────────────────────────────────────────┘

┌─ CONTROLLERS ──────────────────────────────────────────────────┐
│                                                                 │
│  ┌─ AuthController ──────────────────┐                        │
│  │ ├─ POST /api/auth/login           │                        │
│  │ ├─ POST /api/auth/register        │                        │
│  │ ├─ POST /api/auth/verify          │                        │
│  │ └─ GET /api/auth/health           │                        │
│  └───────────────────────────────────┘                        │
│                                                                 │
│  ┌─ QuickFixController ──────────────┐                        │
│  │ ├─ POST /api/quickfix/start       │                        │
│  │ ├─ POST /api/quickfix/stop        │                        │
│  │ ├─ GET /api/quickfix/status       │                        │
│  │ ├─ GET /api/quickfix/trades       │                        │
│  │ ├─ GET /api/quickfix/trades/all   │                        │
│  │ └─ GET /api/quickfix/health       │                        │
│  └───────────────────────────────────┘                        │
└─────────────────────────────────────────────────────────────────┘
         │                                          │
         └──────────────────────┬──────────────────┘
                                │

┌─ SERVICES ────────────────────────────────────────────────────┐
│                                                                 │
│  ┌─ AuthService ────────────┐                                 │
│  │ ├─ login()               │                                 │
│  │ ├─ register()            │                                 │
│  │ └─ validateAndGetUsername()                                │
│  └──────────────────────────┘                                 │
│                                                                 │
│  ┌─ QuickFixService ────────┐                                 │
│  │ ├─ startConnection()     │                                 │
│  │ ├─ stopConnection()      │                                 │
│  │ └─ isConnected()         │                                 │
│  └──────────────────────────┘                                 │
│                                                                 │
│  ┌─ MockTradeDataService ───┐                                 │
│  │ └─ generateMockTrades()  │                                 │
│  └──────────────────────────┘                                 │
└─────────────────────────────────────────────────────────────────┘
         │                         │
         └────────────┬────────────┘
                      │

┌─ REPOSITORIES ────────────────────────────────────────────────┐
│                                                                 │
│  ┌─ UserRepository ──────────────────────┐                    │
│  │ ├─ findByUsername(username)           │                    │
│  │ ├─ findByUserId(userId)               │                    │
│  │ ├─ findByEmail(email)                 │                    │
│  │ ├─ existsByUsername(username)         │                    │
│  │ └─ save(user)                         │                    │
│  └───────────────────────────────────────┘                    │
│                                                                 │
│  ┌─ TradeDataRepository ─────────────────┐                    │
│  │ ├─ save(trade)                        │                    │
│  │ ├─ findById(id)                       │                    │
│  │ ├─ findAll()                          │                    │
│  │ ├─ getRecentTrades(limit)             │                    │
│  │ ├─ getTotalTradeCount()               │                    │
│  │ ├─ getTotalVolume()                   │                    │
│  │ ├─ getAveragePrice()                  │                    │
│  │ └─ getHistory()                       │                    │
│  └───────────────────────────────────────┘                    │
└─────────────────────────────────────────────────────────────────┘
         │                         │
         └────────────┬────────────┘
                      │

┌─ MODELS / DTOs ────────────────────────────────────────────────┐
│                                                                  │
│  ┌─ User ─────────────────────────┐                            │
│  │ ├─ userId                      │                            │
│  │ ├─ username                    │                            │
│  │ ├─ email                       │                            │
│  │ ├─ password                    │                            │
│  │ ├─ enabled                     │                            │
│  │ └─ createdAt                   │                            │
│  └────────────────────────────────┘                            │
│                                                                  │
│  ┌─ TradeData ────────────────────┐                            │
│  │ ├─ orderId                     │                            │
│  │ ├─ symbol                      │                            │
│  │ ├─ side (BUY/SELL)             │                            │
│  │ ├─ quantity                    │                            │
│  │ ├─ price                       │                            │
│  │ ├─ status                      │                            │
│  │ ├─ executedQty                 │                            │
│  │ ├─ executedPrice               │                            │
│  │ ├─ timestamp                   │                            │
│  │ └─ messageType                 │                            │
│  └────────────────────────────────┘                            │
│                                                                  │
│  ┌─ DTOs ─────────────────────────┐                            │
│  │ ├─ LoginRequest                │                            │
│  │ ├─ AuthResponse                │                            │
│  │ └─ TradeStatsDto               │                            │
│  └────────────────────────────────┘                            │
└─────────────────────────────────────────────────────────────────┘

┌─ CONFIGURATION & SECURITY ────────────────────────────────────┐
│                                                                  │
│  ┌─ QuickFixConfig ──────────────────────┐                    │
│  │ └─ socketInitiator()                  │                    │
│  └───────────────────────────────────────┘                    │
│                                                                  │
│  ┌─ WebSocketConfig ─────────────────────┐                    │
│  │ └─ registerWebSocketHandlers()        │                    │
│  │    (endpoint: /ws/trades)             │                    │
│  └───────────────────────────────────────┘                    │
│                                                                  │
│  ┌─ SecurityConfig ──────────────────────┐                    │
│  │ ├─ securityFilterChain()              │                    │
│  │ ├─ corsConfigurationSource()          │                    │
│  │ └─ passwordEncoder()                  │                    │
│  └───────────────────────────────────────┘                    │
│                                                                  │
│  ┌─ QuickFixApplicationAdapter ──────────┐                    │
│  │ ├─ onCreate()                         │                    │
│  │ ├─ onLogon()                          │                    │
│  │ ├─ fromApp() [main handler]           │                    │
│  │ ├─ extractNewOrder()                  │                    │
│  │ ├─ extractExecutionReport()           │                    │
│  │ └─ mapOrderStatus()                   │                    │
│  └───────────────────────────────────────┘                    │
│                                                                  │
│  ┌─ JwtUtil ────────────────────────────┐                    │
│  │ ├─ generateToken()                    │                    │
│  │ ├─ extractUsername()                  │                    │
│  │ ├─ validateToken()                    │                    │
│  │ └─ isTokenExpired()                   │                    │
│  └───────────────────────────────────────┘                    │
│                                                                  │
│  ┌─ TradeWebSocketHandler ───────────────┐                    │
│  │ ├─ afterConnectionEstablished()       │                    │
│  │ ├─ afterConnectionClosed()            │                    │
│  │ ├─ handleTextMessage()                │                    │
│  │ └─ broadcastTradeUpdate()             │                    │
│  └───────────────────────────────────────┘                    │
└─────────────────────────────────────────────────────────────────┘
```

---

## Frontend Architecture (Next.js + React)

```
┌──────────────────────────────────────────────────────────────┐
│              NEXT.JS FRONTEND (TypeScript)                    │
│                   quickfix-dashboard                          │
└──────────────────────────────────────────────────────────────┘

┌─ PAGES ────────────────────────────────────────────────────┐
│                                                              │
│  ┌─ app/page.tsx ──────────────────────┐                  │
│  │ ├─ Dashboard Component (main view)  │                  │
│  │ ├─ Real-time trade updates          │                  │
│  │ ├─ Connection status display        │                  │
│  │ ├─ Trade statistics (4-column grid) │                  │
│  │ ├─ Trade table (20 columns)         │                  │
│  │ ├─ WebSocket connection logic       │                  │
│  │ ├─ HTTP fallback polling            │                  │
│  │ ├─ Connect/Disconnect handlers      │                  │
│  │ └─ Logout handler                   │                  │
│  └─────────────────────────────────────┘                  │
│                                                              │
│  ┌─ app/layout.tsx ────────────────────┐                  │
│  │ ├─ Root layout wrapper              │                  │
│  │ ├─ Dark mode theme configuration    │                  │
│  │ ├─ Global CSS variables (HSL)       │                  │
│  │ └─ Metadata setup                   │                  │
│  └─────────────────────────────────────┘                  │
└────────────────────────────────────────────────────────────┘

┌─ COMPONENTS ───────────────────────────────────────────────┐
│                                                              │
│  ┌─ UI Library (shadcn) ────────────────────────────────┐  │
│  │ ├─ components/ui/card.tsx                            │  │
│  │ │  ├─ Card                                            │  │
│  │ │  ├─ CardHeader                                      │  │
│  │ │  ├─ CardTitle                                       │  │
│  │ │  ├─ CardDescription                                 │  │
│  │ │  ├─ CardContent                                     │  │
│  │ │  └─ CardFooter                                      │  │
│  │ └────────────────────────────────────────────────────┘  │
│  │                                                          │
│  │ ├─ components/ui/button.tsx                            │  │
│  │ │  ├─ Variants: default, destructive, outline, etc.   │  │
│  │ │  ├─ Sizes: sm, default, lg, icon                    │  │
│  │ │  └─ States: disabled, loading, etc.                 │  │
│  │ └────────────────────────────────────────────────────┘  │
│  │                                                          │
│  │ ├─ components/ui/status-badge.tsx                      │  │
│  │ │  ├─ Status: connected/disconnected/connecting       │  │
│  │ │  ├─ Visual indicator (dot)                           │  │
│  │ │  └─ Color coded feedback                             │  │
│  │ └────────────────────────────────────────────────────┘  │
│  └──────────────────────────────────────────────────────────┘
│                                                              │
│  ┌─ Business Logic ──────────────────────────────────────┐  │
│  │ ├─ Login Component (new)                              │  │
│  │ │  ├─ Username/password input                         │  │
│  │ │  ├─ Login/Register toggle                           │  │
│  │ │  ├─ Form validation                                 │  │
│  │ │  └─ Auth API calls                                  │  │
│  │ └─────────────────────────────────────────────────────┘  │
│  │                                                          │
│  │ ├─ Dashboard Component (existing)                      │  │
│  │ │  ├─ WebSocket connection management                 │  │
│  │ │  ├─ FIX connection control                          │  │
│  │ │  ├─ Real-time trade display                         │  │
│  │ │  ├─ Statistics calculation                          │  │
│  │ │  └─ Logout functionality                            │  │
│  │ └─────────────────────────────────────────────────────┘  │
│  └──────────────────────────────────────────────────────────┘
└────────────────────────────────────────────────────────────┘

┌─ STYLING & THEME ──────────────────────────────────────────┐
│                                                              │
│  ├─ Dark Mode (default)                                    │
│  │  ├─ Background: 0 0% 3.6% (near black)                │
│  │  ├─ Foreground: 0 0% 98.2% (near white)               │
│  │  ├─ Card: 0 0% 8.5% (dark gray)                       │
│  │  ├─ Border: 0 0% 14.9%                                │
│  │  └─ Accent colors for status/trades                   │
│  │                                                         │
│  ├─ Tailwind CSS                                          │
│  │  ├─ Responsive grid layouts                           │
│  │  ├─ Dark theme utilities                              │
│  │  ├─ Color scales for UI consistency                   │
│  │  └─ Animation/transition effects                      │
│  │                                                         │
│  └─ CSS Variables (app/layout.tsx)                        │
│     ├─ --background                                       │
│     ├─ --foreground                                       │
│     ├─ --card / --card-foreground                         │
│     ├─ --border / --input                                 │
│     ├─ --primary / --primary-foreground                   │
│     ├─ --destructive                                      │
│     └─ --radius (0.5rem)                                  │
└────────────────────────────────────────────────────────────┘

┌─ DATA FLOW ────────────────────────────────────────────────┐
│                                                              │
│  1. User Login                                             │
│     ├─ Enter credentials                                  │
│     ├─ POST /api/auth/login                               │
│     ├─ Receive JWT token                                  │
│     └─ Store & navigate to dashboard                      │
│                                                              │
│  2. WebSocket Connection (on dashboard mount)             │
│     ├─ Connect to ws://localhost:8080/ws/trades           │
│     ├─ Receive TradeStatsDto updates                      │
│     ├─ Auto-reconnect on failure (3s retry)              │
│     └─ Update component state                             │
│                                                              │
│  3. HTTP Fallback (when WS disconnected)                   │
│     ├─ Poll GET /api/quickfix/status (every 5s)          │
│     ├─ Poll GET /api/quickfix/trades on FIX connected    │
│     └─ Update UI with latest data                         │
│                                                              │
│  4. Trade Display                                          │
│     ├─ Render 4-stat cards (trades/volume/price/status)  │
│     ├─ Display 20-column trade table                      │
│     ├─ Format prices, timestamps, status badges          │
│     └─ Color-code BUY/SELL and statuses                   │
│                                                              │
│  5. User Logout                                            │
│     ├─ Clear JWT token                                    │
│     ├─ Close WebSocket connection                         │
│     └─ Redirect to login page                             │
└────────────────────────────────────────────────────────────┘
```

---

## FIX Protocol Message Flow

```
┌──────────────────────────────────────────────────────────────┐
│           FIX PROTOCOL (QuickFIX/J) MESSAGE FLOW             │
└──────────────────────────────────────────────────────────────┘

FIX Test Server ─────────────┐
(Port 9878)                  │
                             ▼
     ┌─ Logon (35=A) ────────────────────┐
     │ BeginString=FIX.4.2                │
     │ SenderCompID=TARGET                │
     │ TargetCompID=SENDER                │
     └────────────────────────────────────┘
                    │
     ┌─ Heartbeat (35=0) ────────────────┐
     │ Every 30 seconds                   │
     └────────────────────────────────────┘
                    │
     ┌─ New Order (35=D) ────────────────┐
     │ ClOrdID (order ID)                 │
     │ Symbol (stock ticker)              │
     │ Side (1=BUY, 2=SELL)               │
     │ OrderQty                           │
     │ Price                              │
     └────────────────────────────────────┘
                    │
     ┌─ Execution Report (35=8) ────────┐
     │ OrderID                            │
     │ Symbol                             │
     │ Side                               │
     │ OrderQty                           │
     │ Price                              │
     │ OrdStatus (order status)           │
     │ LastQty (executed qty)             │
     │ LastPx (executed price)            │
     └────────────────────────────────────┘
                    │
                    ▼
        ┌─ Spring Boot Backend ──────┐
        │ QuickFixApplicationAdapter │
        │  ├─ onCreate()             │
        │  ├─ onLogon()              │
        │  ├─ fromApp()              │
        │  ├─ extractNewOrder()      │
        │  └─ extractExecutionReport()
        └────────┬────────────────────┘
                 │
        ┌────────▼────────────────────┐
        │ TradeData extraction        │
        │ ├─ Parse FIX fields        │
        │ ├─ Map statuses            │
        │ └─ Create TradeData object  │
        └────────┬────────────────────┘
                 │
        ┌────────▼────────────────────┐
        │ TradeDataRepository         │
        │ ├─ Save trade              │
        │ ├─ Maintain history        │
        │ └─ Calculate statistics    │
        └────────┬────────────────────┘
                 │
        ┌────────▼────────────────────┐
        │ TradeWebSocketHandler       │
        │ ├─ Get stats from repo     │
        │ ├─ Serialize to JSON       │
        │ └─ Broadcast to all clients │
        └────────┬────────────────────┘
                 │
        ┌────────▼────────────────────┐
        │ WebSocket Message           │
        │ TradeStatsDto {             │
        │   totalTrades: 15,          │
        │   totalVolume: 2296,        │
        │   averagePrice: 158.67,     │
        │   recentTrades: [...]       │
        │ }                           │
        └────────┬────────────────────┘
                 │
                 ▼
        ┌─────────────────────────────┐
        │ Next.js Frontend WebSocket  │
        │ ├─ Receive message         │
        │ ├─ Parse JSON              │
        │ ├─ Update React state       │
        │ └─ Re-render UI components │
        └─────────────────────────────┘
```

---

## Authentication Flow

```
┌────────────────────────────────────────────────────────────┐
│              AUTHENTICATION FLOW (JWT)                      │
└────────────────────────────────────────────────────────────┘

Browser Login Page
        │
        ├─ User enters credentials (username, password)
        │
        ▼
POST /api/auth/login
        │
        ├─ AuthController receives LoginRequest
        │
        ▼
AuthService.login()
        │
        ├─ Find user by username in UserRepository
        │
        ├─ Validate password (plain text comparison for demo)
        │
        ├─ Check if user enabled
        │
        ├─ If valid:
        │  └─ Generate JWT token via JwtUtil
        │
        ▼
Return AuthResponse {
    success: true,
    message: "Login successful",
    token: "eyJhbGc...",
    username: "admin"
}
        │
        ├─ Store token in localStorage/sessionStorage
        │
        ├─ Add to Authorization header: "Bearer <token>"
        │
        ▼
Access Protected Resources
        │
        ├─ Include token in requests
        │
        ├─ POST /api/quickfix/start (with Bearer token)
        │
        ├─ GET /api/quickfix/trades (with Bearer token)
        │
        ▼
SecurityConfig validates token
        │
        ├─ Extract token from Authorization header
        │
        ├─ JwtUtil.validateToken()
        │
        ├─ If valid: Allow request
        │
        └─ If invalid: Return 401 Unauthorized

User Logout
        │
        ├─ Clear localStorage token
        │
        ├─ Close WebSocket connection
        │
        └─ Redirect to login page
```

---

## File Structure

```
project19-Aero/
├── aero/                          # Spring Boot Backend
│   ├── pom.xml                    # Maven dependencies (Spring, JWT, Security, WebSocket)
│   ├── src/main/java/com/aero/quickfix/
│   │   ├── AeroApplication.java   # Main entry point
│   │   ├── controller/
│   │   │   ├── AuthController.java        # /api/auth/* endpoints
│   │   │   └── QuickFixController.java    # /api/quickfix/* endpoints
│   │   ├── service/
│   │   │   ├── AuthService.java          # Login/Register logic
│   │   │   ├── QuickFixService.java      # FIX connection management
│   │   │   └── MockTradeDataService.java # Mock data generation
│   │   ├── repository/
│   │   │   ├── UserRepository.java       # User in-memory storage
│   │   │   └── TradeDataRepository.java  # Trade in-memory storage
│   │   ├── model/
│   │   │   ├── User.java                 # User entity
│   │   │   └── TradeData.java            # Trade data entity
│   │   ├── dto/
│   │   │   ├── LoginRequest.java         # Login payload
│   │   │   ├── AuthResponse.java         # Auth response
│   │   │   └── TradeStatsDto.java        # Trade statistics
│   │   ├── config/
│   │   │   ├── QuickFixConfig.java       # FIX protocol setup
│   │   │   ├── SecurityConfig.java       # Spring Security & CORS
│   │   │   └── QuickFixApplicationAdapter.java  # FIX message handler
│   │   ├── websocket/
│   │   │   ├── TradeWebSocketHandler.java      # WebSocket handler
│   │   │   └── WebSocketConfig.java           # WebSocket endpoint config
│   │   └── security/
│   │       └── JwtUtil.java               # JWT token operations
│   ├── src/main/resources/
│   │   ├── application.yml          # Spring Boot configuration
│   │   └── quickfix-client.cfg      # FIX protocol settings
│   └── target/
│       └── aero-quickfix-1.0.0.jar  # Compiled application (26MB)
│
├── quickfix-dashboard/            # Next.js Frontend
│   ├── package.json               # npm dependencies
│   ├── tsconfig.json              # TypeScript config
│   ├── next.config.js             # Next.js config (API proxy)
│   ├── tailwind.config.js         # Tailwind CSS theme
│   ├── app/
│   │   ├── layout.tsx             # Root layout (dark mode setup)
│   │   ├── page.tsx               # Dashboard page (main component)
│   │   └── login.tsx              # Login page (new)
│   ├── components/
│   │   └── ui/
│   │       ├── card.tsx           # Card component library
│   │       ├── button.tsx         # Button component variants
│   │       └── status-badge.tsx   # Status indicator component
│   ├── styles/
│   │   └── globals.css            # Global styles
│   └── node_modules/              # Dependencies
│
├── quickfix-server/               # Python FIX Test Server
│   └── simple_server.py           # TCP server sending FIX messages
│
├── README.md                      # Project documentation
├── WEBSOCKET_UPGRADE.md           # WebSocket implementation guide
└── IMPLEMENTATION_SUMMARY.md      # Feature summary
```

---

## Technology Stack

### Backend
- **Framework:** Spring Boot 3.2.0
- **Language:** Java 17
- **Security:** Spring Security, JWT (jjwt 0.12.3)
- **Real-time:** WebSocket (Spring WebSocket)
- **FIX Protocol:** QuickFIX/J 2.3.1
- **Build:** Maven 3.9.11
- **Server:** Embedded Tomcat (Port 8080)

### Frontend
- **Framework:** Next.js 13.5.11
- **Language:** TypeScript
- **UI Framework:** React 18
- **Styling:** Tailwind CSS
- **Component Library:** shadcn/ui
- **HTTP Client:** Axios
- **Icons:** lucide-react
- **Server:** Next.js Dev Server (Port 3000/3001)

### Test Infrastructure
- **Language:** Python 3.11
- **Protocol:** Raw TCP/FIX
- **Port:** 9878
- **Simulation:** NewOrder (35=D), ExecutionReport (35=8)

---

## Key Features by Component

### Authentication
- ✅ User registration (POST /api/auth/register)
- ✅ User login (POST /api/auth/login)
- ✅ JWT token generation
- ✅ Token validation (POST /api/auth/verify)
- ✅ CORS support for cross-origin requests

### FIX Protocol
- ✅ SocketInitiator connection to test server
- ✅ Logon/Logout message handling
- ✅ NewOrder (35=D) message parsing
- ✅ ExecutionReport (35=8) message parsing
- ✅ Trade data extraction from FIX fields
- ✅ Order status mapping

### Real-Time Updates
- ✅ WebSocket endpoint (/ws/trades)
- ✅ Bi-directional message streaming
- ✅ Automatic broadcast on trade capture
- ✅ Fallback HTTP polling (5-second interval)
- ✅ Auto-reconnect with 3-second retry

### Dashboard UI
- ✅ Dark mode (shadcn default)
- ✅ Real-time trade table (20 columns)
- ✅ Statistics cards (trades, volume, avg price)
- ✅ Connection status indicators
- ✅ WebSocket connection status
- ✅ Responsive design (mobile to desktop)
- ✅ Color-coded trade sides and statuses

### Trade Data Management
- ✅ In-memory repository (1000-record limit)
- ✅ Trade history with FIFO rotation
- ✅ Statistics calculation (total, volume, average)
- ✅ Recent trades filtering
- ✅ Thread-safe collections (ConcurrentHashMap)

---

## Data Models

### User
```
{
  userId: "USER_1765189116597",
  username: "admin",
  email: "admin@aero.com",
  password: "admin123",
  enabled: true,
  createdAt: 1765189116597
}
```

### TradeData
```
{
  orderId: "ORD1000",
  symbol: "AAPL",
  side: "BUY",
  quantity: 100,
  price: 150.25,
  status: "FILLED",
  executedQty: 50,
  executedPrice: 150.30,
  timestamp: 1765189116597,
  messageType: "ExecutionReport"
}
```

### TradeStatsDto
```
{
  totalTrades: 15,
  totalVolume: 2296,
  averagePrice: 158.67,
  recentTrades: [TradeData, ...]
}
```

---

## Deployment Architecture

```
Production Deployment
├── Containerization
│   ├─ Docker image (Spring Boot + Java 17)
│   └─ Docker image (Next.js)
├── Orchestration
│   ├─ Kubernetes pods/services
│   └─ Load balancer
├── Databases
│   ├─ PostgreSQL (User management)
│   └─ MongoDB (Trade history)
├── Authentication
│   ├─ OAuth 2.0 / OpenID Connect
│   └─ Keycloak integration
├── Monitoring
│   ├─ Prometheus metrics
│   ├─ Grafana dashboards
│   └─ ELK stack (logs)
└── Security
    ├─ TLS/HTTPS enforcement
    ├─ API rate limiting
    ├─ WAF (Web Application Firewall)
    └─ DDoS protection
```

---

This diagram provides a complete overview of the Aero QuickFIX Trading Dashboard project architecture, showing how all components interact in a real-time trading system.
