# Public landing page

Run `npm run dev` from `frontend` and open the printed localhost URL. Run `npm run build` for TypeScript validation and a production build. No deployment is performed by these commands.

## Routes

- `/`: public product landing page, also accessible when signed in.
- `/dashboard`: existing authenticated SOC overview.
- `/login` and `/register`: existing authentication flows; successful authentication now opens `/dashboard`.
- `/analysis`, `/events`, `/incidents`, `/incidents/:id`, `/response`, `/analytics`, `/reports`, `/status`, `/settings`: existing protected routes, unchanged.

The SOC sidebar overview link now points to `/dashboard`. API configuration, authentication requests, and dashboard data components are preserved. SOC modules are loaded on demand to keep the public entry point lightweight. A future production host must serve `index.html` for client-side routes.

## Android download

Set `VITE_ANDROID_APK_URL` in your local or deployment environment to an actual HTTPS release asset URL or a same-origin absolute path. Restart Vite after changing environment variables; production builds capture the value at build time. See `.env.example`. No final release URL is assumed, and no APK is copied into this frontend.

When the value is absent or unsupported, download CTAs navigate to the Android section, which explains that the download is not available yet. Once configured, all download CTAs use the same value. Cross-origin hosting controls download headers; the frontend does not force an attachment download.

## Visual assets and content

The landing page uses scoped styles, existing Lucide icons, CSS, and inline SVG. No new runtime dependencies or remote fonts are required. Reduced motion, keyboard focus, skip navigation, and a responsive menu are supported.

The supplied request contained no Android screenshots. `PhonePreview` is explicitly labeled as an illustration, not an app screenshot. Replace it with approved Android screenshots when supplied. Colors follow the request's dark navy, violet, and blue direction; exact reference matching remains pending.

Descriptions are grounded in the current Random Forest pipeline, four input features, SHAP contributions, five controlled scenarios, five-second event-feed polling, and recorded response actions. No performance benchmarks or production protection claims are added. Existing dashboard demonstration metrics remain outside this landing-page change.

## Local verification

Chrome checks cover 1440, 1024, 768, 390, and 320 pixel viewports, horizontal overflow, mobile menu and Escape dismissal, protected routes, download availability, and reduced motion. Real authentication, analysis/SHAP, event feed, incidents, and responses are checked against the unchanged backend using a temporary database copy, preserving project data. The incident registry has a pre-existing render error: it expects `analysis.event.source_ip`, but the API analysis schema does not include `event`. That unrelated UI/API mismatch remains unchanged.
