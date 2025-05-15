import React, { useState, useEffect } from 'react';
import ItemCard from '../components/ItemCard';
import axiosInstance from '../axiosInstance';
import { useTranslation } from 'react-i18next';

// Dummy data for testing
const dummyItems = [
  {
    id: 1,
    name: "Professional Camera",
    description: "High-quality DSLR camera perfect for photography enthusiasts. Includes basic lens and carrying case.",
    imageUrl: "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
    lenderName: "John Smith",
    category: "Electronics",
    purchaseDate: "2022-01-15",
    purchasePrice: 1200.00,
  },
  {
    id: 2,
    name: "Camping Tent",
    description: "4-person tent in excellent condition. Waterproof and easy to set up. Perfect for weekend getaways.",
    imageUrl: "https://images.unsplash.com/photo-1504280390367-361c6e9f38f4?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
    lenderName: "Sarah Johnson",
    category: "Outdoor Gear",
    purchaseDate: "2021-07-20",
    purchasePrice: 150.00,
  },
  {
    id: 3,
    name: "Electric Drill",
    description: "Powerful cordless drill with multiple attachments. Includes charger and carrying case.",
    imageUrl: "https://images.unsplash.com/photo-1586985289688-ca3cf47d3b9e?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
    lenderName: "Mike Brown",
    category: "Tools",
    purchaseDate: "2023-02-10",
    purchasePrice: 75.00,
  },
  {
    id: 4,
    name: "Mountain Bike",
    description: "Well-maintained mountain bike suitable for trails and city riding. Includes helmet and lock.",
    imageUrl: "https://images.unsplash.com/photo-1485968579580-b6d095142e6e?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
    lenderName: "Emma Wilson",
    category: "Sports Equipment",
    purchaseDate: "2022-05-01",
    purchasePrice: 450.00,
  },
  {
    id: 5,
    name: "Projector",
    description: "HD projector with HDMI input. Perfect for movie nights or presentations.",
    imageUrl: "https://images.unsplash.com/photo-1581094794329-c8112c4e1b0d?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
    lenderName: "David Lee",
    category: "Electronics",
    purchaseDate: "2023-01-05",
    purchasePrice: 300.00,
  },
  {
    id: 6,
    name: "Gaming Console",
    description: "Latest gaming console with two controllers and several popular games.",
    imageUrl: "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
    lenderName: "Alex Turner",
    category: "Electronics",
    purchaseDate: "2023-03-15",
    purchasePrice: 500.00,
  }
];

function Home() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const { t } = useTranslation();

  useEffect(() => {
    const fetchItems = async () => {
      try {
        const response = await axiosInstance.get('/items/available');
        const availableItems = response.data;

        // Ahora, para cada item, traemos el owner
        const itemsWithOwnerNames = await Promise.all(
          availableItems.map(async (item) => {
            try {
              const ownerResponse = await axiosInstance.get(`/users/${item.owner}`);
              const ownerName = `${ownerResponse.data.username}`;
              
              return {
                id: item.id,
                name: item.name,
                description: item.description,
                category: item.category || 'N/A',
                purchaseDate: item.purchaseDate || null,
                purchasePrice: item.purchasePrice || 0,
                imageUrl: `https://images.unsplash.com/photo-1606813907291-d86efa9b94db?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60`, // o ajusta si necesitas
                lenderName: ownerName
              };
            } catch (ownerError) {
              console.error(`Error fetching owner for item ${item.id}:`, ownerError);
              return {
                id: item.id,
                name: item.name,
                description: item.description,
                category: item.category || 'N/A',
                purchaseDate: item.purchaseDate || null,
                purchasePrice: item.purchasePrice || 0,
                imageUrl: `https://images.unsplash.com/photo-1606813907291-d86efa9b94db?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60`,
                lenderName: 'Unknown'
              };
            }
          })
        );

        setItems(itemsWithOwnerNames);
      } catch (error) {
        console.error('Error fetching available items:', error);
        setError('Failed to load available items. Using dummy data for now.');
        // Fallback to dummy data if API fails, ensuring all fields are present
        const augmentedDummyItems = dummyItems.map(item => ({
          ...item,
          category: item.category || 'N/A', // Ensure dummy data also has fallbacks if not defined
          purchaseDate: item.purchaseDate || null,
          purchasePrice: item.purchasePrice || 0,
        }));
        setItems(augmentedDummyItems);
      } finally {
        setLoading(false);
      }
    };

    fetchItems();
  }, []);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-gray-700 text-lg">{t('home.loading')}</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-red-600 text-lg">{error}</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-8">{t('home.availableItems')}</h1>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {items.map(item => (
            <ItemCard key={item.id} item={item} />
          ))}
        </div>
      </div>
    </div>
  );
}

export default Home;