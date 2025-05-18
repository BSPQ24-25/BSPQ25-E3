import React, { useState } from 'react';
import { useTranslation } from 'react-i18next';
import { toast } from 'react-toastify';

function ReturnItemModal({ isOpen, onClose, onConfirm, itemName }) {
  const { t } = useTranslation();
  const [observations, setObservations] = useState('');
  
  if (!isOpen) return null;

  const handleConfirm = async () => {
    try {
      await onConfirm(observations);
      toast.success(
        t('returnModal.success', { item: itemName })
      );
      setObservations('');
      onClose();
    } catch (err) {
      console.error('Error al confirmar devolución:', err);
      toast.error(
        t('returnModal.error', { item: itemName })
      );
    }
  };

  return (
    <div className="fixed inset-0 bg-gray-500 bg-opacity-75 flex items-center justify-center z-50">
      <div className="bg-white rounded-lg p-6 max-w-md w-full mx-4">
        <h3 className="text-lg font-medium text-gray-900 mb-4 text-left">
          {t('returnModal.confirmReturn')}
        </h3>
        <p className="text-gray-500 mb-4 text-left">
          {t('returnModal.sureReturn')} {itemName}'?
        </p>

        <label htmlFor="observations" className="block text-sm font-medium text-gray-700 mb-1">
        </label>
        <textarea
          id="observations"
          value={observations}
          onChange={(e) => setObservations(e.target.value)}
          placeholder={t('returnModal.placeholder')}
          className="w-full border border-gray-300 rounded-md p-2 mb-6 focus:outline-none focus:ring-2 focus:ring-blue-500"
          rows={4}
        />

        <div className="flex justify-end space-x-3">
          <button
            onClick={() => { setObservations(''); onClose(); }}
            className="px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-md hover:bg-gray-50 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
          >
            {t('returnModal.cancel')}
          </button>
          <button
            onClick={handleConfirm}
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
