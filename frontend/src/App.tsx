import { lazy, Suspense } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./lib/AuthContext";
import Landing from "./pages/Landing";

const Login = lazy(() => import("./pages/Login"));
const Register = lazy(() => import("./pages/Register"));
const Layout = lazy(() => import("./components/Layout"));
const Dashboard = lazy(() => import("./pages/Dashboard"));
const ThreatAnalysis = lazy(() => import("./pages/ThreatAnalysis"));
const Incidents = lazy(() => import("./pages/Incidents"));
const LiveEvents = lazy(() => import("./pages/LiveEvents"));
const IncidentDetails = lazy(() => import("./pages/IncidentDetails"));
const ResponseCenter = lazy(() => import("./pages/ResponseCenter"));
const Analytics = lazy(() => import("./pages/Analytics"));
const Reports = lazy(() => import("./pages/Reports"));
const SystemStatus = lazy(() => import("./pages/SystemStatus"));
const ProfileSettings = lazy(() => import("./pages/ProfileSettings"));

const ProtectedRoute = ({ children }: { children: React.ReactNode }) => {
  const { isAuthenticated } = useAuth();
  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }
  return <>{children}</>;
};

function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route
        element={
          <ProtectedRoute>
            <Layout />
          </ProtectedRoute>
        }
      >
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="analysis" element={<ThreatAnalysis />} />
        <Route path="events" element={<LiveEvents />} />
        <Route path="incidents" element={<Incidents />} />
        <Route path="incidents/:id" element={<IncidentDetails />} />
        <Route path="response" element={<ResponseCenter />} />
        <Route path="analytics" element={<Analytics />} />
        <Route path="reports" element={<Reports />} />
        <Route path="status" element={<SystemStatus />} />
        <Route path="settings" element={<ProfileSettings />} />
      </Route>
    </Routes>
  );
}

function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Suspense
          fallback={
            <div
              role="status"
              className="min-h-screen flex items-center justify-center"
            >
              Loading workspace…
            </div>
          }
        >
          <AppRoutes />
        </Suspense>
      </BrowserRouter>
    </AuthProvider>
  );
}

export default App;
