import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { FaArrowLeft } from 'react-icons/fa';
import axios from 'axios';

const formatDegreeType = (degreeType, t) => {
  switch (degreeType) {
    case 'UNIVERSITY_DEGREE':
      return t('degreeType.universityDegree', 'University Degree');
    case 'MASTER':
      return t('degreeType.master', 'Master');
    case 'DOCTORATE':
      return t('degreeType.doctorate', 'Doctorate');
    default:
      return degreeType;
  }
};

function UserProfile() {
  const { t } = useTranslation();
  const { id } = useParams();
  const [user, setUser] = useState(null);
  const [lentItems, setLentItems] = useState([]);
  const [borrowedItems, setBorrowedItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    Promise.all([
      axios.get(`http://localhost:8080/api/users/${id}`, { withCredentials: true }),
      axios.get(`http://localhost:8080/api/users/${id}/lent-items`, { withCredentials: true }),
      axios.get(`http://localhost:8080/api/users/${id}/borrowed-items`, { withCredentials: true }),
    ])
      .then(([uRes, lentRes, borRes]) => {
        setUser(uRes.data);
        setLentItems(lentRes.data);
        setBorrowedItems(borRes.data);
      })
      .catch(err => {
        console.error(err);
        setError(t('userProfile.fetchError', 'Error cargando perfil.'));
      })
      .finally(() => setLoading(false));
  }, [id, t]);

  if (loading) {
    return <p className="text-center mt-8">{t('userProfile.loading', 'Cargando perfil...')}</p>;
  }

  if (error) {
    return <p className="text-center mt-8 text-red-500">{error}</p>;
  }

  if (!user) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center bg-gray-50 p-6">
        <h2 className="text-2xl font-semibold text-red-600 mb-4">
          {t('userProfile.error.notFoundTitle', 'User Not Found')}
        </h2>
        <p className="text-gray-600 mb-6">
          {t('userProfile.error.notFoundDetails', 'Could not find a user with this ID.')}
        </p>
        <Link
          to="/users"
          className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700"
        >
          <FaArrowLeft className="mr-2" />
          {t('userProfile.backToUsers', 'Back to Users List')}
        </Link>
      </div>
    );
  }

  const ItemList = ({ items, type }) => {
    if (!items.length) {
      return (
        <p className="text-gray-500 italic">
          {t(
            type === 'lent' ? 'userProfile.noLentItems' : 'userProfile.noBorrowedItems',
            type === 'lent'
              ? `No items lent by ${user.name}.`
              : `No items borrowed by ${user.name}.`
          )}
        </p>
      );
    }
    return (
      <ul className="divide-y divide-gray-200">
        {items.map(item => (
          <li key={item.id} className="py-3 flex justify-between items-center">
            <div>
              <p className="text-md font-medium text-gray-800">{item.itemName}</p>
              <p className="text-sm text-gray-500">
                {type === 'lent'
                  ? `${t('userProfile.lentTo', 'Lent to:')} ${item.borrowerName}`
                  : `${t('userProfile.borrowedFrom', 'Borrowed from:')} ${item.lenderName}`}
                {item.date && ` - ${t('userProfile.date', 'Date:')} ${item.date}`}
              </p>
            </div>
            <span
              className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                item.status === 'Returned' || item.status === 'Available'
                  ? 'bg-green-100 text-green-800'
                  : 'bg-yellow-100 text-yellow-800'
              }`}
            >
              {t(`itemStatus.${item.status.toLowerCase()}`, item.status)}
            </span>
          </li>
        ))}
      </ul>
    );
  };

  return (
    <div className="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
      <div className="container mx-auto max-w-2xl">
        <div className="mb-6">
          <Link to="/users" className="inline-flex items-center text-blue-600 hover:text-blue-800">
            <FaArrowLeft className="mr-2" />
            {t('userProfile.backToUsers', 'Back to Users List')}
          </Link>
        </div>

        <div className="bg-white shadow-xl rounded-lg overflow-hidden">
          <div className="bg-gray-800 p-6">
            <h1 className="text-3xl font-bold text-white text-center">{user.name}</h1>
          </div>

          <div className="p-6 space-y-4">
            <div>
              <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                {t('userProfile.email', 'Email')}
              </h3>
              <p className="text-lg text-gray-900">{user.email}</p>
            </div>
            <div>
              <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                {t('userProfile.phone', 'Phone Number')}
              </h3>
              <p className="text-lg text-gray-900">{user.phoneNumber}</p>
            </div>
            <div>
              <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                {t('userProfile.address', 'Address')}
              </h3>
              <p className="text-lg text-gray-900">{user.address}</p>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                  {t('userProfile.degreeType', 'Degree Type')}
                </h3>
                <p className="text-lg text-gray-900">
                  {formatDegreeType(user.degreeType, t)}
                </p>
              </div>
              <div>
                <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wider">
                  {t('userProfile.degreeYear', 'Degree Year')}
                </h3>
                <p className="text-lg text-gray-900">{user.degreeYear}</p>
              </div>
            </div>
          </div>

          <div className="border-t border-gray-200">
            <div className="bg-gray-100 p-4 border-b border-gray-200">
              <h2 className="text-xl font-semibold text-gray-700">
                {t('userProfile.itemsLentTitle', 'Items Lent by {{name}}', { name: user.name })}
              </h2>
            </div>
            <div className="p-6">
              <ItemList items={lentItems} type="lent" />
            </div>
          </div>

          <div className="border-t border-gray-200">
            <div className="bg-gray-100 p-4 border-b border-gray-200">
              <h2 className="text-xl font-semibold text-gray-700">
                {t('userProfile.itemsBorrowedTitle', 'Items Borrowed by {{name}}', { name: user.name })}
              </h2>
            </div>
            <div className="p-6">
              <ItemList items={borrowedItems} type="borrowed" />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default UserProfile;
