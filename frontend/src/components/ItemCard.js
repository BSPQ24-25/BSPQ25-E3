// src/components/ItemCard.jsx
import React, { useState } from 'react';
import BorrowModal from './BorrowModal';
import { useTranslation } from 'react-i18next';
import axiosInstance from '../axiosInstance';
import { toast } from 'react-toastify';

function ItemCard({ item, onClick, onLoanCreated }) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { t } = useTranslation();

  const handleBorrowButtonClick = (e) => {
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
        lender: item.lenderId,
        loanDate: formatDate(currentDate),
        estimatedReturnDate: endDate,
      };
      // For debugging
      //console.log(newLoanData);
      await axiosInstance.post('/loans/create', newLoanData, {
        headers: { 'Content-Type': 'application/json' },
      });
      toast.success(t('itemCard.loanCreated', '¡Préstamo creado correctamente!'));
      setIsModalOpen(false);

      if (onLoanCreated) {
        onLoanCreated(item.id);
      }
    } catch (error) {
      const errorText = error.response?.data || error.message;

      if (errorText.includes('penalty')) {
        toast.error(t('itemCard.penalty', 'No puedes pedir más préstamos mientras tengas penalizaciones.'));
      } else if (errorText.includes('3 items reserved')) {
        toast.error(
          t(
            'itemCard.tooManyLoans',
            'Tienes 3 préstamos activos. Devuelve uno antes de pedir otro.'
          )
        );
      } else if (errorText.includes('Lender not found')) {
        toast.error(t('itemCard.lenderNotFound', 'Prestador no encontrado. Revisa los detalles del ítem.'));
      } else if (errorText.includes('Borrower not found')) {
        toast.error(
          t('itemCard.borrowerNotFound', 'Usuario no encontrado. Por favor, inicia sesión de nuevo.')
        );
      } else if (errorText.includes('Item not found')) {
        toast.error(t('itemCard.itemNotFound', 'El ítem que intentas pedir no existe.'));
      } else {
        toast.error(`${t('itemCard.loanFailed', 'Error al crear el préstamo')}: ${errorText}`);
      }
    }
  };

  const handleCardClick = () => {
    if (onClick) onClick(item);
  };

  const formattedPurchaseDate = item.purchaseDate
    ? new Date(item.purchaseDate).toLocaleDateString()
    : 'N/A';

  const formattedPurchasePrice =
    typeof item.purchasePrice === 'number'
      ? `$${item.purchasePrice.toFixed(2)}`
      : 'N/A';

  return (
    <>
      <div
        onClick={handleCardClick}
        className="bg-white rounded-lg shadow-md overflow-hidden hover:shadow-lg transition-shadow duration-300 cursor-pointer"
      >
        <div className="h-48 w-full bg-gray-200">
          <img
            src={`${process.env.REACT_APP_API_BASE_URL}${item.imageUrl}`}
            alt={item.name}
            className="w-full h-full object-cover"
          />
        </div>
        <div className="p-4">
          <h3 className="text-lg font-semibold text-gray-800 mb-2">{item.name}</h3>
          <p className="text-gray-600 text-sm mb-4">{item.description}</p>

          <div className="flex items-center text-sm text-gray-500 mb-1 text-left">
            <span className="mr-2">{t('itemCard.category', 'Categoría')}:</span>
            <span className="font-medium">{item.category || 'N/A'}</span>
          </div>
          <div className="flex items-center text-sm text-gray-500 mb-1 text-left">
            <span className="mr-2">
              {t('itemCard.purchaseDate', 'Fecha de compra')}:
            </span>
            <span className="font-medium">{formattedPurchaseDate}</span>
          </div>
          <div className="flex items-center text-sm text-gray-500 mb-4 text-left">
            <span className="mr-2">
              {t('itemCard.purchasePrice', 'Precio de compra')}:
            </span>
            <span className="font-medium">{formattedPurchasePrice}</span>
          </div>

          <div className="flex items-center text-sm text-gray-500 mb-4">
            <span className="mr-2">{t('itemCard.lender', 'Prestador')}:</span>
            <span className="font-medium">{item.lenderName}</span>
          </div>

          <button
            onClick={handleBorrowButtonClick}
            className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition-colors duration-300"
          >
            {t('itemCard.borrow', 'Pedir prestado')}
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
