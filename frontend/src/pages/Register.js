import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { registerUser, loginUser } from "../api";
import { useAuth } from '../context/AuthContext';
import { useTranslation } from 'react-i18next';

function Register() {
  const { t } = useTranslation();
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    confirmPassword: '',
    telephoneNumber: '',
    address: '',
    degreeType: '', // Changed from UNIVERSITY_DEGREE to empty for placeholder
    degreeYear: '',
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();
  const { login } = useAuth();

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
      setError(t('register.passwordMismatch'));
      return;
    }
    // Registration logic
    try {
      // First register the user
      await registerUser({
        name: formData.firstName, // Changed from firstName to name to match RegistrationRecord
        lastName: formData.lastName,
        email: formData.email,
        password: formData.password,
        telephoneNumber: formData.telephoneNumber,
        address: formData.address,
        degreeType: formData.degreeType,
        degreeYear: formData.degreeYear ? parseInt(formData.degreeYear, 10) : null,
      });

      // Then automatically log them in
      const loginResponse = await loginUser({
        email: formData.email,
        password: formData.password
      });

      // Set the authentication state
      login(loginResponse.token, loginResponse.user);
      navigate('/');
    } catch (error) {
      setError(error.response?.data?.message || error.message || t('register.error'));
    }
    console.log('Registration attempt:', formData);
  };

  return (
    <div className="min-h-screen bg-gray-50 flex flex-col justify-start pt-20 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <h2 className="text-center text-3xl font-extrabold text-gray-900">
        {t('register.createAccount')}
        </h2>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-4 shadow sm:rounded-lg sm:px-10">
          {error && (
            <div className="mb-4 p-3 text-sm text-red-700 bg-red-100 rounded-md">
              {error}
            </div>
          )}
          <form className="space-y-6" onSubmit={handleSubmit}>
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div>
                <label htmlFor="firstName" className="block text-sm font-medium text-gray-700 text-left">
                {t('register.firstName')}
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
                    className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                  />
                </div>
              </div>

              <div>
                <label htmlFor="lastName" className="block text-sm font-medium text-gray-700 text-left">
                  {t('register.lastName')}
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
                    className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                  />
                </div>
              </div>
            </div>

            <div>
              <label htmlFor="email" className="block text-sm font-medium text-gray-700 text-left">
                {t('register.email')}
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
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                />
              </div>
            </div>

            <div>
              <label htmlFor="telephoneNumber" className="block text-sm font-medium text-gray-700 text-left">
                {t('register.telephoneNumber', 'Telephone Number')}
              </label>
              <div className="mt-1">
                <input
                  id="telephoneNumber"
                  name="telephoneNumber"
                  type="tel"
                  autoComplete="tel"
                  value={formData.telephoneNumber}
                  onChange={handleChange}
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                />
              </div>
            </div>

            <div>
              <label htmlFor="address" className="block text-sm font-medium text-gray-700 text-left">
                {t('register.address', 'Address')}
              </label>
              <div className="mt-1">
                <input
                  id="address"
                  name="address"
                  type="text"
                  autoComplete="street-address"
                  value={formData.address}
                  onChange={handleChange}
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                />
              </div>
            </div>
            
            <div className="grid grid-cols-1 gap-6 sm:grid-cols-2">
              <div>
                <label htmlFor="degreeType" className="block text-sm font-medium text-gray-700 text-left">
                  {t('register.degreeType', 'Degree Type')}
                </label>
                <div className="mt-1">
                  <select
                    id="degreeType"
                    name="degreeType"
                    value={formData.degreeType}
                    onChange={handleChange}
                    required
                    className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                  >
                    <option value="" disabled>{t('register.selectDegreeType', 'Select Degree Type...')}</option>
                    <option value="UNIVERSITY_DEGREE">{t('degreeType.universityDegree', 'University Degree')}</option>
                    <option value="MASTER">{t('degreeType.master', 'Master')}</option>
                    <option value="DOCTORATE">{t('degreeType.doctorate', 'Doctorate')}</option>
                  </select>
                </div>
              </div>

              <div>
                <label htmlFor="degreeYear" className="block text-sm font-medium text-gray-700 text-left">
                  {t('register.degreeYear', 'Degree Year')}
                </label>
                <div className="mt-1">
                  <input
                    id="degreeYear"
                    name="degreeYear"
                    type="number"
                    value={formData.degreeYear}
                    onChange={handleChange}
                    className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                  />
                </div>
              </div>
            </div>

            <div>
              <label htmlFor="password" className="block text-sm font-medium text-gray-700 text-left">
                {t('register.password')}
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
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                />
              </div>
            </div>

            <div>
              <label htmlFor="confirmPassword" className="block text-sm font-medium text-gray-700 text-left">
                {t('register.confirmPassword')}
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
                  className="appearance-none block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-blue-500 focus:border-blue-500 sm:text-sm"
                />
              </div>
            </div>

            <div>
              <button
                type="submit"
                className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                {t('register.createAccount')}
              </button>
            </div>
          </form>

          <div className="mt-6">
            <div className="relative">
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-white text-gray-500">
                  {t('register.alreadyHaveAccount')}{' '}
                  <Link to="/login" className="font-medium text-blue-600 hover:text-blue-500">
                    {t('register.signIn')}
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