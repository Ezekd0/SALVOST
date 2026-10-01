import { useState } from 'react';
import { User, Lock, Bell, Shield, LogOut } from 'lucide-react';
import { useNavigate } from 'react-router-dom';

export default function ProfileSettings() {
  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('profile');

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <div className="space-y-6 animate-in fade-in duration-500">
      <div className="flex justify-between items-end">
        <div>
          <h1 className="text-2xl font-bold text-text-light tracking-tight flex items-center">
            <User className="h-6 w-6 mr-2 text-teal" />
            User Settings
          </h1>
          <p className="text-sm text-text-muted mt-1">Manage your profile, security preferences, and notifications.</p>
        </div>
      </div>

      <div className="bg-navy border border-navy-dark rounded-xl shadow-lg flex flex-col md:flex-row overflow-hidden">
        {/* Sidebar */}
        <div className="w-full md:w-64 bg-[#070F18] border-r border-navy-dark p-4 flex flex-col space-y-2">
          <button 
            onClick={() => setActiveTab('profile')}
            className={`w-full flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors ${activeTab === 'profile' ? 'bg-navy-dark text-teal' : 'text-text-muted hover:bg-navy hover:text-text-light'}`}
          >
            <User className="h-5 w-5 mr-3" /> Profile
          </button>
          <button 
            onClick={() => setActiveTab('security')}
            className={`w-full flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors ${activeTab === 'security' ? 'bg-navy-dark text-teal' : 'text-text-muted hover:bg-navy hover:text-text-light'}`}
          >
            <Lock className="h-5 w-5 mr-3" /> Security
          </button>
          <button 
            onClick={() => setActiveTab('notifications')}
            className={`w-full flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors ${activeTab === 'notifications' ? 'bg-navy-dark text-teal' : 'text-text-muted hover:bg-navy hover:text-text-light'}`}
          >
            <Bell className="h-5 w-5 mr-3" /> Notifications
          </button>
          <button 
            onClick={() => setActiveTab('api')}
            className={`w-full flex items-center px-4 py-3 text-sm font-medium rounded-lg transition-colors ${activeTab === 'api' ? 'bg-navy-dark text-teal' : 'text-text-muted hover:bg-navy hover:text-text-light'}`}
          >
            <Shield className="h-5 w-5 mr-3" /> API Keys
          </button>
          
          <div className="flex-grow"></div>
          <div className="pt-4 border-t border-navy-dark mt-4">
             <button 
              onClick={handleLogout}
              className="w-full flex items-center justify-center px-4 py-2 text-sm font-medium rounded-lg border border-semantic-danger text-semantic-danger hover:bg-semantic-danger hover:bg-opacity-10 transition-colors"
            >
              <LogOut className="h-4 w-4 mr-2" /> Sign Out
            </button>
          </div>
        </div>

        {/* Content Area */}
        <div className="flex-1 p-6 md:p-8">
          {activeTab === 'profile' && (
            <div className="max-w-2xl animate-in fade-in">
              <h2 className="text-xl font-bold text-text-light mb-6">Profile Information</h2>
              <div className="space-y-6">
                <div>
                  <label className="block text-sm font-medium text-text-muted mb-2">Full Name</label>
                  <input type="text" defaultValue="Demo User" className="w-full bg-[#070F18] border border-navy-dark rounded-md px-4 py-2 text-text-light focus:outline-none focus:border-teal transition-colors" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-text-muted mb-2">Email Address</label>
                  <input type="email" defaultValue="admin@soc-platform.local" className="w-full bg-[#070F18] border border-navy-dark rounded-md px-4 py-2 text-text-light focus:outline-none focus:border-teal transition-colors" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-text-muted mb-2">Role</label>
                  <input type="text" disabled defaultValue="Security Analyst" className="w-full bg-[#070F18] border border-navy-dark rounded-md px-4 py-2 text-text-muted opacity-50 cursor-not-allowed" />
                </div>
                <button className="px-6 py-2 bg-teal hover:bg-teal-light text-navy-dark font-medium rounded-md transition-colors">
                  Save Changes
                </button>
              </div>
            </div>
          )}

          {activeTab === 'security' && (
            <div className="max-w-2xl animate-in fade-in">
              <h2 className="text-xl font-bold text-text-light mb-6">Security Settings</h2>
              <div className="space-y-6">
                 <div>
                  <label className="block text-sm font-medium text-text-muted mb-2">Current Password</label>
                  <input type="password" placeholder="••••••••" className="w-full bg-[#070F18] border border-navy-dark rounded-md px-4 py-2 text-text-light focus:outline-none focus:border-teal transition-colors" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-text-muted mb-2">New Password</label>
                  <input type="password" placeholder="New Password" className="w-full bg-[#070F18] border border-navy-dark rounded-md px-4 py-2 text-text-light focus:outline-none focus:border-teal transition-colors" />
                </div>
                <div>
                  <label className="block text-sm font-medium text-text-muted mb-2">Confirm New Password</label>
                  <input type="password" placeholder="Confirm Password" className="w-full bg-[#070F18] border border-navy-dark rounded-md px-4 py-2 text-text-light focus:outline-none focus:border-teal transition-colors" />
                </div>
                <div className="flex items-center space-x-3 pt-4 border-t border-navy-dark">
                  <input type="checkbox" id="2fa" className="h-4 w-4 bg-navy border-navy-dark text-teal rounded focus:ring-teal focus:ring-offset-navy" />
                  <label htmlFor="2fa" className="text-text-light font-medium">Enable Two-Factor Authentication (2FA)</label>
                </div>
                <button className="px-6 py-2 bg-teal hover:bg-teal-light text-navy-dark font-medium rounded-md transition-colors mt-4">
                  Update Security Settings
                </button>
              </div>
            </div>
          )}

          {activeTab === 'notifications' && (
            <div className="max-w-2xl animate-in fade-in">
              <h2 className="text-xl font-bold text-text-light mb-6">Notification Preferences</h2>
              <div className="space-y-4">
                {[
                  { title: 'High Severity Threats', desc: 'Email alerts for Critical and High incidents' },
                  { title: 'Daily Digest', desc: 'Summary of the day\'s automated responses' },
                  { title: 'System Alerts', desc: 'Notifications about platform health and latency' },
                  { title: 'New Reports', desc: 'When scheduled reports are ready for download' }
                ].map((item, i) => (
                  <div key={i} className="flex items-start justify-between p-4 bg-[#070F18] border border-navy-dark rounded-lg">
                    <div>
                      <h3 className="font-medium text-text-light">{item.title}</h3>
                      <p className="text-sm text-text-muted mt-1">{item.desc}</p>
                    </div>
                    <label className="relative inline-flex items-center cursor-pointer">
                      <input type="checkbox" defaultChecked className="sr-only peer" />
                      <div className="w-11 h-6 bg-navy border border-navy-dark rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-0.5 after:left-[2px] after:bg-text-muted after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-teal peer-checked:after:bg-white"></div>
                    </label>
                  </div>
                ))}
              </div>
            </div>
          )}

          {activeTab === 'api' && (
            <div className="max-w-2xl animate-in fade-in">
               <h2 className="text-xl font-bold text-text-light mb-6">API Access</h2>
               <p className="text-text-muted mb-6">Manage your API keys for programmatic access to the threat detection engine.</p>
               
               <div className="bg-[#070F18] border border-navy-dark rounded-lg p-5 mb-6">
                 <div className="flex justify-between items-center mb-4">
                   <h3 className="font-medium text-text-light">Default Key</h3>
                   <span className="text-xs font-mono text-text-muted">Created: Oct 01, 2026</span>
                 </div>
                 <div className="flex items-center space-x-2">
                    <code className="flex-1 bg-navy px-3 py-2 rounded border border-navy-dark text-teal font-mono text-sm overflow-hidden text-ellipsis">
                      ai_ctdrs_sk_••••••••••••••••••••••••
                    </code>
                    <button className="p-2 border border-navy-dark rounded bg-navy hover:bg-navy-dark text-text-light transition-colors">
                      Copy
                    </button>
                 </div>
               </div>

               <button className="px-6 py-2 border border-teal text-teal hover:bg-teal hover:bg-opacity-10 font-medium rounded-md transition-colors">
                  Generate New API Key
                </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
