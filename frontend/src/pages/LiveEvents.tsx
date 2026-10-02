import { useEffect, useState } from 'react';
import { Activity, Shield, Filter, Search } from 'lucide-react';
import { format } from 'date-fns';
import api from '../lib/api';

interface SecurityEvent {
  id: number;
  timestamp: string;
  source_ip: string;
  destination_ip: string;
  destination_port: number;
  protocol: string;
  flow_duration: number;
  packet_rate: number;
  bytes_rate: number;
  analysis?: {
    classification: string;
    threat_score: number;
  }
}

export default function LiveEvents() {
  const [events, setEvents] = useState<SecurityEvent[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [query, setQuery] = useState('');
  const [filter, setFilter] = useState('');


  const fetchEvents = async () => {
    try {
      const response = await api.get('/events?limit=50');
      setEvents(response.data);
    } catch (error) {
      console.error("Failed to fetch events", error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchEvents();
    
    // Simulating live updates
    const interval = setInterval(fetchEvents, 5000);
    return () => clearInterval(interval);
  }, []);

  const filtered = events.filter(item => [item.source_ip, item.destination_ip, item.destination_port].join(' ').toLowerCase().includes(query.toLowerCase()) && (!filter || item.analysis?.classification === filter));
  const visible = filtered;

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-end gap-4">
        <div>
          <h1 className="text-2xl font-bold text-text-light tracking-tight flex items-center">
            <Activity className="h-6 w-6 mr-2 text-teal" />
            Live Network Events
          </h1>
          <p className="text-sm text-text-muted mt-1">Real-time stream of network flow data ingested into the ML pipeline.</p>
        </div>
        <div className="flex flex-wrap gap-3 w-full sm:w-auto">
          <div className="relative flex-1 min-w-0">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Search className="h-4 w-4 text-text-muted" />
            </div>
            <input
              type="search" aria-label="Search events" value={query} onChange={e => { setQuery(e.target.value); }}
              className="block w-full sm:w-64 pl-10 bg-navy-dark border border-navy rounded-md py-2 text-sm text-text-light focus:outline-none focus:ring-1 focus:ring-teal focus:border-teal placeholder-text-muted"
              placeholder="Search IP or Port..."
            />
          </div>
          <details className="relative self-start">
            <summary className="px-4 py-2 bg-navy-dark border border-navy rounded-md text-sm text-text-light"><Filter className="inline-block h-4 w-4 mr-2 text-text-muted" />Filters{filter ? `: ${filter}` : ''}</summary>
            <div className="absolute right-0 top-full mt-2 z-20 w-56 p-3 bg-navy-dark border border-teal rounded-md shadow-lg">
              <label className="block text-xs text-text-muted mb-2" htmlFor="events-filter">Classification</label>
              <select id="events-filter" value={filter} onChange={e => { setFilter(e.target.value); }} className="w-full bg-navy text-text-light border border-navy rounded p-2 text-sm">
                <option value="">All classification values</option><option value="Benign">Benign</option><option value="Port Scan">Port Scan</option><option value="Brute Force">Brute Force</option><option value="DDoS">DDoS</option><option value="Malware Traffic">Malware Traffic</option>
              </select>
            </div>
          </details>
        </div>
      </div>

      <div className="bg-navy border border-navy-dark rounded-xl shadow-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-navy-dark">
            <thead className="bg-[#070F18]">
              <tr>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Timestamp</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Source IP</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Dest IP : Port</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Protocol</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Pkt/s (B/s)</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Classification</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-navy-dark bg-navy relative">
              {isLoading && visible.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-text-muted">
                    <div className="flex justify-center items-center">
                      <div className="animate-spin h-6 w-6 border-2 border-teal border-t-transparent rounded-full mr-3"></div>
                      Listening for events...
                    </div>
                  </td>
                </tr>
              ) : visible.length === 0 ? (
                <tr>
                  <td colSpan={6} className="px-6 py-12 text-center text-text-muted">
                    {events.length ? 'No events match the current search and filter.' : 'No network events detected.'}
                  </td>
                </tr>
              ) : (
                visible.map((evt) => (
                  <tr key={evt.id} className="hover:bg-navy-dark transition-colors font-mono text-sm">
                    <td className="px-6 py-4 whitespace-nowrap text-text-muted">
                      {format(new Date(evt.timestamp), 'HH:mm:ss.SSS')}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-text-light">
                      {evt.source_ip}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-text-light">
                      {evt.destination_ip} <span className="text-text-muted">:{evt.destination_port}</span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-text-muted">
                      {evt.protocol}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-text-muted text-xs">
                      {evt.packet_rate} p/s<br/>({(evt.bytes_rate / 1000).toFixed(1)} kB/s)
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {evt.analysis ? (
                        <span className={`inline-flex items-center px-2 py-0.5 rounded text-xs font-medium ${
                          evt.analysis.classification === 'Benign' 
                            ? 'bg-semantic-success/10 text-semantic-success' 
                            : 'bg-semantic-danger/10 text-semantic-danger border border-semantic-danger/50'
                        }`}>
                          {evt.analysis.classification !== 'Benign' && <Shield className="w-3 h-3 mr-1" />}
                          {evt.analysis.classification}
                        </span>
                      ) : (
                        <span className="text-text-muted italic text-xs">Pending...</span>
                      )}
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
