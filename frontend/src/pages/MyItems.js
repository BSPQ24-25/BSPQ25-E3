import React, { useState } from 'react';
import ReturnItemModal from '../components/ReturnItemModal';
import ReminderModal from '../components/ReminderModal';
import UploadItemModal from '../components/UploadItemModal';

function MyItems() {
  // Dummy data for borrowed items
  const borrowedItems = [
    {
      id: 1,
      name: 'Calculus Textbook',
      owner: 'John Smith',
      dueDate: '2024-05-15',
      status: 'borrowed'
    },
    {
      id: 2,
      name: 'Physics Lab Kit',
      owner: 'Sarah Johnson',
      dueDate: '2024-04-20',
      status: 'overdue'
    }
  ];

  // Dummy data for lent items
  const lentItems = [
    {
      id: 1,
      name: 'Chemistry Lab Equipment',
      borrower: 'Mike Brown',
      dueDate: '2024-05-10',
      status: 'borrowed'
    },
    {
      id: 2,
      name: 'Biology Textbook',
      borrower: 'Emma Wilson',
      dueDate: '2024-04-25',
      status: 'overdue'
    },
    {
      id: 3,
      name: 'Math Calculator',
      borrower: null,
      dueDate: null,
      status: 'available'
    }
  ];

  const [selectedItem, setSelectedItem] = useState(null);
  const [isReturnModalOpen, setIsReturnModalOpen] = useState(false);
  const [isReminderModalOpen, setIsReminderModalOpen] = useState(false);
  const [isUploadModalOpen, setIsUploadModalOpen] = useState(false);

  const handleReturnItem = (item) => {
    setSelectedItem(item);
    setIsReturnModalOpen(true);
  };

  const handleConfirmReturn = () => {
    // TODO: Implement return item functionality
    console.log('Returning item:', selectedItem.id);
    setIsReturnModalOpen(false);
    setSelectedItem(null);
  };

  const handleCloseReturnModal = () => {
    setIsReturnModalOpen(false);
    setSelectedItem(null);
  };

  const handleSendReminder = (item) => {
    setSelectedItem(item);
    setIsReminderModalOpen(true);
  };

  const handleConfirmReminder = () => {
    // TODO: Implement send reminder functionality
    console.log('Sending reminder for item:', selectedItem.id);
    setIsReminderModalOpen(false);
    setSelectedItem(null);
  };

  const handleCloseReminderModal = () => {
    setIsReminderModalOpen(false);
    setSelectedItem(null);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'borrowed':
        return 'bg-green-100 text-green-800';
      case 'overdue':
        return 'bg-red-100 text-red-800';
      case 'available':
        return 'bg-blue-100 text-blue-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const handleUploadItem = () => {
    setIsUploadModalOpen(true);
  };

  const handleCloseUploadModal = () => {
    setIsUploadModalOpen(false);
  };

  const handleConfirmUpload = (formData) => {
    // TODO: Implement upload item functionality
    console.log('Uploading item:', formData);
    setIsUploadModalOpen(false);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-gray-800">My Items</h1>
          <button
            onClick={handleUploadItem}
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
          >
            Upload Item
          </button>
        </div>
        
        {/* Borrowed by Me Section */}
        <div className="mb-12">
          <h2 className="text-2xl font-semibold text-gray-700 mb-4 text-left">Borrowed by Me</h2>
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    Item Name
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    Owner
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    Due Date
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {borrowedItems.map((item) => (
                  <tr key={item.id}>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-left font-medium text-gray-900">
                      {item.name}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-left text-gray-500">
                      {item.owner}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-left text-gray-500">
                      {item.dueDate}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-left">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${
                        item.status === 'borrowed' 
                          ? 'bg-green-100 text-green-800' 
                          : 'bg-red-100 text-red-800'
                      }`}>
                        {item.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-left text-gray-500">
                      <button
                        onClick={() => handleReturnItem(item)}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        Return Item
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Lent by Me Section */}
        <div className="mb-12">
          <h2 className="text-2xl font-semibold text-gray-700 mb-4 text-left">Lent by Me</h2>
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    Item Name
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    Borrower
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    Lent Until
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                  <th scope="col" className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    Actions
                  </th>
                </tr>
              </thead>
              <tbody className="bg-white divide-y divide-gray-200">
                {lentItems.map((item) => (
                  <tr key={item.id}>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-left font-medium text-gray-900">
                      {item.name}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-left text-gray-500">
                      {item.borrower || '-'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-left text-gray-500">
                      {item.dueDate || '-'}
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-left">
                      <span className={`px-2 inline-flex text-xs leading-5 font-semibold rounded-full ${getStatusColor(item.status)}`}>
                        {item.status}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-left text-gray-500">
                      {item.status !== 'available' && (
                        <button
                          onClick={() => handleSendReminder(item)}
                          className="text-blue-600 hover:text-blue-900"
                        >
                          Send Reminder
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        {/* Upload Item Modal */}
        <UploadItemModal
          isOpen={isUploadModalOpen}
          onClose={handleCloseUploadModal}
          onConfirm={handleConfirmUpload}
        />

        {/* Return Item Confirmation Modal */}
        <ReturnItemModal
          isOpen={isReturnModalOpen}
          onClose={handleCloseReturnModal}
          onConfirm={handleConfirmReturn}
          itemName={selectedItem?.name}
        />

        {/* Send Reminder Confirmation Modal */}
        <ReminderModal
          isOpen={isReminderModalOpen}
          onClose={handleCloseReminderModal}
          onConfirm={handleConfirmReminder}
          itemName={selectedItem?.name}
          borrowerName={selectedItem?.borrower}
        />
      </div>
    </div>
  );
}

export default MyItems; 