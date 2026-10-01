import {
  Activity,
  ArrowRight,
  BarChart3,
  BrainCircuit,
  Check,
  Database,
  Fingerprint,
  GitBranch,
  Layers,
  LayoutDashboard,
  LockKeyhole,
  Network,
  Radar,
  Shield,
  ShieldCheck,
  Smartphone,
  Workflow,
  Zap,
} from "lucide-react";
import { Link } from "react-router-dom";
import {
  Brand,
  DashboardButton,
  DownloadButton,
  LandingNav,
  PhonePreview,
  SectionHeading,
  SecurityVisual,
  androidApkUrl,
} from "../components/landing/LandingComponents";
import "./Landing.css";

const capabilities = [
  {
    icon: BrainCircuit,
    title: "AI Threat Detection",
    text: "Classify submitted network events with the existing Random Forest detection pipeline.",
  },
  {
    icon: Fingerprint,
    title: "Explainable AI",
    text: "Explore the feature contributions behind a prediction with SHAP explanations.",
  },
  {
    icon: Activity,
    title: "Real-Time Monitoring",
    text: "Review submitted events in the web event feed, refreshed every five seconds.",
  },
  {
    icon: ShieldCheck,
    title: "Incident Response",
    text: "Review severity-based incidents and recorded response actions for controlled events.",
  },
  {
    icon: BarChart3,
    title: "Security Analytics",
    text: "See account-level event, threat, incident, and containment summaries.",
  },
];
const workflow = [
  { icon: Network, title: "Telemetry", detail: "Security events" },
  { icon: Database, title: "Process", detail: "Prepare features" },
  { icon: BrainCircuit, title: "Detect", detail: "Random Forest" },
  { icon: Layers, title: "Classify", detail: "Threat category" },
  { icon: Fingerprint, title: "Explain", detail: "SHAP contributions" },
  { icon: ShieldCheck, title: "Respond", detail: "Incident / response" },
  { icon: LayoutDashboard, title: "Review", detail: "SOC dashboard" },
];
const scenarios = [
  {
    icon: Activity,
    name: "Normal Traffic / Benign",
    text: "A baseline for ordinary network behavior.",
    tag: "BASELINE",
  },
  {
    icon: Radar,
    name: "Port Scan",
    text: "Explore reconnaissance-style traffic patterns.",
    tag: "RECONNAISSANCE",
  },
  {
    icon: LockKeyhole,
    name: "Brute Force",
    text: "Evaluate repeated-access traffic patterns.",
    tag: "ACCESS ATTEMPTS",
  },
  {
    icon: Zap,
    name: "DDoS",
    text: "Examine high-volume traffic characteristics.",
    tag: "TRAFFIC VOLUME",
  },
  {
    icon: Shield,
    name: "Malware Traffic",
    text: "Analyze the supported malware traffic scenario.",
    tag: "MALICIOUS PATTERNS",
  },
];

export default function Landing() {
  return (
    <div className="landing">
      <a className="lp-skip" href="#main-content">
        Skip to content
      </a>
      <LandingNav />
      <main id="main-content">
        <section id="home" className="lp-hero lp-container">
          <div className="lp-hero-copy">
            <span className="lp-pill">
              <span /> INTELLIGENCE BEHIND EVERY SIGNAL
            </span>
            <h1>
              AI-Powered Cyber
              <br />
              <span>Threat Detection</span>
              <br />
              &amp; Response
            </h1>
            <p>
              Turn security events into actionable understanding. AI-CTDRS
              brings AI detection, explainable insights, and incident response
              into one connected security workspace.
            </p>
            <div className="lp-actions">
              <DownloadButton />
              <DashboardButton />
            </div>
            <div className="lp-hero-notes">
              <span>
                <Smartphone size={15} /> Android + Web
              </span>
              <span>
                <Fingerprint size={15} /> Explainable by design
              </span>
            </div>
          </div>
          <SecurityVisual />
        </section>
        <div className="lp-capability-strip">
          <div className="lp-container">
            {capabilities.map(({ icon: Icon, title }) => (
              <span key={title}>
                <Icon size={18} />
                {title}
              </span>
            ))}
          </div>
        </div>

        <section id="features" className="lp-section lp-container">
          <SectionHeading
            eyebrow="CONNECTED SECURITY INTELLIGENCE"
            title={
              <>
                More context.
                <br />
                <span>Better-informed decisions.</span>
              </>
            }
          >
            Follow a security event from detection to explanation and response,
            with the context you need at each step.
          </SectionHeading>
          <div className="lp-features">
            {capabilities.map(({ icon: Icon, title, text }, i) => (
              <article className="lp-card" key={title}>
                <div className="lp-card-top">
                  <span className="lp-icon">
                    <Icon />
                  </span>
                  <span className="lp-index">0{i + 1}</span>
                </div>
                <h3>{title}</h3>
                <p>{text}</p>
              </article>
            ))}
          </div>
        </section>

        <section id="how-it-works" className="lp-section lp-workflow-section">
          <div className="lp-container">
            <SectionHeading
              eyebrow="FROM SIGNAL TO UNDERSTANDING"
              title={
                <>
                  Every event has a story.
                  <br />
                  <span>See the whole picture.</span>
                </>
              }
            >
              One connected workflow, from submitted telemetry to your security
              operations dashboard.
            </SectionHeading>
            <ol className="lp-workflow">
              {workflow.map(({ icon: Icon, title, detail }, i) => (
                <li key={title}>
                  <span className="lp-step-number">0{i + 1}</span>
                  <div className="lp-workflow-icon">
                    <Icon />
                  </div>
                  <h3>{title}</h3>
                  <p>{detail}</p>
                  {i < workflow.length - 1 && (
                    <ArrowRight className="lp-step-arrow" size={16} />
                  )}
                </li>
              ))}
            </ol>
          </div>
        </section>

        <section className="lp-section lp-container lp-intelligence">
          <div className="lp-detection">
            <span className="lp-eyebrow">AI DETECTION</span>
            <h2>
              Designed to find
              <br />
              <span>meaning in the noise.</span>
            </h2>
            <p>
              The backend's Random Forest model analyzes destination port, flow
              duration, packet rate, and byte rate to classify submitted network
              events.
            </p>
            <p>
              Android and web clients connect to that existing detection engine.
              Results bring together a classification, confidence, and SHAP
              feature contributions.
            </p>
            <div className="lp-tech-tags">
              <span>Random Forest</span>
              <span>Backend inference</span>
              <span>Five categories</span>
            </div>
            <div className="lp-feature-input">
              <Database size={22} />
              <span>Network event features</span>
              <ArrowRight size={18} />
              <BrainCircuit size={25} />
            </div>
          </div>
          <article className="lp-explain lp-card">
            <span className="lp-eyebrow">EXPLAINABLE AI / SHAP</span>
            <h2>
              Beyond a verdict.
              <br />
              <span>Understand the why.</span>
            </h2>
            <p>
              SHAP shows how individual features contribute to the predicted
              class. Inspect the evidence behind a suspicious or malicious
              classification.
            </p>
            <div
              className="lp-shap-diagram"
              aria-label="Conceptual feature contribution diagram, not measured results"
            >
              <div className="lp-shap-label">
                <span>FEATURE CONTRIBUTIONS</span>
                <Fingerprint size={18} />
              </div>
              {[
                "Packet rate",
                "Byte rate",
                "Flow duration",
                "Destination port",
              ].map((feature, i) => (
                <div className="lp-shap-row" key={feature}>
                  <span>{feature}</span>
                  <div>
                    <i style={{ width: `${[82, 63, 43, 28][i]}%` }} />
                  </div>
                </div>
              ))}
              <small>
                Illustrative explanation · no measured results shown
              </small>
            </div>
          </article>
        </section>

        <section className="lp-section lp-container" id="scenarios">
          <SectionHeading
            eyebrow="EXPLORE THE SYSTEM"
            title={
              <>
                Five scenarios.<span> One clear workflow.</span>
              </>
            }
          >
            Controlled testing scenarios for exploring detection and
            explanation. These are not live attacks on your device.
          </SectionHeading>
          <div className="lp-scenarios">
            {scenarios.map(({ icon: Icon, name, text, tag }) => (
              <article className="lp-scenario" key={name}>
                <Icon size={25} />
                <small>{tag}</small>
                <h3>{name}</h3>
                <p>{text}</p>
              </article>
            ))}
          </div>
        </section>

        <section id="android" className="lp-section lp-android-section">
          <div className="lp-container lp-android">
            <PhonePreview />
            <div>
              <span className="lp-pill">
                <Smartphone size={14} /> BUILT FOR ANDROID
              </span>
              <h2>
                Take AI-CTDRS
                <br />
                <span>with you.</span>
              </h2>
              <p>
                Your security workflow, in a mobile client. Submit controlled
                scenarios, inspect AI results and SHAP explanations, and review
                incidents from your Android device.
              </p>
              <ul className="lp-checklist">
                <li>
                  <Check /> Explore all five controlled scenarios
                </li>
                <li>
                  <Check /> Understand classifications with SHAP
                </li>
                <li>
                  <Check /> Review incidents and response records
                </li>
                <li>
                  <Check /> Connect to your AI-CTDRS backend
                </li>
              </ul>
              <DownloadButton />
              <p className="lp-download-note" role="status">
                {androidApkUrl
                  ? "Android APK · available from the configured download location."
                  : "The Android download is not available yet. A release link will appear here when published."}
              </p>
            </div>
          </div>
        </section>

        <section className="lp-section lp-container">
          <SectionHeading
            eyebrow="A CONNECTED SECURITY WORKSPACE"
            title={
              <>
                Two clients.<span> Shared intelligence.</span>
              </>
            }
          >
            Android and the SOC dashboard connect to the same backend workflow
            for detection, incident management, and response records.
          </SectionHeading>
          <div
            className="lp-architecture"
            aria-label="Android client and SOC dashboard connect to the AI detection engine, which feeds incident management and recorded response"
          >
            <div className="lp-architecture-clients">
              <div>
                <Smartphone />
                <span>Android Client</span>
              </div>
              <div>
                <LayoutDashboard />
                <span>SOC Dashboard</span>
              </div>
            </div>
            <span className="lp-connector">
              Authenticated API <ArrowRight size={18} />
            </span>
            <div className="lp-engine">
              <BrainCircuit />
              <strong>AI Detection Engine</strong>
              <small>Random Forest + SHAP</small>
            </div>
            <ArrowRight className="lp-architecture-arrow" />
            <div className="lp-architecture-clients">
              <div>
                <GitBranch />
                <span>Incident Management</span>
              </div>
              <div>
                <Workflow />
                <span>Recorded Response</span>
              </div>
            </div>
          </div>
          <p className="lp-architecture-note">
            Response actions in this academic project represent controlled event
            handling, not device-level blocking or production network
            enforcement.
          </p>
        </section>

        <section id="dashboard" className="lp-container lp-soc">
          <div className="lp-soc-mark">
            <LayoutDashboard size={46} strokeWidth={1} />
          </div>
          <div>
            <span className="lp-eyebrow">YOUR SECURITY OPERATIONS CENTER</span>
            <h2>
              The bigger picture.
              <br />
              <span>Ready when you are.</span>
            </h2>
            <p>
              Open the existing SOC dashboard to review events, investigate
              detections, and follow incidents and response records. Sign in
              with your AI-CTDRS account.
            </p>
            <DashboardButton />
          </div>
          <div className="lp-soc-links">
            <span>
              <Activity /> Event feed
            </span>
            <span>
              <Fingerprint /> Threat analysis
            </span>
            <span>
              <ShieldCheck /> Incident review
            </span>
          </div>
        </section>
      </main>
      <footer className="lp-footer lp-container">
        <div>
          <Brand />
          <p>
            AI-Driven Cyber Threat Detection
            <br />
            and Response System
          </p>
          <small>An academic software project for explainable security.</small>
        </div>
        <nav aria-label="Footer navigation">
          <a href="#features">Features</a>
          <a href="#how-it-works">How It Works</a>
          <a href="#android">Android App</a>
          <Link to="/dashboard">
            Dashboard <ArrowRight size={14} />
          </Link>
        </nav>
        <span className="lp-footer-signoff">DETECT. EXPLAIN. RESPOND.</span>
      </footer>
    </div>
  );
}
