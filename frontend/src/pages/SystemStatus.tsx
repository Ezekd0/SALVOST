import { Activity, Server, Database, Shield, Cpu, Network, CheckCircle, AlertTriangle } from 'lucide-react';

export default function SystemStatus() {
  const components = [
    { name: 'API Gateway', status: 'Operational', uptime: '99.99%', latency: '12ms', icon: Network },
    { name: 'ML Inference Engine', status: 'Operational', uptime: '99.95%', latency: '45ms', icon: Cpu },
    { name: 'Feature Extraction Pipeline', status: 'Operational', uptime: '99.98%', latency: '18ms', icon: Activity },
    { name: 'Event Database', status: 'Operational', uptime: '99.99%', latency: '5ms', icon: Database },
    { name: 'Response Orchestrator', status: 'Operational', uptime: '100%', latency: '22ms', icon: Shield },
    { name: 'Log Ingestion', status: 'Degraded', uptime: '98.50%', latency: '150ms', icon: Server },
  ];

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-end gap-4">
        <div>
          <h1 className="text-2xl font-bold text-text-light tracking-tight flex items-center">
            <Activity className="h-6 w-6 mr-2 text-teal" />
            System Status
          </h1>
          <p className="text-sm text-text-muted mt-1">Platform health, component uptime, and operational metrics.</p>
        </div>
      </div>

      <div className="bg-navy border border-navy-dark rounded-xl shadow-lg p-6 flex flex-col md:flex-row items-center justify-between">
        <div className="flex items-center mb-4 md:mb-0">
          <div className="relative">
            <div className="h-16 w-16 rounded-full bg-semantic-success/20 flex items-center justify-center">
              <CheckCircle className="h-8 w-8 text-semantic-success" />
            </div>
            <div className="absolute top-0 right-0 h-4 w-4 rounded-full bg-semantic-success animate-ping"></div>
          </div>
          <div className="ml-6">
            <h2 className="text-2xl font-bold text-text-light">All Systems Operational</h2>
            <p className="text-text-muted mt-1">Last incident was 14 days ago.</p>
          </div>
        </div>
        <div className="text-center md:text-right">
          <p className="text-sm text-text-muted uppercase tracking-wider font-semibold">Current Load</p>
          <p className="text-3xl font-mono font-light text-text-light mt-1">34%</p>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {components.map((comp, idx) => {
          const isOperational = comp.status === 'Operational';
          return (
            <div key={idx} className="bg-navy border border-navy-dark rounded-xl p-5 shadow-lg relative overflow-hidden group">
              <div className="flex justify-between items-start mb-4">
                <div className="flex items-center">
                  <div className={`p-2 rounded-lg mr-3 ${isOperational ? 'bg-teal/20 text-teal' : 'bg-semantic-warning/20 text-semantic-warning'}`}>
                    <comp.icon className="h-5 w-5" />
                  </div>
                  <h3 className="font-semibold text-text-light">{comp.name}</h3>
                </div>
                {isOperational ? (
                  <CheckCircle className="h-5 w-5 text-semantic-success" />
                ) : (
                  <AlertTriangle className="h-5 w-5 text-semantic-warning animate-pulse" />
                )}
              </div>
              
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-6">
                <div>
                  <p className="text-xs text-text-muted uppercase">Status</p>
                  <p className={`font-medium ${isOperational ? 'text-semantic-success' : 'text-semantic-warning'}`}>{comp.status}</p>
                </div>
                <div>
                  <p className="text-xs text-text-muted uppercase">Latency</p>
                  <p className="font-mono text-text-light">{comp.latency}</p>
                </div>
                <div>
                  <p className="text-xs text-text-muted uppercase">Uptime</p>
                  <p className="font-mono text-text-light">{comp.uptime}</p>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
