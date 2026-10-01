# Android redesign verification

## Architecture

`MainActivity` launches `client/ClientScreen`. This redesign retains that active
client, `ClientViewModel`, Retrofit `CtdrsApi`, and encrypted `SessionStore`.
The older `ui/`, `data/`, and Navigation3 sample paths remain untouched and are
not the app entry point. No backend source or endpoint was changed.

## Screens

Splash; three onboarding pages; login; registration; account role selection;
dashboard; controlled scan selector; estimated scan progress with cancellation;
real scan result; threat details with signed SHAP contributions; filtered
incidents; incident detail and automated responses; reports with shareable
current totals; profile; settings and capability explanations; activity;
threat intelligence; server connection; bottom navigation and drawer/logout.

## Data contracts and limits

- Username/password authentication uses existing login/register/me APIs.
- Role is the existing registration/profile field, not an authorization grant.
- All five original scenario telemetry values and `is_demo=true` are retained.
- Analysis classifications, confidence, scores, category, timestamps, network
  fields and SHAP contributions are taken from returned data.
- Severity comes from the matching incident when available.
- Dashboard counts are fetched from the summary/incidents APIs. Backend
  availability is shown separately from device health; no health percentage or
  active monitor count is fabricated.
- Reports share the current account totals; no invented charts or trends.
- Manual resolution, profile editing, push notifications, password changes,
  2FA, model tuning and data deletion are not exposed by the existing service.
  The UI explains these limits rather than presenting working-looking controls.
- Stopping a scan cancels the local request, but cannot undo server processing.
- Backend test verification created a dedicated account and five explicitly
  controlled test events. Results are in `backend-check.json`.

## Main changed source files

- `MainActivity.kt`: dark edge-to-edge system bars.
- `client/ClientScreen.kt`: app shell, navigation and capability pages.
- `client/DesignSystem.kt`: cards, fields, badges, buttons, icons, scalable art.
- `client/EntryScreens.kt`: onboarding, login, registration, roles, server setup.
- `client/OverviewScreens.kt`: dashboard, incidents, reports, profile, settings.
- `client/ScanScreens.kt`: scan flow and real SHAP presentation.
- `client/Api.kt`: additional fields already present in backend responses.
- `client/ClientViewModel.kt`: cancellable scan state and incident loading.
- `theme/Theme.kt`, `theme/Type.kt`: fixed dark palette and typography.
- `res/values/themes.xml`, `res/drawable/ic_launcher_*.xml`: dark launch branding.
- `androidTest/.../client/RedesignTest.kt`: scenario, SHAP and onboarding checks.

## Build

```sh
./gradlew -Dorg.gradle.java.home=/home/ezekdo/Downloads/android-studio/jbr assembleDebug
```

APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Recorded results

- `assembleDebug`: completed and generated the debug APK; the first standalone
  build reported `BUILD SUCCESSFUL`. The final combined build also completed
  `:app:assembleDebug` before the emulator test run was interrupted.
- `testDebugUnitTest`: 2 tests, 0 failures, 0 errors.
- Live FastAPI verification: registration, login, `/auth/me`, five classifications,
  four SHAP features for each analysis, four incident details with responses,
  and authenticated summary retrieval all passed (see `backend-check.json`).
- Emulator instrumentation: onboarding test passed; the remaining run was
  interrupted before a complete result. Do not interpret the partial run as
  a passing suite. The headless emulator retry exited with code 139 during boot.
- Build environment warnings: JDK native-access warning and Android SDK XML
  tooling version mismatch. Neither prevented APK generation.

Full emulator walkthrough and visual screenshot review remain unverified until
an emulator can complete startup and the instrumentation suite can finish.
