import React from 'react';
import { useParams, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { FaArrowLeft } from 'react-icons/fa';

// Detailed dummy data for user profiles
const dummyUserProfiles = {
  '1': { id: 1, name: 'Alice Wonderland', email: 'alice@example.com', phoneNumber: '555-0101', address: '123 Main St, Anytown, USA', degreeType: 'MASTER', degreeYear: 2022 },
  '2': { id: 2, name: 'Bob Builder', email: 'bob@example.com', phoneNumber: '555-0102', address: '456 Oak Ave, Anytown, USA', degreeType: 'UNIVERSITY_DEGREE', degreeYear: 2020 },
  '3': { id: 3, name: 'Charlie Chaplin', email: 'charlie@example.com', phoneNumber: '555-0103', address: '789 Pine Ln, Anytown, USA', degreeType: 'DOCTORATE', degreeYear: 2023 },
  '4': { id: 4, name: 'Diana Prince', email: 'diana@example.com', phoneNumber: '555-0104', address: '321 Elm Rd, Anytown, USA', degreeType: 'MASTER', degreeYear: 2021 },
  '5': { id: 5, name: 'Edward Scissorhands', email: 'edward@example.com', phoneNumber: '555-0105', address: '654 Maple Dr, Anytown, USA', degreeType: 'UNIVERSITY_DEGREE', degreeYear: 2019 },
};

// Dummy data for item history
const dummyLentItemsData = [
  { id: 'l1', userId: 1, itemName: 'Laptop Pro 15"', borrowerName: 'Bob B.', status: 'Returned', date: '2023-05-15' },
  { id: 'l2', userId: 1, itemName: 'Gardening Tools Set', borrowerName: 'Charlie C.', status: 'Lent', date: '2023-06-01' },
  { id: 'l3', userId: 2, itemName: 'Electric Guitar', borrowerName: 'Alice W.', status: 'Lent', date: '2023-06-10' },
  { id: 'l4', userId: 1, itemName: 'Camping Tent (4-person)', borrowerName: 'Diana P.', status: 'Lent', date: '2023-07-01' },
];

const dummyBorrowedItemsData = [
  { id: 'b1', userId: 1, itemName: 'Heavy Duty Ladder', lenderName: 'Diana P.', status: 'Borrowed', date: '2023-05-20' },
  { id: 'b2', userId: 2, itemName: 'Portable Speaker', lenderName: 'Edward S.', status: 'Returned', date: '2023-04-10' },
  { id: 'b3', userId: 1, itemName: 'Sewing Machine', lenderName: 'Fiona G.', status: 'Borrowed', date: '2023-06-05' },
  { id: 'b4', userId: 3, itemName: 'Cookbook Collection', lenderName: 'Alice W.', status: 'Borrowed', date: '2023-07-05' },
];

// Helper to format degree type for display
const formatDegreeType = (degreeType, t) => {
  switch (degreeType) {
    case 'UNIVERSITY_DEGREE': return t('degreeType.universityDegree', 'University Degree');
    case 'MASTER': return t('degreeType.master', 'Master');
    case 'DOCTORATE': return t('degreeType.doctorate', 'Doctorate');
    default: return degreeType;
  }
};

function UserProfile() {
  const { t } = useTranslation();
  const { id } = useParams();
  const user = dummyUserProfiles[id]; // Find user by ID from dummy data

  // Filter item history for the current user
  const lentItems = dummyLentItemsData.filter(item => item.userId === parseInt(id));
  const borrowedItems = dummyBorrowedItemsData.filter(item => item.userId === parseInt(id));

  if (!user) {
    return (
      <div className="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8 flex flex-col items-center justify-center">
        <h2 className="text-2xl font-semibold text-red-600 mb-4">{t('userProfile.error.notFoundTitle', 'User Not Found')}</h2>
        <p className="text-gray-600 mb-6">{t('userProfile.error.notFoundDetails', 'Could not find a user with this ID.')}</p>
        <Link to="/users" className="inline-flex items-center px-4 py-2 border border-transparent text-sm font-medium rounded-md shadow-sm text-white bg-blue-600 hover:bg-blue-700">
          <FaArrowLeft className="mr-2" />
          {t('userProfile.backToUsers', 'Back to Users List')}
        </Link>
      </div>
    );
  }

  // Add this console.log to check the value of user.name
  if (user) {
    console.log("Current user name for profile page titles:", user.name);
  }

  const ItemList = ({ items, type, userName }) => {
    if (!items || items.length === 0) {
      return <p className="text-gray-500 italic text-left">{t(type === 'lent' ? 'userProfile.noLentItems' : 'userProfile.noBorrowedItems', type === 'lent' ? `No items lent by ${userName}.` : `No items borrowed by ${userName}.`)}</p>;
    }
    return (
      <ul className="divide-y divide-gray-200">
        {items.map(item => (
          <li key={item.id} className="py-3 flex justify-between items-center">
            <div className="text-left">
              <p className="text-md font-medium text-gray-800 text-left">{item.itemName}</p>
              <p className="text-sm text-gray-500 text-left">
                {type === 'lent' ? `${t('userProfile.lentTo', 'Lent to:')} ${item.borrowerName}` : `${t('userProfile.borrowedFrom', 'Borrowed from:')} ${item.lenderName}`}
                {item.date && ` - ${t('userProfile.date', 'Date:')} ${item.date}`}
              </p>
            </div>
            <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${item.status === 'Returned' || item.status === 'Available' ? 'bg-green-100 text-green-800' : 'bg-yellow-100 text-yellow-800'}`}>
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

        {/* Single Combined Card */}
        <div className="bg-white shadow-xl rounded-lg overflow-hidden">
          {/* User Info Header */}
          <div className="bg-gray-800 p-6">
            <h1 className="text-3xl font-bold text-white text-center">{user.name}</h1>
          </div>
          
          {/* User Info Details */}
          <div className="p-6 space-y-4">
            <div>
              <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wider">{t('userProfile.email', 'Email')}</h3>
              <p className="text-lg text-gray-900">{user.email}</p>
            </div>
            <div>
              <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wider">{t('userProfile.phone', 'Phone Number')}</h3>
              <p className="text-lg text-gray-900">{user.phoneNumber}</p>
            </div>
            <div>
              <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wider">{t('userProfile.address', 'Address')}</h3>
              <p className="text-lg text-gray-900">{user.address}</p>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                <div>
                    <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wider">{t('userProfile.degreeType', 'Degree Type')}</h3>
                    <p className="text-lg text-gray-900">{formatDegreeType(user.degreeType, t)}</p>
                </div>
                <div>
                    <h3 className="text-xs font-medium text-gray-500 uppercase tracking-wider">{t('userProfile.degreeYear', 'Degree Year')}</h3>
                    <p className="text-lg text-gray-900">{user.degreeYear}</p>
                </div>
            </div>
          </div>

          {/* Items Lent Section - now part of the same card */}
          <div className="border-t border-gray-200">
            <div className="bg-gray-100 p-4 border-b border-gray-200">
              <h2 className="text-xl font-semibold text-gray-700">
                  {t('userProfile.itemsLentTitle', 'Items Lent by {name}', { name: user.name })}
              </h2>
            </div>
            <div className="p-6">
              <ItemList items={lentItems} type="lent" userName={user.name} />
            </div>
          </div>

          {/* Items Borrowed Section - now part of the same card */}
          <div className="border-t border-gray-200">
            <div className="bg-gray-100 p-4 border-b border-gray-200">
              <h2 className="text-xl font-semibold text-gray-700">
                  {t('userProfile.itemsBorrowedTitle', 'Items Borrowed by {name}', { name: user.name })}
              </h2>
            </div>
            <div className="p-6">
              <ItemList items={borrowedItems} type="borrowed" userName={user.name} />
            </div>
          </div>

        </div>
      </div>
    </div>
  );
}

export default UserProfile; 