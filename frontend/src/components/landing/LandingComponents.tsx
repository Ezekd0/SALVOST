import { useRef, useState } from "react";
import { Link } from "react-router-dom";
import {
  Activity,
  ArrowDownToLine,
  ArrowUpRight,
  BrainCircuit,
  ChevronRight,
  Fingerprint,
  LayoutDashboard,
  Menu,
  Shield,
  ShieldCheck,
  Smartphone,
  X,
} from "lucide-react";
import type { ReactNode } from "react";

const configuredApkUrl = (import.meta.env.VITE_ANDROID_APK_URL || "").trim();
// Accept an HTTPS release asset or a same-origin absolute path, never a script URL.
export const androidApkUrl = /^(https:\/\/|\/(?!\/))/.test(configuredApkUrl)
  ? configuredApkUrl
  : "";

export function DownloadButton({ compact = false }: { compact?: boolean }) {
  const label = compact ? "Download App" : "Download Android App";
  return androidApkUrl ? (
    <a className="lp-button lp-primary" href={androidApkUrl}>
      <ArrowDownToLine size={17} />
      {label}
    </a>
  ) : (
    <a className="lp-button lp-primary" href="#android">
      <ArrowDownToLine size={17} />
      {label}
    </a>
  );
}

export function DashboardButton() {
  return (
    <Link className="lp-button lp-secondary" to="/dashboard">
      Open Security Dashboard <ArrowUpRight size={17} />
    </Link>
  );
}

export function Brand() {
  return (
    <a href="/#home" className="lp-brand" aria-label="AI-CTDRS home">
      <span className="lp-brand-icon">
        <Shield size={23} />
      </span>
      AI-CTDRS<span className="lp-brand-dot">.</span>
    </a>
  );
}

export function LandingNav() {
  const [open, setOpen] = useState(false);
  const toggle = useRef<HTMLButtonElement>(null);
  return (
    <header className="lp-header">
      <div className="lp-container lp-nav">
        <Brand />
        <button
          ref={toggle}
          className="lp-menu-toggle"
          aria-label={open ? "Close navigation" : "Open navigation"}
          aria-expanded={open}
          aria-controls="landing-navigation"
          onClick={() => setOpen(!open)}
        >
          {open ? <X /> : <Menu />}
        </button>
        <nav
          id="landing-navigation"
          className={open ? "lp-nav-links is-open" : "lp-nav-links"}
          aria-label="Main navigation"
          onClick={() => setOpen(false)}
          onKeyDown={(e) => {
            if (e.key === "Escape") {
              setOpen(false);
              toggle.current?.focus();
            }
          }}
        >
          <a href="#home">Home</a>
          <a href="#features">Features</a>
          <a href="#how-it-works">How It Works</a>
          <a href="#android">Android App</a>
          <Link to="/dashboard">
            Security Dashboard <ArrowUpRight size={13} />
          </Link>
          <DownloadButton compact />
        </nav>
      </div>
    </header>
  );
}

export function SectionHeading({
  eyebrow,
  title,
  children,
}: {
  eyebrow: string;
  title: ReactNode;
  children?: ReactNode;
}) {
  return (
    <div className="lp-section-heading">
      <span className="lp-eyebrow">{eyebrow}</span>
      <h2>{title}</h2>
      {children && <p>{children}</p>}
    </div>
  );
}

export function SecurityVisual() {
  return (
    <div
      className="lp-security-visual"
      role="img"
      aria-label="Illustration of events flowing through AI detection, SHAP explanation, and response"
    >
      <div className="lp-orbit lp-orbit-outer" />
      <div className="lp-orbit lp-orbit-inner" />
      <div className="lp-orbit lp-orbit-small" />
      <svg
        className="lp-network-lines"
        viewBox="0 0 520 470"
        aria-hidden="true"
      >
        <path d="M80 115L260 235L435 115M70 340L260 235L440 345M260 45V425" />
        <circle cx="80" cy="115" r="4" />
        <circle cx="435" cy="115" r="4" />
        <circle cx="70" cy="340" r="4" />
        <circle cx="440" cy="345" r="4" />
      </svg>
      <div className="lp-shield-core">
        <ShieldCheck strokeWidth={1} />
        <span>AI-CTDRS</span>
      </div>
      <div className="lp-signal lp-signal-one">
        <Activity />
        <span>
          <small>01 / INPUT</small>Security events
        </span>
      </div>
      <div className="lp-signal lp-signal-two">
        <BrainCircuit />
        <span>
          <small>02 / DETECT</small>AI analysis
        </span>
      </div>
      <div className="lp-signal lp-signal-three">
        <Fingerprint />
        <span>
          <small>03 / EXPLAIN</small>SHAP insights
        </span>
      </div>
      <div className="lp-signal lp-signal-four">
        <Shield />
        <span>
          <small>04 / RESPOND</small>Incident response
        </span>
      </div>
      <span className="lp-visual-caption">DETECT. UNDERSTAND. RESPOND.</span>
    </div>
  );
}

export function PhonePreview() {
  return (
    <figure className="lp-phone-figure">
      <div className="lp-phone">
        <div className="lp-phone-camera" />
        <div className="lp-phone-top">
          <Shield size={19} />
          <strong>AI-CTDRS</strong>
          <span>ANDROID</span>
        </div>
        <div className="lp-phone-greeting">
          <small>YOUR SECURITY WORKSPACE</small>
          <h3>Clarity. In your hands.</h3>
        </div>
        <div className="lp-phone-shield">
          <ShieldCheck size={66} strokeWidth={1} />
          <strong>Understand every signal</strong>
          <span>Detection with explanation</span>
        </div>
        <div className="lp-phone-cards">
          <div>
            <Activity />
            <strong>Analyze</strong>
            <span>Security events</span>
          </div>
          <div>
            <Fingerprint />
            <strong>Explain</strong>
            <span>SHAP insights</span>
          </div>
        </div>
        <div className="lp-phone-row">
          <Shield size={18} />
          <span>Review incidents</span>
          <ChevronRight size={16} />
        </div>
        <div className="lp-phone-row">
          <BrainCircuit size={18} />
          <span>Explore test scenarios</span>
          <ChevronRight size={16} />
        </div>
        <div className="lp-phone-bottom">
          <LayoutDashboard />
          <Activity />
          <Shield />
          <Smartphone />
        </div>
      </div>
      <figcaption>
        Illustrative product preview · not an app screenshot
      </figcaption>
    </figure>
  );
}
