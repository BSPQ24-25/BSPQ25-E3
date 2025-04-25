import React, { useState } from 'react';
import ReturnItemModal from '../components/ReturnItemModal';

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

  const [selectedItem, setSelectedItem] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleReturnItem = (item) => {
    setSelectedItem(item);
    setIsModalOpen(true);
  };

  const handleConfirmReturn = () => {
    // TODO: Implement return item functionality
    console.log('Returning item:', selectedItem.id);
    setIsModalOpen(false);
    setSelectedItem(null);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setSelectedItem(null);
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-8">My Items</h1>
        
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

        {/* My Listed Items Section */}
        <div>
          <h2 className="text-2xl font-semibold text-gray-700 mb-4">My Listed Items</h2>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {/* Items will be displayed here */}
          </div>
        </div>

        {/* Return Item Confirmation Modal */}
        <ReturnItemModal
          isOpen={isModalOpen}
          onClose={handleCloseModal}
          onConfirm={handleConfirmReturn}
          itemName={selectedItem?.name}
        />
      </div>
    </div>
  );
}

export default MyItems; 