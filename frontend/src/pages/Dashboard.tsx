import { useEffect, useState } from 'react';
import { Activity, Crosshair, Zap, Lock, RefreshCw, Server, AlertTriangle, ShieldCheck } from 'lucide-react';
import { XAxis, YAxis, CartesianGrid, Tooltip as RechartsTooltip, ResponsiveContainer, AreaChart, Area } from 'recharts';
import api from '../lib/api';

interface DashboardSummary {
  eventsAnalyzed: number;
  threatsDetected: number;
  criticalIncidents: number;
  contained: number;
}

const mockChartData = [
  { time: '00:00', events: 1200, threats: 12 },
  { time: '04:00', events: 900, threats: 5 },
  { time: '08:00', events: 2400, threats: 45 },
  { time: '12:00', events: 3200, threats: 80 },
  { time: '16:00', events: 2800, threats: 60 },
  { time: '20:00', events: 1800, threats: 25 },
  { time: '24:00', events: 1500, threats: 15 },
];

export default function Dashboard() {
  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const fetchSummary = async () => {
    setIsLoading(true);
    try {
      const response = await api.get('/dashboard/summary');
      setSummary(response.data);
    } catch (error) {
      console.error("Failed to fetch dashboard summary", error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchSummary();
  }, []);

  const StatCard = ({ title, value, icon: Icon, colorClass, trend }: any) => (
    <div className="bg-navy border border-navy-dark rounded-xl p-5 shadow-lg relative overflow-hidden group">
      <div className={`absolute top-0 right-0 w-24 h-24 -mr-8 -mt-8 rounded-full opacity-10 transition-transform group-hover:scale-150 ${colorClass.bg}`}></div>
      <div className="flex justify-between items-start relative z-10">
        <div>
          <p className="text-sm font-medium text-text-muted mb-1">{title}</p>
          <h3 className="text-3xl font-bold text-text-light">
            {isLoading ? <div className="h-9 w-16 bg-navy-dark animate-pulse rounded"></div> : value}
          </h3>
          {trend && (
            <p className="mt-2 text-xs text-semantic-success flex items-center">
              <span className="text-semantic-success font-semibold">{trend}</span>
              <span className="text-text-muted ml-1">vs last 24h</span>
            </p>
          )}
        </div>
        <div className={`p-3 rounded-lg ${colorClass.bg} ${colorClass.text} bg-opacity-20`}>
          <Icon className="h-6 w-6" />
        </div>
      </div>
    </div>
  );

  return (
    <div className="space-y-6">
      <div className="flex flex-wrap gap-3 justify-between items-center">
        <div>
          <h1 className="text-2xl font-bold text-text-light tracking-tight">Global Security Posture</h1>
          <p className="text-sm text-text-muted mt-1">Real-time threat landscape and autonomous response metrics.</p>
        </div>
        <button 
          onClick={fetchSummary}
          className="flex items-center px-4 py-2 bg-navy-dark border border-navy text-text-light text-sm font-medium rounded-md hover:bg-navy transition-colors focus:outline-none focus:ring-2 focus:ring-teal"
        >
          <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? 'animate-spin text-teal' : 'text-text-muted'}`} />
          Refresh Data
        </button>
      </div>

      {/* Metrics Row */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard 
          title="Events Analyzed" 
          value={summary?.eventsAnalyzed.toLocaleString() || '0'} 
          icon={Activity} 
          trend="+12.5%"
          colorClass={{ bg: 'bg-teal', text: 'text-teal' }} 
        />
        <StatCard 
          title="Threats Detected" 
          value={summary?.threatsDetected.toLocaleString() || '0'} 
          icon={Crosshair} 
          trend="-2.1%"
          colorClass={{ bg: 'bg-semantic-warning', text: 'text-semantic-warning' }} 
        />
        <StatCard 
          title="Critical Incidents" 
          value={summary?.criticalIncidents.toLocaleString() || '0'} 
          icon={AlertTriangle} 
          colorClass={{ bg: 'bg-semantic-danger', text: 'text-semantic-danger' }} 
        />
        <StatCard 
          title="Auto-Contained" 
          value={summary?.contained.toLocaleString() || '0'} 
          icon={ShieldCheck} 
          trend="+100%"
          colorClass={{ bg: 'bg-semantic-success', text: 'text-semantic-success' }} 
        />
      </div>

      {/* Charts / Empty State Row */}
      {summary && summary.eventsAnalyzed === 0 ? (
        <div className="bg-navy border border-navy-dark rounded-xl p-8 shadow-lg text-center flex flex-col items-center justify-center min-h-[300px]">
          <ShieldCheck className="h-16 w-16 text-teal mb-4 opacity-80" />
          <h2 className="text-2xl font-bold text-text-light mb-2">System Ready</h2>
          <p className="text-text-muted max-w-lg mb-6">
            AI-CTDRS protection is active. No personal security events have been analyzed yet for this account. 
            The Random Forest inference engine is online and monitoring for Normal, Port Scan, Brute Force, DDoS, and Malware Traffic.
          </p>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 w-full max-w-4xl text-left">
            <div className="bg-navy-dark p-4 rounded-lg border border-navy">
              <p className="text-sm font-medium text-text-light flex items-center">
                <Server className="h-4 w-4 text-teal mr-2" /> Inference Engine
              </p>
              <p className="text-xs text-text-muted mt-1">Random Forest v1.2 — <span className="text-semantic-success">Online</span></p>
            </div>
            <div className="bg-navy-dark p-4 rounded-lg border border-navy">
              <p className="text-sm font-medium text-text-light flex items-center">
                <Zap className="h-4 w-4 text-teal mr-2" /> Response Engine
              </p>
              <p className="text-xs text-text-muted mt-1">Auto-containment Active — <span className="text-semantic-success">Online</span></p>
            </div>
            <div className="bg-navy-dark p-4 rounded-lg border border-navy">
              <p className="text-sm font-medium text-text-light flex items-center">
                <Lock className="h-4 w-4 text-teal mr-2" /> Supported Threats
              </p>
              <p className="text-xs text-text-muted mt-1">5 categories tracked — <span className="text-semantic-success">Active</span></p>
            </div>
          </div>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <div className="lg:col-span-2 bg-navy border border-navy-dark rounded-xl p-5 shadow-lg">
            <div className="flex flex-wrap gap-3 justify-between items-center mb-6">
              <h2 className="text-lg font-semibold text-text-light">Network Traffic & Threat Volume</h2>
              <div className="flex items-center space-x-4 text-xs">
                <div className="flex items-center"><span className="w-3 h-3 rounded-full bg-teal mr-2"></span><span className="text-text-muted">Total Events</span></div>
                <div className="flex items-center"><span className="w-3 h-3 rounded-full bg-semantic-danger mr-2"></span><span className="text-text-muted">Threats</span></div>
              </div>
            </div>
            <div className="h-72 w-full">
              <ResponsiveContainer width="100%" height="100%">
                <AreaChart data={mockChartData} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
                  <defs>
                    <linearGradient id="colorEvents" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#17C3B2" stopOpacity={0.3}/>
                      <stop offset="95%" stopColor="#17C3B2" stopOpacity={0}/>
                    </linearGradient>
                    <linearGradient id="colorThreats" x1="0" y1="0" x2="0" y2="1">
                      <stop offset="5%" stopColor="#EF4444" stopOpacity={0.3}/>
                      <stop offset="95%" stopColor="#EF4444" stopOpacity={0}/>
                    </linearGradient>
                  </defs>
                  <CartesianGrid strokeDasharray="3 3" stroke="#13293D" vertical={false} />
                  <XAxis dataKey="time" stroke="#6E8899" fontSize={12} tickLine={false} axisLine={false} />
                  <YAxis yAxisId="left" stroke="#6E8899" fontSize={12} tickLine={false} axisLine={false} />
                  <YAxis yAxisId="right" orientation="right" stroke="#EF4444" fontSize={12} tickLine={false} axisLine={false} />
                  <RechartsTooltip 
                    contentStyle={{ backgroundColor: '#0B1B2B', borderColor: '#13293D', color: '#E8F1F2' }}
                    itemStyle={{ color: '#E8F1F2' }}
                  />
                  <Area yAxisId="left" type="monotone" dataKey="events" stroke="#17C3B2" strokeWidth={2} fillOpacity={1} fill="url(#colorEvents)" />
                  <Area yAxisId="right" type="monotone" dataKey="threats" stroke="#EF4444" strokeWidth={2} fillOpacity={1} fill="url(#colorThreats)" />
                </AreaChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="bg-navy border border-navy-dark rounded-xl p-5 shadow-lg flex flex-col">
            <h2 className="text-lg font-semibold text-text-light mb-4">System Health</h2>
            <div className="flex-1 flex flex-col justify-between space-y-4">
              
              <div className="bg-navy-dark p-4 rounded-lg border border-navy flex items-center justify-between">
                <div className="flex items-center">
                  <div className="p-2 bg-teal bg-opacity-20 rounded-md mr-3 text-teal">
                    <Server className="h-5 w-5" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-text-light">Inference Engine</p>
                    <p className="text-xs text-text-muted">Random Forest v1.2</p>
                  </div>
                </div>
                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-semantic-success bg-opacity-20 text-semantic-success">
                  Online
                </span>
              </div>

              <div className="bg-navy-dark p-4 rounded-lg border border-navy flex items-center justify-between">
                <div className="flex items-center">
                  <div className="p-2 bg-teal bg-opacity-20 rounded-md mr-3 text-teal">
                    <Zap className="h-5 w-5" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-text-light">Response Engine</p>
                    <p className="text-xs text-text-muted">Auto-containment Active</p>
                  </div>
                </div>
                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-semantic-success bg-opacity-20 text-semantic-success">
                  Online
                </span>
              </div>

              <div className="bg-navy-dark p-4 rounded-lg border border-navy flex items-center justify-between">
                <div className="flex items-center">
                  <div className="p-2 bg-teal bg-opacity-20 rounded-md mr-3 text-teal">
                    <Lock className="h-5 w-5" />
                  </div>
                  <div>
                    <p className="text-sm font-medium text-text-light">Data Pipeline</p>
                    <p className="text-xs text-text-muted">Ingestion rate: 450 eps</p>
                  </div>
                </div>
                <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-semantic-success bg-opacity-20 text-semantic-success">
                  Online
                </span>
              </div>
            </div>
          </div>
        </div>
      )}
      
      {/* Prominent CTA */}
      <div className="bg-navy border border-teal rounded-xl p-6 shadow-[0_0_15px_rgba(23,195,178,0.2)] flex flex-col md:flex-row items-center justify-between">
        <div>
          <h2 className="text-xl font-bold text-text-light flex items-center">
            <Crosshair className="h-6 w-6 text-teal mr-2" />
            Analyze New Threat Event
          </h2>
          <p className="text-sm text-text-muted mt-1">Submit a new network event payload for ML-driven cognitive analysis.</p>
        </div>
        <button
          onClick={() => window.location.href = '/analysis'}
          className="mt-4 md:mt-0 px-6 py-3 bg-teal hover:bg-teal-light text-navy-dark font-bold rounded-lg transition-colors shadow-[0_0_10px_rgba(23,195,178,0.4)] flex items-center"
        >
          Initialize Analysis
          <Zap className="h-5 w-5 ml-2" />
        </button>
      </div>
    </div>
  );
}
