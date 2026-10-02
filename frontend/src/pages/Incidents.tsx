import { useEffect, useState } from 'react';
import { ShieldAlert, Search, Filter, AlertTriangle, ShieldCheck, Activity, Eye, ChevronRight } from 'lucide-react';
import { format } from 'date-fns';
import api from '../lib/api';
import { Link } from 'react-router-dom';

interface Incident {
  id: number;
  severity: string;
  status: string;
  created_at: string;
  analysis: {
    attack_type: string;
    classification: string;
    threat_score: number;
    event: {
      source_ip: string;
      destination_ip: string;
    }
  }
}

export default function Incidents() {
  const [incidents, setIncidents] = useState<Incident[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [query, setQuery] = useState('');
  const [filter, setFilter] = useState('');
  const [page, setPage] = useState(0);
  const pageSize = 10;


  useEffect(() => {
    const fetchIncidents = async () => {
      try {
        const response = await api.get('/incidents');
        setIncidents(response.data);
      } catch (error) {
        console.error("Failed to fetch incidents", error);
      } finally {
        setIsLoading(false);
      }
    };
    fetchIncidents();
  }, []);

  const getSeverityBadge = (severity: string) => {
    switch (severity) {
      case 'Critical': return <span className="px-2 py-1 rounded text-xs font-bold bg-semantic-critical/20 text-semantic-critical border border-semantic-critical/50 uppercase tracking-wide">Critical</span>;
      case 'High': return <span className="px-2 py-1 rounded text-xs font-bold bg-semantic-danger/20 text-semantic-danger border border-semantic-danger/50 uppercase tracking-wide">High</span>;
      case 'Medium': return <span className="px-2 py-1 rounded text-xs font-bold bg-semantic-warning/20 text-semantic-warning border border-semantic-warning/50 uppercase tracking-wide">Medium</span>;
      default: return <span className="px-2 py-1 rounded text-xs font-bold bg-teal/20 text-teal border border-teal/50 uppercase tracking-wide">Low</span>;
    }
  };

  const getStatusIcon = (status: string) => {
    switch (status) {
      case 'Contained': return <ShieldCheck className="h-4 w-4 text-semantic-success" />;
      case 'Open': return <AlertTriangle className="h-4 w-4 text-semantic-warning" />;
      case 'Investigating': return <Activity className="h-4 w-4 text-teal" />;
      default: return null;
    }
  };

  const filtered = incidents.filter(item => [item.id, `INC-${item.id.toString().padStart(5, '0')}`, item.analysis.event.source_ip, item.analysis.attack_type].join(' ').toLowerCase().includes(query.toLowerCase()) && (!filter || item.severity === filter));
  const visible = filtered.slice(page * pageSize, (page + 1) * pageSize);

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-end gap-4">
        <div>
          <h1 className="text-2xl font-bold text-text-light tracking-tight flex items-center">
            <ShieldAlert className="h-6 w-6 mr-2 text-teal" />
            Incident Response Registry
          </h1>
          <p className="text-sm text-text-muted mt-1">Review and manage detected threats and automated containment actions.</p>
        </div>
        <div className="flex flex-wrap gap-3 w-full sm:w-auto">
          <div className="relative flex-1 min-w-0">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
              <Search className="h-4 w-4 text-text-muted" />
            </div>
            <input
              type="search" aria-label="Search incidents" value={query} onChange={e => { setQuery(e.target.value); setPage(0); }}
              className="block w-full sm:w-64 pl-10 bg-navy-dark border border-navy rounded-md py-2 text-sm text-text-light focus:outline-none focus:ring-1 focus:ring-teal focus:border-teal placeholder-text-muted"
              placeholder="Search by IP, ID, or Type..."
            />
          </div>
          <details className="relative self-start">
            <summary className="px-4 py-2 bg-navy-dark border border-navy rounded-md text-sm text-text-light"><Filter className="inline-block h-4 w-4 mr-2 text-text-muted" />Filters{filter ? `: ${filter}` : ''}</summary>
            <div className="absolute right-0 top-full mt-2 z-20 w-56 p-3 bg-navy-dark border border-teal rounded-md shadow-lg">
              <label className="block text-xs text-text-muted mb-2" htmlFor="incidents-filter">Severity</label>
              <select id="incidents-filter" value={filter} onChange={e => { setFilter(e.target.value); setPage(0); }} className="w-full bg-navy text-text-light border border-navy rounded p-2 text-sm">
                <option value="">All severity values</option><option value="Critical">Critical</option><option value="High">High</option><option value="Medium">Medium</option><option value="Low">Low</option>
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
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Incident ID</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Timestamp</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Severity</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Classification</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Source IP</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Status</th>
                <th scope="col" className="px-6 py-4 text-right text-xs font-semibold text-text-muted uppercase tracking-wider">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-navy-dark bg-navy">
              {isLoading ? (
                <tr>
                  <td colSpan={7} className="px-6 py-12 text-center text-text-muted">
                    <div className="flex justify-center items-center">
                      <div className="animate-spin h-6 w-6 border-2 border-teal border-t-transparent rounded-full mr-3"></div>
                      Loading incidents...
                    </div>
                  </td>
                </tr>
              ) : visible.length === 0 ? (
                <tr>
                  <td colSpan={7} className="px-6 py-12 text-center text-text-muted">
                    {incidents.length ? 'No incidents match the current search and filter.' : 'No incidents detected in the current timeframe.'}
                  </td>
                </tr>
              ) : (
                visible.map((incident) => (
                  <tr key={incident.id} className="hover:bg-navy-dark transition-colors group">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-mono text-teal">
                      INC-{incident.id.toString().padStart(5, '0')}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-text-light">
                      {format(new Date(incident.created_at), 'yyyy-MM-dd HH:mm:ss')}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      {getSeverityBadge(incident.severity)}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-text-light font-medium">
                      {incident.analysis.attack_type}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-text-muted font-mono">
                      {incident.analysis.event.source_ip}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center text-sm text-text-light">
                        {getStatusIcon(incident.status)}
                        <span className="ml-2">{incident.status}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      <Link to={`/incidents/${incident.id}`} className="inline-flex items-center text-teal hover:text-teal-dark">
                        <Eye className="h-4 w-4 mr-1" />
                        Inspect
                        <ChevronRight className="h-4 w-4 ml-1 opacity-0 group-hover:opacity-100 transition-opacity" />
                      </Link>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
        
        <div className="bg-[#070F18] px-6 py-3 border-t border-navy-dark flex flex-wrap gap-3 items-center justify-between">
          <div className="text-sm text-text-muted">
            Showing <span className="font-medium text-text-light">{visible.length}</span> of {filtered.length} matching incidents
          </div>
          <div className="flex space-x-2">
            <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0} className="px-3 py-1 bg-navy border border-navy-dark text-text-muted rounded text-sm disabled:opacity-50 hover:bg-navy-dark transition-colors">Prev</button>
            <button onClick={() => setPage(p => p + 1)} disabled={(page + 1) * pageSize >= filtered.length} className="px-3 py-1 bg-navy border border-navy-dark text-text-muted rounded text-sm disabled:opacity-50 hover:bg-navy-dark transition-colors">Next</button>
          </div>
        </div>
      </div>
    </div>
  );
}
