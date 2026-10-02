import { useEffect, useState } from 'react';
import { Server, Activity, ShieldCheck, AlertTriangle, AlertCircle, StopCircle, RefreshCw } from 'lucide-react';
import { format } from 'date-fns';
import api from '../lib/api';
import { Link } from 'react-router-dom';

export default function ResponseCenter() {
  const [responses, setResponses] = useState<any[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const fetchResponses = async () => {
    setIsLoading(true);
    try {
      const response = await api.get('/incidents/responses/recent');
      setResponses(response.data);
    } catch (error) {
      console.error("Failed to fetch responses", error);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchResponses();
  }, []);

  const getActionIcon = (actionType: string) => {
    switch (actionType) {
      case 'Allow': return <ShieldCheck className="h-5 w-5 text-semantic-success" />;
      case 'Monitor': return <Activity className="h-5 w-5 text-teal" />;
      case 'Contain': return <AlertTriangle className="h-5 w-5 text-semantic-warning" />;
      case 'Isolate': return <StopCircle className="h-5 w-5 text-semantic-danger" />;
      default: return <AlertCircle className="h-5 w-5 text-text-muted" />;
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-end gap-4">
        <div>
          <h1 className="text-2xl font-bold text-text-light tracking-tight flex items-center">
            <Server className="h-6 w-6 mr-2 text-teal" />
            Response Center
          </h1>
          <p className="text-sm text-text-muted mt-1">Audit log of autonomous actions executed by the response engine.</p>
        </div>
        <button 
          onClick={fetchResponses}
          className="flex items-center px-4 py-2 bg-navy-dark border border-navy rounded-md text-sm text-text-light hover:bg-navy transition-colors focus:outline-none focus:ring-2 focus:ring-teal"
        >
          <RefreshCw className={`h-4 w-4 mr-2 ${isLoading ? 'animate-spin' : 'text-text-muted'}`} />
          Refresh
        </button>
      </div>

      <div className="bg-navy border border-navy-dark rounded-xl shadow-lg overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-navy-dark">
            <thead className="bg-[#070F18]">
              <tr>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Timestamp</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Action Type</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Target IP</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Incident Ref</th>
                <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Operator</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-navy-dark bg-navy relative">
              {isLoading && responses.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-6 py-12 text-center text-text-muted">
                    <div className="flex justify-center items-center">
                      <div className="animate-spin h-6 w-6 border-2 border-teal border-t-transparent rounded-full mr-3"></div>
                      Loading audit logs...
                    </div>
                  </td>
                </tr>
              ) : responses.length === 0 ? (
                <tr>
                  <td colSpan={5} className="px-6 py-12 text-center text-text-muted">
                    No autonomous responses recorded yet.
                  </td>
                </tr>
              ) : (
                responses.map((resp) => (
                  <tr key={resp.id} className="hover:bg-navy-dark transition-colors text-sm">
                    <td className="px-6 py-4 whitespace-nowrap text-text-muted font-mono">
                      {format(new Date(resp.timestamp), 'yyyy-MM-dd HH:mm:ss')}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        {getActionIcon(resp.action_type)}
                        <span className="ml-2 font-medium text-text-light">{resp.action_type}</span>
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-text-light font-mono">
                      {resp.target_ip}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <Link to={`/incidents/${resp.incident_id}`} className="text-teal hover:underline font-mono">
                        INC-{resp.incident_id.toString().padStart(5, '0')}
                      </Link>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-text-muted">
                      System (Auto)
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
