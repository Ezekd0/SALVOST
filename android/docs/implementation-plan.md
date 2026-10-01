# AI-CTDRS Android Client — Implementation Plan

Build a native Android client (Kotlin + Jetpack Compose) that connects to the existing AI-CTDRS FastAPI backend, providing a mobile security-product experience for device protection monitoring and threat analysis.

## Scope & Constraints

- **Preserve** the existing backend, ML pipeline, SHAP explainer, React SOC dashboard, and database completely
- **No** kernel/eBPF monitoring, real malware, real firewall, auto app deletion, or federated learning
- **Reuse** all existing API contracts exactly as they are
- All test/synthetic events will be clearly labelled as `is_demo: true`

---

## Existing API Contracts (Confirmed from Source)

| Method | Endpoint | Auth | Request Body | Response |
|--------|----------|------|-------------|----------|
| POST | `/auth/register` | No | `{full_name, email, username, password, confirm_password, role?}` | User object |
| POST | `/auth/login` | No | `username` + `password` (form-encoded) | `{access_token, token_type}` |
| GET | `/auth/me` | Bearer | — | User object |
| POST | `/analysis/run` | Bearer | `{source_ip, destination_ip, destination_port, protocol, flow_duration, packet_rate, bytes_rate, is_demo?}` | SecurityEvent with nested Analysis |
| GET | `/dashboard/summary` | Bearer | — | `{eventsAnalyzed, threatsDetected, criticalIncidents, contained}` |
| GET | `/incidents/` | Bearer | — | List of Incident (with Analysis + ResponseActions) |
| GET | `/incidents/{id}` | Bearer | — | Single Incident |
| GET | `/incidents/responses/recent` | Bearer | — | List of ResponseAction |
| GET | `/events/?limit=N` | Bearer | — | List of SecurityEvent |
| GET | `/health` | No | — | `{status, service}` |

> [!IMPORTANT]
> Login uses **form-encoded** `application/x-www-form-urlencoded` (OAuth2PasswordRequestForm), not JSON.

---

## Proposed Changes

### Android Project Setup

#### [NEW] `/home/ezekdo/Downloads/Salvost/android/`

Create a new Android project using the `android` CLI:
```
android create empty-activity --name="AI-CTDRS" --output=./android
```

The project will use:
- **Kotlin** with Jetpack Compose
- **Material 3** for UI components
- **Retrofit + OkHttp** for REST API
- **Hilt** for dependency injection (or manual DI for simplicity given the deadline)
- **Kotlin Coroutines** for async operations
- **DataStore** for token persistence
- Target SDK 34, Min SDK 26

---

### Data Layer

#### [NEW] `app/src/main/java/.../data/api/CtdrsApi.kt`
Retrofit interface matching all existing backend endpoints:
- `login(username, password)` → form-encoded POST
- `register(body)` → JSON POST
- `getMe()` → Bearer GET
- `runAnalysis(body)` → Bearer JSON POST
- `getDashboardSummary()` → Bearer GET
- `getIncidents()` → Bearer GET
- `getIncident(id)` → Bearer GET
- `getRecentResponses()` → Bearer GET
- `getEvents(limit)` → Bearer GET
- `healthCheck()` → GET

#### [NEW] `app/src/main/java/.../data/model/*.kt`
Kotlin data classes mirroring the Pydantic schemas:
- `LoginRequest`, `LoginResponse` (Token)
- `RegisterRequest`, `UserResponse`
- `SecurityEventRequest`, `SecurityEventResponse`
- `AnalysisResponse`, `IncidentResponse`, `ResponseActionResponse`
- `DashboardSummary`

#### [NEW] `app/src/main/java/.../data/repository/AuthRepository.kt`
- Token storage (DataStore)
- Login/register/logout flows
- Auth state observable

#### [NEW] `app/src/main/java/.../data/repository/SecurityRepository.kt`
- Dashboard data
- Threat analysis submission
- Incidents and events

---

### Telemetry Layer

#### [NEW] `app/src/main/java/.../telemetry/DeviceTelemetry.kt`
Lightweight device security event generator that:
1. Reads real device metadata (model, OS version, battery, network type) for context
2. Generates **simulated** network telemetry events matching the `SecurityEventCreate` schema
3. Maps 5 scenario profiles (Benign, Port Scan, Brute Force, DDoS, Malware Traffic) to realistic parameters
4. All events are tagged `is_demo: true`
5. Submits to the existing `/analysis/run` endpoint — no backend changes needed

This means the existing Random Forest model processes the Android-submitted events identically to web-submitted events.

---

### UI Layer (Jetpack Compose)

#### Screen 1: Login / Register
- Professional dark security aesthetic
- Username + Password → Sign In
- Link to Register (full_name, email, username, password, confirm_password)
- JWT stored in encrypted DataStore

#### Screen 2: Protection Status (Home/Dashboard)
- Shield icon with protection status indicator
- Summary cards: Events Analyzed, Threats Detected, Critical Incidents, Contained
- System health indicators (Inference Engine, Response Engine)
- Quick-action button: "Run Security Scan"

#### Screen 3: Threat Analysis / Scan
- Scenario selector (5 presets: Benign, Port Scan, Brute Force, DDoS, Malware Traffic)
- "Run Analysis" button with loading animation
- Result display: Classification, Confidence %, Threat Score, Severity
- SHAP Feature Importance breakdown
- Automated Response status
- All results come from the **real Random Forest model** via the API

#### Screen 4: Incidents & Activity
- List of incidents with severity color coding
- Incident detail view with timeline, analysis, response actions
- Recent response actions list

#### Screen 5: Settings
- User profile info (from `/auth/me`)
- Backend server URL configuration
- Logout

#### Navigation
- Bottom navigation bar: Protection | Scan | Incidents | Settings

---

### Backend Changes

> [!NOTE]
> **Zero destructive changes** to the existing backend.

The only backend consideration is that CORS already allows `*` origins, so the Android client's HTTP requests will work without any changes.

---

## Verification Plan

### Build & Install
1. Build debug APK using `android run` or Gradle
2. Install on connected device or emulator

### E2E Flow
1. **Register** a new account from the Android app
2. **Login** with the new credentials
3. **Dashboard** loads with 0 events (new user isolation)
4. **Run Threat Analysis** — submit a Port Scan event
5. Verify the backend processes it through Random Forest + SHAP
6. Verify the Android app displays classification, confidence, threat score, feature importance
7. **Check Incidents** — confirm the incident was created
8. **Verify Web SOC** — confirm the same event and incident appear on the React dashboard at `localhost:4173`
9. **Run all 5 scenarios** (Benign, Port Scan, Brute Force, DDoS, Malware Traffic)
10. **Logout** and verify token is cleared

### Cross-platform verification
- Submit an event from Android → see it in the Web SOC dashboard
- Confirm user data isolation between Android and web users
