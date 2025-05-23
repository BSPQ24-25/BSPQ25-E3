import React, { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

function Users() {
  const { t } = useTranslation();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem('token');

    axios
      .get('http://localhost:8080/users', {
        params: { token },
        withCredentials: true
      })
      .then(({ data }) => {
        if (Array.isArray(data)) setUsers(data);
        else throw new Error('API devolvió algo distinto a un array');
      })
      .catch(err => {
        console.error('[Users] error en GET /users:', err);
        if (err.response?.status === 401) {
          setError(t('users.notAuthorized', 'You are not authorized to see the user list.'));
        } else {
          setError(t('users.fetchError', 'Error loading users.'));
        }
      })
      .finally(() => setLoading(false));
  }, [t]);

  if (loading) return <p className="text-center mt-8">{t('users.loading', 'Cargando...')}</p>;
  if (error)   return <p className="text-center mt-8 text-red-500">{error}</p>;

  return (
    <div className="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
      {/* Contenedor a ancho completo */}
      <div className="container mx-auto w-full">
        <h1 className="text-3xl font-bold text-gray-800 mb-8 text-center">
          {t('users.title', 'Users')}
        </h1>

        {users.length === 0 ? (
          <p className="text-center text-gray-500 mt-10">{t('users.noUsers', 'No hay usuarios para mostrar.')}</p>
        ) : (
          // Wrapper con overflow-x para scroll si hace falta
          <div className="bg-white shadow-md rounded-lg overflow-x-auto">
            {/* Tabla a ancho completo */}
            <table className="min-w-full w-full divide-y divide-gray-200">
              <thead className="bg-gray-100">
                <tr>
                  <th className="px-8 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.id', 'ID')}
                  </th>
                  <th className="px-8 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.name', 'Name')}
                  </th>
                  <th className="px-8 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.email', 'Email')}
                  </th>
                  <th className="px-8 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.phone', 'Phone Number')}
                  </th>
                  <th className="px-8 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.address', 'Address')}
                  </th>
                  <th className="px-8 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.degreeType', 'Degree Type')}
                  </th>
                  <th className="px-8 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.degreeYear', 'Degree Year')}
                  </th>
                  <th className="px-8 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.penalties', 'Penalties')}
                  </th>
                  <th className="px-8 py-4 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.averageRating', 'Average Rating')}
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {users.map(user => (
                  <tr
                    key={user.id}
                    onClick={() => navigate(`/users/${user.id}`)}
                    className="hover:bg-gray-50 cursor-pointer"
                  >
                    <td className="px-8 py-4 whitespace-nowrap text-sm font-medium text-gray-900">
                      {user.id}
                    </td>
                    <td className="px-8 py-4 whitespace-nowrap text-sm text-gray-800">
                      {user.name}
                    </td>
                    <td className="px-8 py-4 whitespace-nowrap text-sm text-gray-500">
                      {user.email}
                    </td>
                    <td className="px-8 py-4 whitespace-nowrap text-sm text-gray-500">
                      {user.telephoneNumber}
                    </td>
                    <td className="px-8 py-4 whitespace-nowrap text-sm text-gray-500">
                      {user.address}
                    </td>
                    <td className="px-8 py-4 whitespace-nowrap text-sm text-gray-500">
                      {user.degreeType}
                    </td>
                    <td className="px-8 py-4 whitespace-nowrap text-sm text-gray-500">
                      {user.degreeYear}
                    </td>
                    <td className="px-8 py-4 whitespace-nowrap text-sm text-gray-500">
                      {user.penalties}
                    </td>
                    <td className="px-8 py-4 whitespace-nowrap text-sm text-gray-500">
                      {user.averageRating?.toFixed(2)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}

export default Users;
