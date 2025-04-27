import React from 'react';
import { useTranslation } from 'react-i18next'; // <-- añadido para traducción

function ReturnItemModal({ isOpen, onClose, onConfirm, itemName }) {
  const { t } = useTranslation(); // <-- añadido para traducción
  
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
        <h3 className="text-lg font-medium text-gray-900 mb-4 text-left">
        {t('returnModal.confirmReturn')}
        </h3>
        <p className="text-gray-500 mb-6 text-left">
          {t('returnModal.sureReturn')}{itemName}'?
        </p>
        <div className="flex justify-end space-x-3">
          <button
            onClick={onClose}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            {t('returnModal.cancel')}
          </button>
          <button
            onClick={onConfirm}
            className="px-4 py-2 text-sm font-medium text-white bg-blue-600 border border-transparent rounded-md hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            {t('returnModal.confirmReturn')}
          </button>
        </div>
      </div>
    </div>
  );
}

export default ReturnItemModal; 