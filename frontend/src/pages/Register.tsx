import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Shield, Lock, User, AlertCircle, Loader2, Mail } from 'lucide-react';
import api from '../lib/api';
import { useAuth } from '../lib/AuthContext';

export default function Register() {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleRegister = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (password !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setIsLoading(true);

    try {
      await api.post('/auth/register', {
        full_name: fullName,
        email,
        username,
        password,
        confirm_password: confirmPassword
      });

      // Auto login after registration
      const formData = new URLSearchParams();
      formData.append('username', username);
      formData.append('password', password);

      const loginResponse = await api.post('/auth/login', formData, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
      });

      login(loginResponse.data.access_token, { username, role: 'Admin' });
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.detail || 'Registration failed. Please check your details.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-navy flex flex-col justify-center py-12 sm:px-6 lg:px-8 relative overflow-hidden">
      {/* Background patterns */}
      <div className="absolute inset-0 z-0">
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGNpcmNsZSBjeD0iMzAiIGN5PSIzMCIgcj0iMSIgZmlsbD0icmdiYSgyMywgMTk1LCAxNzgsIDAuMTUpIi8+PC9zdmc+')] opacity-20"></div>
        <div className="absolute top-0 left-0 right-0 h-96 bg-gradient-to-b from-navy-dark to-transparent"></div>
        <div className="absolute bottom-0 left-0 right-0 h-96 bg-gradient-to-t from-navy-dark to-transparent"></div>
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-md relative z-10">
        <div className="flex justify-center">
          <div className="h-16 w-16 bg-navy-dark border-2 border-teal rounded-xl flex items-center justify-center shadow-[0_0_15px_rgba(23,195,178,0.5)]">
            <Shield className="h-10 w-10 text-teal" />
          </div>
        </div>
        <h2 className="mt-6 text-center text-3xl font-extrabold text-text-light tracking-tight">
          AI-CTDRS SOC
        </h2>
        <p className="mt-2 text-center text-sm text-text-muted">
          Create Account
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md relative z-10">
        <div className="bg-navy-dark/80 backdrop-blur-md py-8 px-4 shadow-2xl sm:rounded-xl sm:px-10 border border-navy shadow-[0_0_30px_rgba(0,0,0,0.5)]">
          <form className="space-y-4" onSubmit={handleRegister}>
            <div>
              <label htmlFor="fullName" className="block text-sm font-medium text-text-muted mb-1">
                Full Name
              </label>
              <div className="mt-1 relative rounded-md shadow-sm">
                <input
                  id="fullName"
                  name="fullName"
                  type="text"
                  required
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  className="block w-full px-3 bg-navy border border-navy-dark rounded-md py-2 text-text-light focus:outline-none focus:ring-1 focus:ring-teal focus:border-teal sm:text-sm transition-colors"
                  placeholder="John Doe"
                />
              </div>
            </div>

            <div>
              <label htmlFor="email" className="block text-sm font-medium text-text-muted mb-1">
                Email Address
              </label>
              <div className="mt-1 relative rounded-md shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Mail className="h-5 w-5 text-text-muted" />
                </div>
                <input
                  id="email"
                  name="email"
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="block w-full pl-10 bg-navy border border-navy-dark rounded-md py-2 text-text-light focus:outline-none focus:ring-1 focus:ring-teal focus:border-teal sm:text-sm transition-colors"
                  placeholder="operator@example.com"
                />
              </div>
            </div>

            <div>
              <label htmlFor="username" className="block text-sm font-medium text-text-muted mb-1">
                Username
              </label>
              <div className="mt-1 relative rounded-md shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <User className="h-5 w-5 text-text-muted" />
                </div>
                <input
                  id="username"
                  name="username"
                  type="text"
                  required
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="block w-full pl-10 bg-navy border border-navy-dark rounded-md py-2 text-text-light focus:outline-none focus:ring-1 focus:ring-teal focus:border-teal sm:text-sm transition-colors"
                  placeholder="Choose a unique ID"
                />
              </div>
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-text-muted mb-1">
                Password
              </label>
              <div className="mt-1 relative rounded-md shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock className="h-5 w-5 text-text-muted" />
                </div>
                <input
                  id="password"
                  name="password"
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="block w-full pl-10 bg-navy border border-navy-dark rounded-md py-2 text-text-light focus:outline-none focus:ring-1 focus:ring-teal focus:border-teal sm:text-sm transition-colors"
                  placeholder="••••••••"
                />
              </div>
            </div>

            <div>
              <label htmlFor="confirmPassword" className="block text-sm font-medium text-text-muted mb-1">
                Confirm Password
              </label>
              <div className="mt-1 relative rounded-md shadow-sm">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock className="h-5 w-5 text-text-muted" />
                </div>
                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type="password"
                  required
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="block w-full pl-10 bg-navy border border-navy-dark rounded-md py-2 text-text-light focus:outline-none focus:ring-1 focus:ring-teal focus:border-teal sm:text-sm transition-colors"
                  placeholder="••••••••"
                />
              </div>
            </div>

            {error && (
              <div className="rounded-md bg-semantic-danger/10 border border-semantic-danger/30 p-3 flex items-start">
                <AlertCircle className="h-5 w-5 text-semantic-danger mt-0.5 mr-2 flex-shrink-0" />
                <p className="text-sm text-semantic-danger">{error}</p>
              </div>
            )}

            <div className="pt-2">
              <button
                type="submit"
                disabled={isLoading}
                className="w-full flex justify-center py-2.5 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-navy-dark bg-teal hover:bg-teal-dark focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-teal focus:ring-offset-navy disabled:opacity-70 transition-colors"
              >
                {isLoading ? (
                  <>
                    <Loader2 className="animate-spin -ml-1 mr-2 h-4 w-4 text-navy-dark" />
                    Registering...
                  </>
                ) : (
                  'Create Account'
                )}
              </button>
            </div>
          </form>
          
          <div className="mt-6 border-t border-navy pt-4">
            <p className="text-sm text-center text-text-muted">
              Already have an account?{' '}
              <Link to="/login" className="font-medium text-teal hover:text-teal-light">
                Sign in
              </Link>
            </p>
          </div>
        </div>
      </div>
    </div>
  );
}
