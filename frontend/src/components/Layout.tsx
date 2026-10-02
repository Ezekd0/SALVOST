import { useEffect, useRef, useState } from 'react';
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

  const menuButton = useRef<HTMLButtonElement>(null);
  const drawer = useRef<HTMLDivElement>(null);
  useEffect(() => {
    if (!isMobileMenuOpen) return;
    const previousOverflow = document.body.style.overflow;
    document.body.style.overflow = 'hidden';
    drawer.current?.querySelector<HTMLElement>('button, a')?.focus();
    const onKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') setIsMobileMenuOpen(false);
      if (event.key === 'Tab') {
        const controls = drawer.current?.querySelectorAll<HTMLElement>('button, a');
        if (!controls?.length) return;
        const first = controls[0], last = controls[controls.length - 1];
        if (event.shiftKey && document.activeElement === first) { event.preventDefault(); last.focus(); }
        else if (!event.shiftKey && document.activeElement === last) { event.preventDefault(); first.focus(); }
      }
    };
    const onResize = () => { if (window.innerWidth >= 1024) setIsMobileMenuOpen(false); };
    window.addEventListener('keydown', onKeyDown);
    window.addEventListener('resize', onResize);
    return () => {
      document.body.style.overflow = previousOverflow;
      window.removeEventListener('keydown', onKeyDown);
      window.removeEventListener('resize', onResize);
      menuButton.current?.focus();
    };
  }, [isMobileMenuOpen]);

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
        className={`group flex min-h-11 lg:min-h-0 items-center px-3 py-2 text-sm font-medium rounded-md transition-colors ${
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
    <div className="h-dvh bg-navy flex overflow-hidden app-shell">
      {/* Mobile sidebar */}
      <div className={`lg:hidden ${isMobileMenuOpen ? 'fixed inset-0 flex z-40' : 'hidden'}`}>
        <div className="fixed inset-0 bg-navy-dark bg-opacity-75 transition-opacity" onClick={() => setIsMobileMenuOpen(false)}></div>
        <div ref={drawer} id="workspace-navigation" role="dialog" aria-modal="true" aria-label="Workspace navigation" className="relative flex flex-col w-[min(20rem,calc(100vw-3rem))] pt-5 pb-4 bg-navy-dark border-r border-navy">
          <div className="absolute top-0 right-0 -mr-12 pt-2">
            <button
              aria-label="Close navigation" className="ml-1 flex items-center justify-center h-11 w-11 rounded-full focus:outline-none focus:ring-2 focus:ring-inset focus:ring-teal"
              onClick={() => setIsMobileMenuOpen(false)}
            >
              <X className="h-6 w-6 text-text-light" />
            </button>
          </div>
          <div className="flex-shrink-0 flex items-center px-4">
            <ShieldAlert className="h-8 w-8 shrink-0 text-teal" />
            <div className="ml-3 min-w-0"><span className="text-xl font-bold text-text-light tracking-wider">AI-CTDRS</span><p className="text-xs text-text-muted">Android Threat Detection</p></div>
          </div>
          <div className="mt-5 flex-1 h-0 overflow-y-auto">
            <nav className="px-2 space-y-1">
              {[...navItems, ...bottomNavItems].map((item) => (
                <NavItem key={item.name} item={item} />
              ))}
            </nav>
            <button onClick={handleLogout} className="m-4 p-3 text-sm text-text-muted border border-navy rounded">Disconnect Session</button>
          </div>
        </div>
      </div>

      {/* Static sidebar for desktop */}
      <div className="hidden lg:flex lg:flex-shrink-0 border-r border-navy-dark bg-[#0A1520]">
        <div className="flex flex-col w-64">
          <div className="flex items-center h-16 flex-shrink-0 px-4 border-b border-navy-dark bg-[#070F18]">
            <ShieldAlert className="h-8 w-8 shrink-0 text-teal drop-shadow-[0_0_8px_rgba(23,195,178,0.5)]" />
            <div className="ml-3 min-w-0"><span className="text-xl font-bold text-text-light tracking-wider">AI-CTDRS</span><p className="text-xs text-text-muted">Android Threat Detection</p></div>
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
              <div className="ml-3 min-w-0 break-words">
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
            ref={menuButton} aria-label="Open navigation" aria-expanded={isMobileMenuOpen} aria-controls="workspace-navigation" className="px-3 border-r border-navy-dark text-text-muted focus:outline-none focus:ring-2 focus:ring-inset focus:ring-teal lg:hidden"
            onClick={() => setIsMobileMenuOpen(true)}
          >
            <Menu className="h-6 w-6" />
          </button>
          
          <div className="min-w-0 flex-1 px-3 sm:px-4 flex justify-between gap-2">
            <div className="min-w-0 flex-1 flex items-center">
              {/* Contextual breadcrumb or title could go here */}
              <div className="flex items-center space-x-2 text-sm">
                <span className="text-text-muted">SOC</span>
                <span className="text-text-muted">/</span>
                <span className="text-teal font-medium truncate">
                  {[...navItems, ...bottomNavItems].find(i => location.pathname === i.path || (i.path !== '/' && location.pathname.startsWith(i.path)))?.name || 'Dashboard'}
                </span>
              </div>
            </div>
            <div className="shrink-0 flex items-center sm:ml-4 gap-2 sm:gap-4">
              <div className="hidden sm:flex items-center">
                <div className="h-2 w-2 rounded-full bg-semantic-success mr-2 animate-pulse shadow-[0_0_5px_#10B981]"></div>
                <span className="text-xs text-text-muted uppercase tracking-wider font-semibold">System Online</span>
              </div>
              
              <NavLink to="/incidents" aria-label="View security incidents" className="h-11 w-11 flex items-center justify-center rounded-full text-text-muted hover:text-text-light focus:ring-2 focus:ring-teal">
                <Bell className="h-5 w-5" />
              </NavLink>

              <button
                onClick={handleLogout}
                className="h-11 w-11 flex items-center justify-center rounded-full text-text-muted hover:text-semantic-danger transition-colors focus:outline-none"
                aria-label="Disconnect Session" title="Disconnect Session"
              >
                <LogOut className="h-5 w-5" />
              </button>
            </div>
          </div>
        </div>

        <main className="workspace-main min-w-0 flex-1 relative z-0 overflow-y-auto focus:outline-none bg-navy-dark/50">
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
