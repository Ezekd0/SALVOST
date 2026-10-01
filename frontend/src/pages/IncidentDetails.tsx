import { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Activity, ShieldAlert, Cpu, Network,  Clock, Server, AlertTriangle } from 'lucide-react';
import { format } from 'date-fns';
import api from '../lib/api';

export default function IncidentDetails() {
  const { id } = useParams();
  const [incident, setIncident] = useState<any>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchIncident = async () => {
      try {
        const response = await api.get(`/incidents/${id}`);
        setIncident(response.data);
      } catch (error) {
        console.error("Failed to fetch incident", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchIncident();
  }, [id]);

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-64">
        <div className="animate-spin h-8 w-8 border-4 border-teal border-t-transparent rounded-full"></div>
      </div>
    );
  }

  if (!incident) {
    return (
      <div className="text-center py-12">
        <AlertTriangle className="h-12 w-12 text-semantic-warning mx-auto mb-4" />
        <h2 className="text-xl font-bold text-text-light">Incident Not Found</h2>
        <p className="text-text-muted mt-2">The requested incident could not be located in the database.</p>
        <Link to="/incidents" className="mt-4 inline-block text-teal hover:underline">Return to Incidents</Link>
      </div>
    );
  }

  const { analysis } = incident;
  const { event } = analysis;
  let explanation = {};
  try {
    explanation = JSON.parse(analysis.explanation_json);
  } catch (e) {}

  return (
    <div className="space-y-6">
      <div>
        <Link to="/incidents" className="inline-flex items-center text-sm text-text-muted hover:text-text-light mb-4 transition-colors">
          <ArrowLeft className="h-4 w-4 mr-1" />
          Back to Registry
        </Link>
        <div className="flex justify-between items-start">
          <div>
            <h1 className="text-2xl font-bold text-text-light tracking-tight flex items-center">
              Incident INC-{incident.id.toString().padStart(5, '0')}
            </h1>
            <p className="text-sm text-text-muted mt-1">Detected at {format(new Date(incident.created_at), 'yyyy-MM-dd HH:mm:ss')}</p>
          </div>
          <div className="flex items-center space-x-3">
            <span className={`px-3 py-1 rounded-full text-sm font-bold uppercase tracking-wide border ${
              incident.severity === 'Critical' ? 'bg-semantic-critical/20 text-semantic-critical border-semantic-critical/50' :
              incident.severity === 'High' ? 'bg-semantic-danger/20 text-semantic-danger border-semantic-danger/50' :
              incident.severity === 'Medium' ? 'bg-semantic-warning/20 text-semantic-warning border-semantic-warning/50' :
              'bg-teal/20 text-teal border-teal/50'
            }`}>
              {incident.severity} Severity
            </span>
            <span className="px-3 py-1 rounded-full text-sm font-bold uppercase tracking-wide border bg-navy-dark text-text-light border-navy">
              Status: {incident.status}
            </span>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Left Column - Details */}
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-navy border border-navy-dark rounded-xl shadow-lg overflow-hidden">
            <div className="p-4 border-b border-navy-dark flex items-center bg-[#0B1B2B]/50">
              <ShieldAlert className="h-5 w-5 mr-2 text-teal" />
              <h2 className="text-lg font-semibold text-text-light">Threat Classification</h2>
            </div>
            <div className="p-6 flex flex-col md:flex-row items-center justify-between gap-6">
              <div className="flex-1">
                <p className="text-sm text-text-muted uppercase tracking-wider font-semibold mb-1">Detected Vector</p>
                <h3 className="text-3xl font-black text-semantic-danger">{analysis.attack_type}</h3>
                <p className="text-sm text-text-light mt-2">
                  The AI inference engine identified malicious patterns consistent with a {analysis.attack_type} attack.
                </p>
              </div>
              <div className="flex flex-col items-center justify-center p-6 bg-navy-dark rounded-full border-4 border-semantic-danger w-32 h-32 relative">
                <span className="text-3xl font-bold text-text-light">{(analysis.confidence * 100).toFixed(0)}%</span>
                <span className="text-xs text-text-muted uppercase tracking-wider absolute bottom-4">Confidence</span>
              </div>
            </div>
          </div>

          <div className="bg-navy border border-navy-dark rounded-xl shadow-lg overflow-hidden">
            <div className="p-4 border-b border-navy-dark flex items-center bg-[#0B1B2B]/50">
              <Network className="h-5 w-5 mr-2 text-teal" />
              <h2 className="text-lg font-semibold text-text-light">Network Telemetry</h2>
            </div>
            <div className="p-6">
              <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                <div className="bg-navy-dark p-3 rounded border border-navy">
                  <p className="text-xs text-text-muted uppercase">Source IP</p>
                  <p className="text-sm font-mono text-text-light mt-1">{event.source_ip}</p>
                </div>
                <div className="bg-navy-dark p-3 rounded border border-navy">
                  <p className="text-xs text-text-muted uppercase">Dest IP</p>
                  <p className="text-sm font-mono text-text-light mt-1">{event.destination_ip}</p>
                </div>
                <div className="bg-navy-dark p-3 rounded border border-navy">
                  <p className="text-xs text-text-muted uppercase">Dest Port</p>
                  <p className="text-sm font-mono text-text-light mt-1">{event.destination_port}</p>
                </div>
                <div className="bg-navy-dark p-3 rounded border border-navy">
                  <p className="text-xs text-text-muted uppercase">Protocol</p>
                  <p className="text-sm font-mono text-text-light mt-1">{event.protocol}</p>
                </div>
                <div className="bg-navy-dark p-3 rounded border border-navy">
                  <p className="text-xs text-text-muted uppercase">Flow Duration</p>
                  <p className="text-sm font-mono text-text-light mt-1">{event.flow_duration.toFixed(2)}s</p>
                </div>
                <div className="bg-navy-dark p-3 rounded border border-navy">
                  <p className="text-xs text-text-muted uppercase">Packet Rate</p>
                  <p className="text-sm font-mono text-text-light mt-1">{event.packet_rate.toFixed(0)} p/s</p>
                </div>
                <div className="bg-navy-dark p-3 rounded border border-navy col-span-2">
                  <p className="text-xs text-text-muted uppercase">Bytes Rate</p>
                  <p className="text-sm font-mono text-text-light mt-1">{event.bytes_rate.toFixed(0)} b/s</p>
                </div>
              </div>
            </div>
          </div>
          
          <div className="bg-navy border border-navy-dark rounded-xl shadow-lg overflow-hidden">
            <div className="p-4 border-b border-navy-dark flex items-center bg-[#0B1B2B]/50">
              <Cpu className="h-5 w-5 mr-2 text-teal" />
              <h2 className="text-lg font-semibold text-text-light">Explainable AI (SHAP)</h2>
            </div>
            <div className="p-6">
              <p className="text-sm text-text-muted mb-4">Feature importance values contributing to the threat classification.</p>
              <div className="space-y-3">
                {Object.entries(explanation).map(([feature, value]: [string, any]) => {
                  const numValue = Math.abs(parseFloat(value));
                  const percentage = Math.min(numValue * 100, 100);
                  const isPositive = parseFloat(value) > 0;
                  return (
                    <div key={feature}>
                      <div className="flex justify-between text-xs mb-1">
                        <span className="font-mono text-text-light">{feature}</span>
                        <span className={isPositive ? 'text-semantic-danger' : 'text-teal'}>{value > 0 ? '+' : ''}{value.toFixed(4)}</span>
                      </div>
                      <div className="w-full bg-navy-dark rounded-full h-2">
                        <div 
                          className={`h-2 rounded-full ${isPositive ? 'bg-semantic-danger' : 'bg-teal'}`} 
                          style={{ width: `${percentage}%` }}
                        ></div>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>

        {/* Right Column - Timeline/Actions */}
        <div className="space-y-6">
          <div className="bg-navy border border-navy-dark rounded-xl shadow-lg overflow-hidden h-full">
            <div className="p-4 border-b border-navy-dark flex items-center bg-[#0B1B2B]/50">
              <Clock className="h-5 w-5 mr-2 text-teal" />
              <h2 className="text-lg font-semibold text-text-light">Response Timeline</h2>
            </div>
            <div className="p-6">
              <div className="relative border-l border-navy-dark ml-3 space-y-8">
                
                <div className="relative">
                  <div className="absolute -left-[21px] bg-navy p-1 rounded-full">
                    <Activity className="h-4 w-4 text-teal" />
                  </div>
                  <div className="pl-4">
                    <p className="text-xs text-text-muted">{format(new Date(event.timestamp), 'HH:mm:ss.SSS')}</p>
                    <p className="text-sm font-medium text-text-light mt-1">Event Ingested</p>
                    <p className="text-xs text-text-muted mt-1">Telemetry received from Edge Sensor</p>
                  </div>
                </div>

                <div className="relative">
                  <div className="absolute -left-[21px] bg-navy p-1 rounded-full">
                    <Cpu className="h-4 w-4 text-semantic-warning" />
                  </div>
                  <div className="pl-4">
                    <p className="text-xs text-text-muted">{format(new Date(analysis.timestamp), 'HH:mm:ss.SSS')}</p>
                    <p className="text-sm font-medium text-text-light mt-1">AI Inference Complete</p>
                    <p className="text-xs text-text-muted mt-1">Detected {analysis.attack_type}</p>
                  </div>
                </div>

                {incident.responses?.map((resp: any, index: number) => (
                  <div className="relative" key={index}>
                    <div className="absolute -left-[21px] bg-navy p-1 rounded-full">
                      <Server className="h-4 w-4 text-semantic-danger" />
                    </div>
                    <div className="pl-4">
                      <p className="text-xs text-text-muted">{format(new Date(resp.timestamp), 'HH:mm:ss.SSS')}</p>
                      <p className="text-sm font-medium text-text-light mt-1">Auto-Response Executed</p>
                      <p className="text-xs text-text-muted mt-1 font-mono">{resp.action_type}</p>
                    </div>
                  </div>
                ))}

              </div>
              
              <div className="mt-8 pt-6 border-t border-navy-dark space-y-3">
                <button className="w-full py-2 bg-navy-dark border border-teal text-teal hover:bg-teal hover:text-navy-dark transition-colors rounded text-sm font-medium">
                  Update Status
                </button>
                <button className="w-full py-2 bg-semantic-danger/20 border border-semantic-danger/50 text-semantic-danger hover:bg-semantic-danger hover:text-navy-dark transition-colors rounded text-sm font-medium">
                  Escalate Incident
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
