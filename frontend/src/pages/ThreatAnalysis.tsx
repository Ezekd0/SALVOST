import React, { useState } from 'react';
import { Play, Crosshair, ShieldAlert, Cpu, Network, CheckCircle } from 'lucide-react';
import api from '../lib/api';

export default function ThreatAnalysis() {
  const [formData, setFormData] = useState({
    source_ip: '192.168.1.100',
    destination_ip: '10.0.0.50',
    destination_port: 80,
    protocol: 'TCP',
    flow_duration: 1.5,
    packet_rate: 1500,
    bytes_rate: 25000,
    is_demo: true,
  });

  const [isLoading, setIsLoading] = useState(false);
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  const presetScenarios = {
    'Benign': {
      source_ip: '192.168.1.100',
      destination_ip: '10.0.0.50',
      destination_port: 443,
      protocol: 'TCP',
      flow_duration: 5.5,
      packet_rate: 50,
      bytes_rate: 5000,
      is_demo: true,
    },
    'DDoS': {
      source_ip: '203.0.113.45',
      destination_ip: '10.0.0.10',
      destination_port: 80,
      protocol: 'TCP',
      flow_duration: 0.1,
      packet_rate: 50000,
      bytes_rate: 1500000,
      is_demo: true,
    },
    'Port Scan': {
      source_ip: '198.51.100.14',
      destination_ip: '10.0.0.25',
      destination_port: 22,
      protocol: 'TCP',
      flow_duration: 0.05,
      packet_rate: 500,
      bytes_rate: 300,
      is_demo: true,
    },
    'Brute Force': {
      source_ip: '172.16.5.99',
      destination_ip: '10.0.0.5',
      destination_port: 22,
      protocol: 'TCP',
      flow_duration: 120.0,
      packet_rate: 200,
      bytes_rate: 15000,
      is_demo: true,
    },
    'Malware Traffic': {
      source_ip: '10.0.0.88',
      destination_ip: '185.199.108.153',
      destination_port: 4444,
      protocol: 'TCP',
      flow_duration: 3600.0,
      packet_rate: 5,
      bytes_rate: 400,
      is_demo: true,
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value, type } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'number' ? parseFloat(value) : value
    }));
  };

  const loadScenario = (name: keyof typeof presetScenarios) => {
    setFormData(presetScenarios[name]);
  };

  const handleSimulate = async () => {
    setIsLoading(true);
    setError(null);
    setResult(null);
    try {
      const response = await api.post('/analysis/run', formData);
      setResult(response.data);
    } catch (err: any) {
      setError(err.response?.data?.detail || 'Simulation failed');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-text-light tracking-tight">Threat Analysis (Live Demo)</h1>
        <p className="text-sm text-text-muted mt-1">Inject network events into the ML inference engine to evaluate detection capabilities.</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Simulation Form */}
        <div className="bg-navy border border-navy-dark rounded-xl shadow-lg flex flex-col h-full">
          <div className="p-5 border-b border-navy-dark flex justify-between items-center bg-[#0B1B2B]/50 rounded-t-xl">
            <h2 className="text-lg font-semibold text-text-light flex items-center">
              <Network className="h-5 w-5 mr-2 text-teal" />
              Event Injector
            </h2>
          </div>
          
          <div className="p-5 flex-1">
            <div className="mb-6 flex flex-wrap gap-2">
              <span className="text-sm text-text-muted mr-2 flex items-center">Presets:</span>
              {Object.keys(presetScenarios).map(key => (
                <button
                  key={key}
                  onClick={() => loadScenario(key as keyof typeof presetScenarios)}
                  className="px-2 py-1 text-xs font-medium rounded bg-navy-dark border border-navy text-text-light hover:bg-teal hover:bg-opacity-20 hover:border-teal transition-colors"
                >
                  {key}
                </button>
              ))}
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-medium text-text-muted mb-1">Source IP</label>
                <input type="text" name="source_ip" value={formData.source_ip} onChange={handleChange} className="w-full bg-navy-dark border border-navy rounded p-2 text-sm text-text-light focus:outline-none focus:border-teal" />
              </div>
              <div>
                <label className="block text-xs font-medium text-text-muted mb-1">Dest IP</label>
                <input type="text" name="destination_ip" value={formData.destination_ip} onChange={handleChange} className="w-full bg-navy-dark border border-navy rounded p-2 text-sm text-text-light focus:outline-none focus:border-teal" />
              </div>
              <div>
                <label className="block text-xs font-medium text-text-muted mb-1">Dest Port</label>
                <input type="number" name="destination_port" value={formData.destination_port} onChange={handleChange} className="w-full bg-navy-dark border border-navy rounded p-2 text-sm text-text-light focus:outline-none focus:border-teal" />
              </div>
              <div>
                <label className="block text-xs font-medium text-text-muted mb-1">Protocol</label>
                <select name="protocol" value={formData.protocol} onChange={handleChange} className="w-full bg-navy-dark border border-navy rounded p-2 text-sm text-text-light focus:outline-none focus:border-teal">
                  <option value="TCP">TCP</option>
                  <option value="UDP">UDP</option>
                  <option value="ICMP">ICMP</option>
                </select>
              </div>
              <div>
                <label className="block text-xs font-medium text-text-muted mb-1">Flow Duration (s)</label>
                <input type="number" step="0.01" name="flow_duration" value={formData.flow_duration} onChange={handleChange} className="w-full bg-navy-dark border border-navy rounded p-2 text-sm text-text-light focus:outline-none focus:border-teal" />
              </div>
              <div>
                <label className="block text-xs font-medium text-text-muted mb-1">Packet Rate (p/s)</label>
                <input type="number" name="packet_rate" value={formData.packet_rate} onChange={handleChange} className="w-full bg-navy-dark border border-navy rounded p-2 text-sm text-text-light focus:outline-none focus:border-teal" />
              </div>
              <div>
                <label className="block text-xs font-medium text-text-muted mb-1">Bytes Rate (b/s)</label>
                <input type="number" name="bytes_rate" value={formData.bytes_rate} onChange={handleChange} className="w-full bg-navy-dark border border-navy rounded p-2 text-sm text-text-light focus:outline-none focus:border-teal" />
              </div>
            </div>
          </div>
          
          <div className="p-5 border-t border-navy-dark bg-[#0B1B2B]/30 rounded-b-xl">
            <button
              onClick={handleSimulate}
              disabled={isLoading}
              className="w-full flex items-center justify-center px-4 py-2 bg-teal hover:bg-teal-dark text-navy-dark font-semibold rounded transition-colors disabled:opacity-50"
            >
              {isLoading ? (
                <span className="flex items-center"><div className="animate-spin h-4 w-4 border-2 border-navy-dark border-t-transparent rounded-full mr-2"></div> Processing...</span>
              ) : (
                <span className="flex items-center"><Play className="h-4 w-4 mr-2" /> Inject to ML Engine</span>
              )}
            </button>
            {error && <p className="mt-2 text-sm text-semantic-danger text-center">{error}</p>}
          </div>
        </div>

        {/* Results Panel */}
        <div className="bg-navy border border-navy-dark rounded-xl shadow-lg flex flex-col h-full overflow-hidden">
          <div className="p-5 border-b border-navy-dark flex justify-between items-center bg-[#0B1B2B]/50">
            <h2 className="text-lg font-semibold text-text-light flex items-center">
              <Cpu className="h-5 w-5 mr-2 text-teal" />
              Inference Result
            </h2>
          </div>
          
          <div className="p-5 flex-1 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iMzAiIGN5PSIzMCIgcj0iMSIgZmlsbD0icmdiYSgyMywgMTk1LCAxNzgsIDAuMikiLz48L3N2Zz4=')]">
            {!result && !isLoading && (
              <div className="h-full flex flex-col items-center justify-center text-text-muted">
                <Crosshair className="h-12 w-12 mb-4 opacity-20" />
                <p>Awaiting event injection...</p>
              </div>
            )}
            
            {isLoading && (
              <div className="h-full flex flex-col items-center justify-center text-teal">
                <div className="animate-spin h-12 w-12 border-4 border-teal border-t-transparent rounded-full mb-4"></div>
                <p className="animate-pulse font-mono">ANALYZING SIGNATURES...</p>
              </div>
            )}

            {result && !isLoading && (
              <div className="space-y-6 animate-in fade-in slide-in-from-bottom-4 duration-500">
                <div className={`p-4 rounded-lg border ${
                  result.analysis.classification === 'Benign' 
                    ? 'bg-semantic-success/10 border-semantic-success/30 text-semantic-success' 
                    : 'bg-semantic-danger/10 border-semantic-danger/30 text-semantic-danger'
                }`}>
                  <div className="flex items-center justify-between">
                    <div className="flex items-center">
                      {result.analysis.classification === 'Benign' ? <CheckCircle className="h-8 w-8 mr-3" /> : <ShieldAlert className="h-8 w-8 mr-3 animate-pulse" />}
                      <div>
                        <p className="text-xs font-bold uppercase tracking-widest opacity-80">Classification</p>
                        <h3 className="text-2xl font-black">{result.analysis.classification}</h3>
                      </div>
                    </div>
                    <div className="text-right">
                      <p className="text-xs font-bold uppercase tracking-widest opacity-80">Confidence</p>
                      <h3 className="text-2xl font-black">{(result.analysis.confidence * 100).toFixed(1)}%</h3>
                    </div>
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div className="bg-navy-dark p-3 rounded border border-navy">
                    <p className="text-xs text-text-muted uppercase">Threat Score</p>
                    <p className="text-lg font-mono text-text-light mt-1">{(result.analysis.threat_score * 100).toFixed(0)} / 100</p>
                  </div>
                  <div className="bg-navy-dark p-3 rounded border border-navy">
                    <p className="text-xs text-text-muted uppercase">Event ID</p>
                    <p className="text-lg font-mono text-text-light mt-1">EVT-{result.id.toString().padStart(5, '0')}</p>
                  </div>
                </div>

                {result.analysis.classification !== 'Benign' && (
                  <div>
                    <h4 className="text-sm font-semibold text-text-light mb-2 border-b border-navy-dark pb-1">Automated Response Initiated</h4>
                    <div className="bg-navy-dark p-3 rounded border border-navy text-sm font-mono text-text-muted">
                      {'> '} System contained IP {result.source_ip} based on severity policy.
                    </div>
                  </div>
                )}
                
                <div>
                   <h4 className="text-sm font-semibold text-text-light mb-2 border-b border-navy-dark pb-1">SHAP Feature Importance</h4>
                   <div className="bg-navy-dark p-3 rounded border border-navy text-xs font-mono text-text-muted overflow-auto max-h-32">
                     <pre>{JSON.stringify(JSON.parse(result.analysis.explanation_json), null, 2)}</pre>
                   </div>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
