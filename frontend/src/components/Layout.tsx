import { useState } from 'react';
import { Outlet, NavLink, useNavigate, useLocation } from 'react-router-dom';
import { 
  LayoutDashboard, 
  ShieldAlert, 
  Activity, 
  FileText, 
  BarChart2, 
  Crosshair, 
  Settings, 
  LogOut,
  Menu,
  X,
  Bell,
  
  User as UserIcon,
  Server
} from 'lucide-react';
import { useAuth } from '../lib/AuthContext';

const navItems = [
  { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard },
  { name: 'Live Events', path: '/events', icon: Activity },
  { name: 'Threat Analysis', path: '/analysis', icon: Crosshair },
  { name: 'Incidents', path: '/incidents', icon: ShieldAlert },
  { name: 'Response Center', path: '/response', icon: Server },
  { name: 'Analytics', path: '/analytics', icon: BarChart2 },
  { name: 'Reports', path: '/reports', icon: FileText },
];

const bottomNavItems = [
  { name: 'System Status', path: '/status', icon: Activity },
  { name: 'Settings', path: '/settings', icon: Settings },
];

export default function Layout() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const NavItem = ({ item }: { item: any }) => {
    const isActive = location.pathname === item.path || (item.path !== '/' && location.pathname.startsWith(item.path));
    return (
      <NavLink
        to={item.path}
        onClick={() => setIsMobileMenuOpen(false)}
        className={`group flex items-center px-3 py-2 text-sm font-medium rounded-md transition-colors ${
          isActive
            ? 'bg-navy-dark text-teal border-l-2 border-teal'
            : 'text-text-muted hover:bg-navy hover:text-text-light border-l-2 border-transparent'
        }`}
      >
        <item.icon
          className={`flex-shrink-0 -ml-1 mr-3 h-5 w-5 ${
            isActive ? 'text-teal' : 'text-text-muted group-hover:text-text-light'
          }`}
          aria-hidden="true"
        />
        <span className="truncate">{item.name}</span>
        {isActive && (
          <div className="ml-auto w-1.5 h-1.5 rounded-full bg-teal shadow-[0_0_8px_rgba(23,195,178,1)]"></div>
        )}
      </NavLink>
    );
  };

  return (
    <div className="min-h-screen bg-navy flex overflow-hidden">
      {/* Mobile sidebar */}
      <div className={`md:hidden ${isMobileMenuOpen ? 'fixed inset-0 flex z-40' : 'hidden'}`}>
        <div className="fixed inset-0 bg-navy-dark bg-opacity-75 transition-opacity" onClick={() => setIsMobileMenuOpen(false)}></div>
        <div className="relative flex-1 flex flex-col max-w-xs w-full pt-5 pb-4 bg-navy-dark border-r border-navy">
          <div className="absolute top-0 right-0 -mr-12 pt-2">
            <button
              className="ml-1 flex items-center justify-center h-10 w-10 rounded-full focus:outline-none focus:ring-2 focus:ring-inset focus:ring-teal"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              <X className="h-6 w-6 text-text-light" />
            </button>
          </div>
          <div className="flex-shrink-0 flex items-center px-4">
            <ShieldAlert className="h-8 w-8 text-teal" />
            <span className="ml-2 text-xl font-bold text-text-light tracking-wider">AI-CTDRS</span>
          </div>
          <div className="mt-5 flex-1 h-0 overflow-y-auto">
            <nav className="px-2 space-y-1">
              {navItems.map((item) => (
                <NavItem key={item.name} item={item} />
              ))}
            </nav>
          </div>
        </div>
      </div>

      {/* Static sidebar for desktop */}
      <div className="hidden md:flex md:flex-shrink-0 border-r border-navy-dark bg-[#0A1520]">
        <div className="flex flex-col w-64">
          <div className="flex items-center h-16 flex-shrink-0 px-4 border-b border-navy-dark bg-[#070F18]">
            <ShieldAlert className="h-8 w-8 text-teal drop-shadow-[0_0_8px_rgba(23,195,178,0.5)]" />
            <span className="ml-3 text-xl font-bold text-text-light tracking-widest">AI-CTDRS</span>
          </div>
          <div className="flex-1 flex flex-col overflow-y-auto pt-5 pb-4">
            <nav className="flex-1 px-2 space-y-1">
              <div className="px-3 py-2 text-xs font-semibold text-text-muted uppercase tracking-wider mb-2">
                Core Operations
              </div>
              {navItems.map((item) => (
                <NavItem key={item.name} item={item} />
              ))}
              
              <div className="mt-8 px-3 py-2 text-xs font-semibold text-text-muted uppercase tracking-wider mb-2">
                System
              </div>
              {bottomNavItems.map((item) => (
                <NavItem key={item.name} item={item} />
              ))}
            </nav>
          </div>
          <div className="flex-shrink-0 flex border-t border-navy-dark p-4 bg-[#070F18]">
            <div className="flex items-center w-full">
              <div className="h-9 w-9 rounded-full bg-navy border border-teal flex items-center justify-center">
                <UserIcon className="h-5 w-5 text-teal" />
              </div>
              <div className="ml-3">
                <p className="text-sm font-medium text-text-light">{user?.username || 'Operator'}</p>
                <p className="text-xs font-medium text-teal">{user?.role || 'Analyst'}</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Main content */}
      <div className="flex flex-col w-0 flex-1 overflow-hidden">
        <div className="relative z-10 flex-shrink-0 flex h-16 bg-[#070F18] border-b border-navy-dark shadow">
          <button
            className="px-4 border-r border-navy-dark text-text-muted focus:outline-none focus:ring-2 focus:ring-inset focus:ring-teal md:hidden"
            onClick={() => setIsMobileMenuOpen(true)}
          >
            <Menu className="h-6 w-6" />
          </button>
          
          <div className="flex-1 px-4 flex justify-between">
            <div className="flex-1 flex items-center">
              {/* Contextual breadcrumb or title could go here */}
              <div className="flex items-center space-x-2 text-sm">
                <span className="text-text-muted">SOC</span>
                <span className="text-text-muted">/</span>
                <span className="text-teal font-medium">
                  {navItems.find(i => location.pathname === i.path || (i.path !== '/' && location.pathname.startsWith(i.path)))?.name || 'Dashboard'}
                </span>
              </div>
            </div>
            <div className="ml-4 flex items-center md:ml-6 space-x-4">
              <div className="flex items-center">
                <div className="h-2 w-2 rounded-full bg-semantic-success mr-2 animate-pulse shadow-[0_0_5px_#10B981]"></div>
                <span className="text-xs text-text-muted uppercase tracking-wider font-semibold">System Online</span>
              </div>
              
              <button className="p-1 rounded-full text-text-muted hover:text-text-light focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-offset-navy focus:ring-teal relative">
                <span className="absolute top-1 right-1 h-2 w-2 rounded-full bg-semantic-danger"></span>
                <Bell className="h-5 w-5" />
              </button>

              <button
                onClick={handleLogout}
                className="p-1 rounded-full text-text-muted hover:text-text-light hover:text-semantic-danger transition-colors focus:outline-none"
                title="Disconnect Session"
              >
                <LogOut className="h-5 w-5" />
              </button>
            </div>
          </div>
        </div>

        <main className="flex-1 relative z-0 overflow-y-auto focus:outline-none bg-navy-dark/50">
          <div className="py-6">
            <div className="max-w-7xl mx-auto px-4 sm:px-6 md:px-8">
              <Outlet />
            </div>
          </div>
        </main>
      </div>
    </div>
  );
}
