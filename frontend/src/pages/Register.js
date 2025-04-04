import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { registerUser } from "../api";
import { useAuth } from '../context/AuthContext';

// Debug mode configuration
const DEBUG_ACCOUNT = {
  token: 'debug-token-12345',
  user: {
    firstName: 'Debug',
    lastName: 'User',
    email: 'debug@example.com'
  }
};

function Register() {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [debugMode, setDebugMode] = useState(false);
  const navigate = useNavigate();
  const { login } = useAuth();

  // Check for debug mode in localStorage on component mount
  useEffect(() => {
    const savedDebugMode = localStorage.getItem('debugMode') === 'true';
    setDebugMode(savedDebugMode);
  }, []);

  // Save debug mode preference to localStorage
  const toggleDebugMode = () => {
    const newDebugMode = !debugMode;
    setDebugMode(newDebugMode);
    localStorage.setItem('debugMode', newDebugMode);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value
    }));
    // Clear error when user starts typing
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Validate passwords match
    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    
    // Registration logic
    try {
      let response;
      
      if (debugMode) {
        // In debug mode, simulate successful registration with debug account
        console.log('Debug mode: Simulating successful registration');
        response = DEBUG_ACCOUNT;
      } else {
        // Normal registration flow
        response = await registerUser({
          firstName: formData.firstName,
          lastName: formData.lastName,
          email: formData.email,
          password: formData.password,
        });
      }
  
      // Log the user in automatically after successful registration
      login(response.token, response.user);
      
      // Redirect to home page
      navigate('/');
    } catch (error) {
      setError(error.response?.data?.message || error.message);
    }
    console.log('Registration attempt:', formData);
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-start pt-20 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <h2 className="text-center text-3xl font-extrabold text-gray-900">
          Create your account
        </h2>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
          {/* Debug Mode Toggle */}
          <div className="mb-4 flex items-center justify-end">
            <label htmlFor="debug-mode" className="mr-2 text-sm text-gray-600">
              Debug Mode
            </label>
            <button
              type="button"
              id="debug-mode"
              onClick={toggleDebugMode}
              className={`relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 ${
                debugMode ? 'bg-blue-600' : 'bg-gray-200'
              }`}
              role="switch"
              aria-checked={debugMode}
              aria-labelledby="debug-mode-label"
            >
              <span
                aria-hidden="true"
                className={`pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out ${
                  debugMode ? 'translate-x-5' : 'translate-x-0'
                }`}
              />
            </button>
          </div>
          
          {debugMode && (
            <div className="mb-4 p-3 text-sm text-blue-700 bg-blue-100 rounded-md">
              Debug mode is enabled. You will be logged in with the debug account.
            </div>
          )}
          
          {error && (
            <div className="mb-4 p-3 text-sm text-red-700 bg-red-100 rounded-md">
              {error}
            </div>
          )}
          
          <form className="space-y-6" onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div>
                <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 text-left">
                  First name
                </label>
                <div className="mt-1">
                  <input
                    id="firstName"
                    name="firstName"
                    type="text"
                    autoComplete="given-name"
                    required
                    value={formData.firstName}
                    onChange={handleChange}
                    disabled={debugMode}
                    className={`appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm ${debugMode ? 'bg-gray-100 text-gray-500' : ''}`}
                  />
                </div>
              </div>

              <div>
                <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 text-left">
                  Last name
                </label>
                <div className="mt-1">
                  <input
                    id="lastName"
                    name="lastName"
                    type="text"
                    autoComplete="family-name"
                    required
                    value={formData.lastName}
                    onChange={handleChange}
                    disabled={debugMode}
                    className={`appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm ${debugMode ? 'bg-gray-100 text-gray-500' : ''}`}
                  />
                </div>
              </div>
            </div>

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
                  value={formData.email}
                  onChange={handleChange}
                  disabled={debugMode}
                  className={`appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm ${debugMode ? 'bg-gray-100 text-gray-500' : ''}`}
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
                  autoComplete="new-password"
                  required
                  value={formData.password}
                  onChange={handleChange}
                  disabled={debugMode}
                  className={`appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm ${debugMode ? 'bg-gray-100 text-gray-500' : ''}`}
                />
              </div>
            </div>

            <div>
              <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 text-left">
                Confirm Password
              </label>
              <div className="mt-1">
                <input
                  id="confirmPassword"
                  name="confirmPassword"
                  type="password"
                  autoComplete="new-password"
                  required
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  disabled={debugMode}
                  className={`appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm ${debugMode ? 'bg-gray-100 text-gray-500' : ''}`}
                />
              </div>
            </div>

            <div>
              <button
                type="submit"
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                {debugMode ? 'Register with Debug Account' : 'Create account'}
              </button>
            </div>
          </form>

          <div className="mt-6">
            <div className="relative">
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white text-gray-500">
                  Already have an account?{' '}
                  <Link to="/login" className="font-medium text-blue-600 hover:text-blue-500">
                    Sign in
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

export default Register; 