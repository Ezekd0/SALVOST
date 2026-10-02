import { BarChart2, Activity, Database, GitMerge } from 'lucide-react';
import { AreaChart, Area, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, RadarChart, PolarGrid, PolarAngleAxis, PolarRadiusAxis, Radar, Legend } from 'recharts';

const mockPerformanceData = [
  { epoch: 'Epoch 1', accuracy: 0.82, loss: 0.45 },
  { epoch: 'Epoch 2', accuracy: 0.88, loss: 0.32 },
  { epoch: 'Epoch 3', accuracy: 0.91, loss: 0.24 },
  { epoch: 'Epoch 4', accuracy: 0.94, loss: 0.18 },
  { epoch: 'Epoch 5', accuracy: 0.95, loss: 0.15 },
  { epoch: 'Epoch 6', accuracy: 0.96, loss: 0.12 },
  { epoch: 'Epoch 7', accuracy: 0.98, loss: 0.08 },
];

const modelMetrics = [
  { metric: 'Precision', 'Benign': 98, 'Port Scan': 95, 'Brute Force': 97, 'DDoS': 99, 'Malware': 92, fullMark: 100 },
  { metric: 'Recall', 'Benign': 99, 'Port Scan': 93, 'Brute Force': 96, 'DDoS': 98, 'Malware': 89, fullMark: 100 },
  { metric: 'F1 Score', 'Benign': 98.5, 'Port Scan': 94, 'Brute Force': 96.5, 'DDoS': 98.5, 'Malware': 90.5, fullMark: 100 },
];

export default function Analytics() {
  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-end gap-4">
        <div>
          <h1 className="text-2xl font-bold text-text-light tracking-tight flex items-center">
            <BarChart2 className="h-6 w-6 mr-2 text-teal" />
            Analytics & Model Performance
          </h1>
          <p className="text-sm text-text-muted mt-1">Review the inference engine's detection accuracy and training metrics.</p>
        </div>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-4 gap-6">
        <div className="bg-navy border border-navy-dark rounded-xl p-5 shadow-lg flex items-center">
          <div className="p-3 bg-teal bg-opacity-20 rounded-lg mr-4">
            <Activity className="h-6 w-6 text-teal" />
          </div>
          <div>
            <p className="text-sm text-text-muted font-medium">Global Accuracy</p>
            <h3 className="text-2xl font-bold text-text-light">98.2%</h3>
          </div>
        </div>
        
        <div className="bg-navy border border-navy-dark rounded-xl p-5 shadow-lg flex items-center">
          <div className="p-3 bg-semantic-danger bg-opacity-20 rounded-lg mr-4">
            <Database className="h-6 w-6 text-semantic-danger" />
          </div>
          <div>
            <p className="text-sm text-text-muted font-medium">False Positive Rate</p>
            <h3 className="text-2xl font-bold text-text-light">1.4%</h3>
          </div>
        </div>

        <div className="bg-navy border border-navy-dark rounded-xl p-5 shadow-lg flex items-center">
          <div className="p-3 bg-semantic-warning bg-opacity-20 rounded-lg mr-4">
            <GitMerge className="h-6 w-6 text-semantic-warning" />
          </div>
          <div>
            <p className="text-sm text-text-muted font-medium">Model Architecture</p>
            <h3 className="text-lg font-bold text-text-light">Random Forest</h3>
          </div>
        </div>
        
        <div className="bg-navy border border-navy-dark rounded-xl p-5 shadow-lg flex items-center">
          <div className="p-3 bg-semantic-success bg-opacity-20 rounded-lg mr-4">
            <Activity className="h-6 w-6 text-semantic-success" />
          </div>
          <div>
            <p className="text-sm text-text-muted font-medium">Inference Latency</p>
            <h3 className="text-2xl font-bold text-text-light">~45ms</h3>
          </div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-navy border border-navy-dark rounded-xl shadow-lg p-5">
          <h2 className="text-lg font-semibold text-text-light mb-4">Training History (Accuracy vs Loss)</h2>
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={mockPerformanceData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
                <defs>
                  <linearGradient id="colorAccuracy" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#17C3B2" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="#17C3B2" stopOpacity={0}/>
                  </linearGradient>
                  <linearGradient id="colorLoss" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#EF4444" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="#EF4444" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#13293D" vertical={false} />
                <XAxis dataKey="epoch" stroke="#6E8899" fontSize={12} tickLine={false} axisLine={false} />
                <YAxis yAxisId="left" stroke="#17C3B2" fontSize={12} tickLine={false} axisLine={false} domain={[0.6, 1]} />
                <YAxis yAxisId="right" orientation="right" stroke="#EF4444" fontSize={12} tickLine={false} axisLine={false} domain={[0, 0.6]} />
                <Tooltip 
                  contentStyle={{ backgroundColor: '#0B1B2B', borderColor: '#13293D', color: '#E8F1F2' }}
                  itemStyle={{ color: '#E8F1F2' }}
                />
                <Area yAxisId="left" type="monotone" dataKey="accuracy" stroke="#17C3B2" strokeWidth={2} fillOpacity={1} fill="url(#colorAccuracy)" />
                <Area yAxisId="right" type="monotone" dataKey="loss" stroke="#EF4444" strokeWidth={2} fillOpacity={1} fill="url(#colorLoss)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="bg-navy border border-navy-dark rounded-xl shadow-lg p-5">
          <h2 className="text-lg font-semibold text-text-light mb-4">Class-Specific Performance</h2>
          <div className="h-80">
            <ResponsiveContainer width="100%" height="100%">
              <RadarChart cx="50%" cy="50%" outerRadius="80%" data={modelMetrics}>
                <PolarGrid stroke="#13293D" />
                <PolarAngleAxis dataKey="metric" stroke="#6E8899" fontSize={12} />
                <PolarRadiusAxis angle={30} domain={[0, 100]} stroke="#13293D" />
                <Radar name="Benign" dataKey="Benign" stroke="#10B981" fill="#10B981" fillOpacity={0.4} />
                <Radar name="DDoS" dataKey="DDoS" stroke="#EF4444" fill="#EF4444" fillOpacity={0.4} />
                <Radar name="Brute Force" dataKey="Brute Force" stroke="#F59E0B" fill="#F59E0B" fillOpacity={0.4} />
                <Legend wrapperStyle={{ fontSize: '12px', color: '#8AA3B8' }} />
                <Tooltip contentStyle={{ backgroundColor: '#0B1B2B', borderColor: '#13293D', color: '#E8F1F2' }} />
              </RadarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>
    </div>
  );
}
