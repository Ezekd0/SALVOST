import { FileText, Download, Calendar, Filter } from 'lucide-react';

const mockReports = [
  { id: 'REP-001', name: 'Weekly SOC Summary', date: '2026-09-28', status: 'Generated', size: '2.4 MB' },
  { id: 'REP-002', name: 'Incident Post-Mortem (EVT-00042)', date: '2026-09-27', status: 'Generated', size: '1.1 MB' },
  { id: 'REP-003', name: 'Monthly Compliance Audit (ISO 27001)', date: '2026-09-01', status: 'Generated', size: '5.8 MB' },
  { id: 'REP-004', name: 'Threat Intelligence Feed Export', date: '2026-09-30', status: 'Processing', size: '--' },
];

export default function Reports() {
  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-end gap-4">
        <div>
          <h1 className="text-2xl font-bold text-text-light tracking-tight flex items-center">
            <FileText className="h-6 w-6 mr-2 text-teal" />
            Automated Reporting
          </h1>
          <p className="text-sm text-text-muted mt-1">Report preview. Generation and downloads are not connected to a report service.</p>
        </div>
        <button disabled title="Report generation is not connected to a service" className="px-4 py-2 bg-teal hover:bg-teal-light text-navy-dark font-medium rounded-md transition-colors flex items-center">
          <FileText className="h-4 w-4 mr-2" />
          Generate New Report
        </button>
      </div>

      <div className="bg-navy border border-navy-dark rounded-xl shadow-lg overflow-hidden">
        <div className="p-4 border-b border-navy-dark flex justify-between items-center bg-[#070F18]">
          <div className="flex space-x-4">
            <div className="flex items-center text-sm text-text-muted">
              <Calendar className="h-4 w-4 mr-2" />
              Last 30 Days
            </div>
            <div className="flex items-center text-sm text-text-muted">
              <Filter className="h-4 w-4 mr-2" />
              All Report Types
            </div>
          </div>
        </div>
        
        <div className="overflow-x-auto">
        <table className="min-w-full divide-y divide-navy-dark">
          <thead className="bg-[#070F18]">
            <tr>
              <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Report ID</th>
              <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Name</th>
              <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Date</th>
              <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Status</th>
              <th scope="col" className="px-6 py-4 text-left text-xs font-semibold text-text-muted uppercase tracking-wider">Size</th>
              <th scope="col" className="px-6 py-4 text-right text-xs font-semibold text-text-muted uppercase tracking-wider">Action</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-navy-dark bg-navy">
            {mockReports.map((report) => (
              <tr key={report.id} className="hover:bg-navy-dark transition-colors">
                <td className="px-6 py-4 whitespace-nowrap text-sm font-mono text-teal">
                  {report.id}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-text-light font-medium">
                  {report.name}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-text-muted">
                  {report.date}
                </td>
                <td className="px-6 py-4 whitespace-nowrap">
                  <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                    report.status === 'Generated' ? 'bg-semantic-success/20 text-semantic-success' : 'bg-semantic-warning/20 text-semantic-warning animate-pulse'
                  }`}>
                    {report.status}
                  </span>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-text-muted font-mono">
                  {report.size}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                  <button 
                    disabled aria-label={`Download ${report.name} (unavailable)`} title="Preview report: no downloadable file is available"
                    className="inline-flex items-center text-text-muted hover:text-teal disabled:opacity-30 disabled:hover:text-text-muted transition-colors"
                  >
                    <Download className="h-5 w-5" />
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        </div>
      </div>
    </div>
  );
}
