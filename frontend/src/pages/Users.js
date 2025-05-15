import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { useNavigate } from 'react-router-dom';

// Dummy data for now
const dummyUsers = [
  { id: 1, name: 'Alice Wonderland', email: 'alice@example.com', phoneNumber: '555-0101' },
  { id: 2, name: 'Bob Builder', email: 'bob@example.com', phoneNumber: '555-0102' },
  { id: 3, name: 'Charlie Chaplin', email: 'charlie@example.com', phoneNumber: '555-0103' },
  { id: 4, name: 'Diana Prince', email: 'diana@example.com', phoneNumber: '555-0104' },
  { id: 5, name: 'Edward Scissorhands', email: 'edward@example.com', phoneNumber: '555-0105' },
];

function Users() {
  const { t } = useTranslation();
  const [users, setUsers] = useState(dummyUsers);
  const navigate = useNavigate();

  const handleUserClick = (userId) => {
    navigate(`/users/${userId}`);
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
      <div className="container mx-auto max-w-4xl">
        <h1 className="text-3xl font-bold text-gray-800 mb-8 text-center">
          {t('users.title', 'Users')}
        </h1>

        {users.length === 0 ? (
          <p className="text-center text-gray-500 mt-10">{t('users.noUsers', 'No users found.')}</p>
        ) : (
          <div className="bg-white shadow-md rounded-lg overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-100">
                <tr>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.id', 'ID')}
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.name', 'Name')}
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.email', 'Email')}
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                    {t('users.table.phone', 'Phone Number')}
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {users.map((user) => (
                  <tr key={user.id} onClick={() => handleUserClick(user.id)} className="hover:bg-gray-50 cursor-pointer">
                    <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900 text-left">
                      {user.id}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-800 text-left">
                      {user.name}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-left">
                      {user.email}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-left">
                      {user.phoneNumber}
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