import React, { useState, useEffect } from 'react';
import ItemCard from '../components/ItemCard';
import axiosInstance from '../axiosInstance';
import { useTranslation } from 'react-i18next';

function Home() {
  const { t } = useTranslation();
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchItems = async () => {
      try {
        // Obtener el ID del usuario autenticado desde /users/me
        let currentUserId = null;
        try {
          const meResponse = await axiosInstance.get('/users/me');
          currentUserId = meResponse.data.id;
        } catch (meError) {
          console.warn('No se pudo obtener /users/me, se mostrarán todos los ítems', meError);
        }

        // Obtener ítems disponibles
        const response = await axiosInstance.get('/items/available');
        const availableItems = response.data;

        // Filtrar propios si tenemos currentUserId
        const filteredItems = currentUserId
          ? availableItems.filter(item => item.owner !== currentUserId)
          : availableItems;

        // Mapear con nombre del prestador
        const itemsWithOwnerNames = await Promise.all(
          filteredItems.map(async item => {
            try {
              const ownerRes = await axiosInstance.get(`/users/${item.owner}`);
              return {
                id: item.id,
                name: item.name,
                description: item.description,
                category: item.category || 'N/A',
                purchaseDate: item.purchaseDate || null,
                purchasePrice: item.purchasePrice || 0,
                imageUrl: item.image,
                lenderId: item.owner,
                lenderName: ownerRes.data.username,
              };
            } catch {
              return {
                id: item.id,
                name: item.name,
                description: item.description,
                category: item.category || 'N/A',
                purchaseDate: item.purchaseDate || null,
                purchasePrice: item.purchasePrice || 0,
                imageUrl: item.image,
                lenderId: item.owner,
                lenderName: 'Unknown',
              };
            }
          })
        );

        setItems(itemsWithOwnerNames);
      } catch (err) {
        console.error('Error fetching available items:', err);
        setError(t('home.errorLoading'));
      } finally {
        setLoading(false);
      }
    };

    fetchItems();
  }, [t]);

  if (loading) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-gray-700 text-lg">{t('home.loading')}</div>
    </div>
  );

  if (error) return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-red-600 text-lg">{error}</div>
    </div>
  );

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-8">
          {t('home.availableItems')}
        </h1>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {items.map(item => (
            <ItemCard
              key={item.id}
              item={item}
              onLoanCreated={() => setItems(prev => prev.filter(i => i.id !== item.id))}
            />
          ))}
        </div>
      </div>
    </div>
  );
}

export default Home;