import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { loginUser } from "../api";
import { useAuth } from '../context/AuthContext';

function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [debugMode, setDebugMode] = useState(false);
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { login, isAuthenticated } = useAuth();

  // Check if debug mode is enabled in localStorage on component mount
  useEffect(() => {
    const savedDebugMode = localStorage.getItem('debugMode') === 'true';
    setDebugMode(savedDebugMode);
    
    // If already logged in, redirect to home
    if (isAuthenticated) {
      navigate('/');
    }
  }, [navigate, isAuthenticated]);

  // Toggle debug mode and save to localStorage
  const toggleDebugMode = () => {
    const newDebugMode = !debugMode;
    setDebugMode(newDebugMode);
    localStorage.setItem('debugMode', newDebugMode);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    
    try {
      // If debug mode is enabled, use empty credentials to trigger the debug login
      const credentials = debugMode ? { email: '', password: '' } : { email, password };
      const response = await loginUser(credentials);
      
      // Use the auth context login function
      login(response.token, {
        firstName: response.firstName || 'Demo',
        lastName: response.lastName || 'Account',
        email: response.email || 'debug@test.com'
      });
      
      navigate('/');
    } catch (error) {
      setError(error.response?.data?.message || "Login failed. Please try again.");
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-start pt-20 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <h2 className="text-center text-3xl font-extrabold text-gray-900">
          Sign in to your account
        </h2>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
          {/* Debug Mode Toggle */}
          <div className="mb-6 flex items-center justify-between">
            <span className="text-sm text-gray-500">Debug Mode</span>
            <button
              type="button"
              onClick={toggleDebugMode}
              className={`relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 ${
                debugMode ? 'bg-blue-600' : 'bg-gray-200'
              }`}
            >
              <span
                className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                  debugMode ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>
          
          {debugMode && (
            <div className="mb-6 p-3 bg-blue-50 text-blue-700 rounded-md text-sm">
              <p className="font-medium">Debug Mode Enabled</p>
              <p className="mt-1">Demo Account: debug@test.com</p>
              <p>Just click Sign in to log in with the demo account.</p>
            </div>
          )}

          {error && (
            <div className="mb-6 p-3 bg-red-50 text-red-700 rounded-md text-sm">
              {error}
            </div>
          )}

          <form className="space-y-6" onSubmit={handleSubmit}>
            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 text-left">
                Email address
              </label>
              <div className="mt-1">
                <input
                  id="email"
                  name="email"
                  type="email"
                  autoComplete="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                  disabled={debugMode}
                />
              </div>
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 text-left">
                Password
              </label>
              <div className="mt-1">
                <input
                  id="password"
                  name="password"
                  type="password"
                  autoComplete="current-password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                  disabled={debugMode}
                />
              </div>
            </div>

            <div>
              <button
                type="submit"
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                {debugMode ? 'Sign in with Demo Account' : 'Sign in'}
              </button>
            </div>
          </form>

          <div className="mt-6">
            <div className="relative">
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white text-gray-500">
                  Don't have an account?{' '}
                  <Link to="/register" className="font-medium text-blue-600 hover:text-blue-500">
                    Sign up
                  </Link>
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default Login; 