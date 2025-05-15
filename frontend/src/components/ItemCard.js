import React, { useState } from 'react';
import BorrowModal from './BorrowModal';
import { useTranslation } from 'react-i18next';

function ItemCard({ item, onClick }) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { t } = useTranslation();

  const handleBorrowButtonClick = (e) => {
    e.stopPropagation();
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleConfirmBorrow = (endDate) => {
    console.log(`Borrowing ${item.name} until ${endDate}`);
    setIsModalOpen(false);
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