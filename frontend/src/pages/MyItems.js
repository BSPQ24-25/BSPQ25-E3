import React, { useState, useEffect } from 'react';
import axiosInstance from '../axiosInstance';
import ReturnItemModal from '../components/ReturnItemModal';
import ReminderModal from '../components/ReminderModal';
import UploadItemModal from '../components/UploadItemModal';
import { useTranslation } from 'react-i18next';

function MyItems() {
  const { t } = useTranslation();

  const [borrowedItems, setBorrowedItems] = useState([]);
  const [lentItems, setLentItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [selectedItem, setSelectedItem] = useState(null);
  const [isReturnModalOpen, setIsReturnModalOpen] = useState(false);
  const [isReminderModalOpen, setIsReminderModalOpen] = useState(false);
  const [isUploadModalOpen, setIsUploadModalOpen] = useState(false);

  const fetchItems = async () => {
    try {
      const [borrowedResponse, lentResponse, loansBorrowerResponse, loansLenderResponse] = await Promise.all([
        axiosInstance.get('/items/borrowed'),
        axiosInstance.get('/items/lent'),
        axiosInstance.get('/loans/borrower'),
        axiosInstance.get('/loans/lender')
      ]);

      const borrowed = borrowedResponse.data.map((item) => ({
        id: item.id,
        name: item.name,
        owner: item.owner,
        dueDate: item.dueDate || '-',
        status: item.status.toLowerCase(),
      }));

      const lentRaw = lentResponse.data.map(item => ({
        id: item.id,
        name: item.name,
        borrower: item.borrower || '-',
        dueDate: item.dueDate || '-',
        status: item.status.toLowerCase(),
      }));
      const lent = lentRaw.filter((item, index, self) =>
        index === self.findIndex(t => t.id === item.id)
      );

      const loansLender = loansLenderResponse.data.map(loan => ({
        id: loan.id,
        dueDate: loan.estimatedReturnDate || '-',
        itemId: loan.item,
        borrower: loan.borrower || '-',
        status: loan.loanStatus.toLowerCase(),
      }));
      const lendWithDueDate = lent
        .map(item => {
          if (item.status === 'available') {
            return { ...item, dueDate: '-', borrower: '-' };
          }
          const loan = loansLender.find(l => l.itemId === item.id && l.status === 'in_use');
          return loan ? { ...item, dueDate: loan.dueDate, borrower: loan.borrower } : null;
        })
        .filter(item => item);

      const loans = loansBorrowerResponse.data.map(loan => ({
        id: loan.id,
        dueDate: loan.estimatedReturnDate || '-',
        itemId: loan.item,
        status: loan.loanStatus.toLowerCase(),
      }));
      const borrowedWithDueDate = borrowed
        .map(item => {
          const loan = loans.find(l => l.itemId === item.id && l.status === 'in_use');
          return loan ? { ...item, dueDate: loan.dueDate } : null;
        })
        .filter(item => item);

      setBorrowedItems(borrowedWithDueDate);
      setLentItems(lendWithDueDate);
    } catch (err) {
      console.error('Error fetching items:', err);
      setError('Failed to load items');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchItems();
  }, []);

  const handleReturnItem = (item) => {
    setSelectedItem(item);
    setIsReturnModalOpen(true);
  };

  /**
   * Ahora recibe 'observations' desde ReturnItemModal
   * Envía PUT a /loans/{id}/return con el body { observations }
   */
  const handleConfirmReturn = async (observations) => {
    try {
      const response = await axiosInstance.put(
        `/loans/${selectedItem.id}/return`,
        { observations }
      );
      if (response.status === 200) {
        console.log('Item returned successfully:', selectedItem.id, observations);
        fetchItems();
      } else {
        console.error('Failed to return item');
      }
    } catch (error) {
      console.error('Error returning item:', error);
    } finally {
      setIsReturnModalOpen(false);
      setSelectedItem(null);
    }
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
      case 'borrowed': return 'bg-green-100 text-green-800';
      case 'overdue':  return 'bg-red-100 text-red-800';
      case 'available':return 'bg-blue-100 text-blue-800';
      default:         return 'bg-gray-100 text-gray-800';
    }
  };

  const handleUploadItem = () => setIsUploadModalOpen(true);
  const handleCloseUploadModal = () => setIsUploadModalOpen(false);
  const handleConfirmUpload = async (formData) => {
    const toBase64 = file => new Promise((res, rej) => {
      const reader = new FileReader();
      reader.readAsDataURL(file);
      reader.onload = () => res(reader.result);
      reader.onerror = rej;
    });
    const base64Image = formData.image ? await toBase64(formData.image) : null;
    try {
      const adaptedData = {
        name: formData.name,
        description: formData.description,
        category: formData.category || 'Misc',
        purchaseDate: formData.purchaseDate,
        purchasePrice: formData.purchasePrice,
        imageBase64: base64Image,
        status: 'available',
        condition: 'NEW',
      };
      await axiosInstance.post('/items/create', adaptedData);
      console.log('Item uploaded successfully!');
      setIsUploadModalOpen(false);
    } catch (error) {
      console.error('Failed to upload item:', error.response?.data || error.message);
    }
  };

  if (loading) return <div>{t('loading')}</div>;
  if (error)   return <div className="text-red-600">{error}</div>;

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <div className="flex justify-between items-center mb-8">
          <h1 className="text-3xl font-bold text-gray-800">{t('myItems.title')}</h1>
          <button
            onClick={handleUploadItem}
            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
          >
            {t('myItems.uploadButton')}
          </button>
        </div>

        {/* Borrowed by Me Section */}
        <div className="mb-12">
          <h2 className="text-2xl font-semibold text-gray-700 mb-4 text-left">{t('myItems.borrowedSection')}</h2>
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    {t('myItems.table.itemName')}
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    {t('myItems.table.owner')}
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    {t('myItems.table.dueDate')}
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    {t('myItems.table.status')}
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    {t('myItems.table.actions')}
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
                        {t(`myItems.status.${item.status}`)}
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-sm text-left text-gray-500">
                      <button
                        onClick={() => handleReturnItem(item)}
                        className="text-blue-600 hover:text-blue-900"
                      >
                        {t('myItems.actions.return')}
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
          <h2 className="text-2xl font-semibold text-gray-700 mb-4 text-left">{t('myItems.lentSection')}</h2>
          <div className="bg-white rounded-lg shadow overflow-hidden">
            <table className="min-w-full divide-y divide-gray-200">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    {t('myItems.table.itemName')}
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    {t('myItems.table.borrower')}
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    {t('myItems.table.lentUntil')}
                  </th>
                  <th className="px-6 py-3 text-left text-xs font-bold text-gray-500 uppercase tracking-wider">
                    {t('myItems.table.status')}
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
                        {t(`myItems.status.${item.status}`)}
                      </span>
                    </td>
            
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <UploadItemModal
          isOpen={isUploadModalOpen}
          onClose={handleCloseUploadModal}
          onConfirm={handleConfirmUpload}
        />

        <ReturnItemModal
          isOpen={isReturnModalOpen}
          onClose={handleCloseReturnModal}
          onConfirm={handleConfirmReturn}
          itemName={selectedItem?.name}
        />

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