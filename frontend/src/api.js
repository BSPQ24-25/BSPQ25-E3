import axios from "axios";

const API_URL = "http://localhost:8080";

// Check if debug mode is enabled
const isDebugMode = () => {
  return localStorage.getItem('debugMode') === 'true';
};

// Mock data for debug mode
const mockUserData = {
  id: 'debug-user-id',
  firstName: 'Demo',
  lastName: 'Account',
  email: 'debug@test.com',
  token: 'debug-token-' + Date.now()
};

export const registerUser = async (userData) => {
  // If debug mode is enabled, return mock data
  if (isDebugMode()) {
    console.log('Debug mode: Skipping API call for registration');
    return { ...mockUserData, message: 'Debug registration successful' };
  }

  try {
    const response = await axios.post(`${API_URL}/users`, userData);
    return response.data;
  } catch (error) {
    console.error("Error en el registro:", error.response?.data || error.message);
    throw error;
  }
};

export const loginUser = async (loginData) => {
  // If debug mode is enabled, return mock data regardless of credentials
  if (isDebugMode()) {
    console.log('Debug mode: Skipping API call for login');
    return { ...mockUserData, message: 'Debug login successful' };
  }

  try {
    const response = await axios.post(`${API_URL}/users/login`, loginData);
    return response.data;
  } catch (error) {
    console.error("Error en el login:", error.response?.data || error.message);
    throw error;
  }
};
