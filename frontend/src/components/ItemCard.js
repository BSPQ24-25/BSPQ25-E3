import React, { useState } from 'react';
import BorrowModal from './BorrowModal';
import { useTranslation } from 'react-i18next';
import axiosInstance from '../axiosInstance';

function ItemCard({ item, onClick, onLoanCreated }) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { t } = useTranslation();

  const handleBorrowButtonClick = (e) => {
    console.log('Item recibido en ItemCard:', item);
    e.stopPropagation();
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleConfirmBorrow = async (endDate) => {
    try {

      const pad = (n) => n.toString().padStart(2, '0');
      const formatDate = (date) => {
        const d = new Date(date);
        return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
      };

      const currentDate = new Date();

      const newLoanData = {
        item: item.id,
        lender: item.lenderId,  // <- acá debe ser lenderId
        loanDate: formatDate(currentDate),
        estimatedReturnDate: endDate,
      };

      console.log(newLoanData);
      await axiosInstance.post('/loans/create', newLoanData, {
        headers: {
          'Content-Type': 'application/json',
        },
      });

      alert('Loan created successfully!');
      setIsModalOpen(false);

      // We warn Home that this item has been borrowed and to remove it from the view
      if (onLoanCreated) {
        onLoanCreated(item.id);
      }

    } catch (error) {
        // error.response.data puede contener el mensaje del backend
        const errorText = error.response?.data || error.message;

        if (errorText.includes('penalty')) {
          alert('You cannot borrow items while under penalty.');
        } else if (errorText.includes('3 items reserved')) {
          alert('You already have 3 active loans. Please return one before borrowing another.');
        } else if (errorText.includes('Lender not found')) {
          alert('Lender not found. Please check the item details.');
        } else if (errorText.includes('Borrower not found')) {
          alert('Your user data could not be found. Please log in again.');
        } else if (errorText.includes('Item not found')) {
          alert('The item you are trying to borrow does not exist.');
        } else {
          alert(`Failed to create loan: ${errorText}`);
        }

        return;
    }
  };


  const handleCardClick = () => {
    if (onClick) {
      onClick(item);
    }
  };

  const formattedPurchaseDate = item.purchaseDate 
    ? new Date(item.purchaseDate).toLocaleDateString() 
    : 'N/A';
  
  const formattedPurchasePrice = typeof item.purchasePrice === 'number' 
    ? `$${item.purchasePrice.toFixed(2)}`
    : 'N/A';

  return (
    <>
      <div className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300">
        <div className="h-48 w-full bg-gray-200">
          <img 
            src={item.imageUrl} 
            alt={item.name}
            className="w-full h-full object-cover"
          />
        </div>
        <div className="p-4">
          <h3 className="text-lg font-semibold text-gray-800 mb-2">{item.name}</h3>
          <p className="text-gray-600 text-sm mb-4">{item.description}</p>
          
          <div className="flex items-center text-sm text-gray-500 mb-1 text-left">
            <span className="mr-2">{t('itemCard.category', 'Category')}:</span> 
            <span className="font-medium">{item.category || 'N/A'}</span>
          </div>
          <div className="flex items-center text-sm text-gray-500 mb-1 text-left">
            <span className="mr-2">{t('itemCard.purchaseDate', 'Purchase Date')}:</span> 
            <span className="font-medium">{formattedPurchaseDate}</span>
          </div>
          <div className="flex items-center text-sm text-gray-500 mb-4 text-left">
            <span className="mr-2">{t('itemCard.purchasePrice', 'Purchase Price')}:</span> 
            <span className="font-medium">{formattedPurchasePrice}</span>
          </div>

          <div className="flex items-center text-sm text-gray-500 mb-4">
            <span className="mr-2">{t('itemCard.lender')}:</span>
            <span className="font-medium">{item.lenderName}</span>
          </div>
          <button 
            onClick={handleBorrowButtonClick}
            className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition-colors duration-300"
          >
            {t('itemCard.borrow')}
          </button>
        </div>
      </div>
      <BorrowModal
        isOpen={isModalOpen}
        onClose={handleCloseModal}
        onConfirm={handleConfirmBorrow}
        itemName={item.name}
      />
    </>
  );
}

export default ItemCard; 