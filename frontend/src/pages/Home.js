import React from 'react';
import ItemCard from '../components/ItemCard';

// Dummy data for testing
const dummyItems = [
  {
    id: 1,
    name: "Professional Camera",
    description: "High-quality DSLR camera perfect for photography enthusiasts. Includes basic lens and carrying case.",
    imageUrl: "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
    lenderName: "John Smith"
  },
  {
    id: 2,
    name: "Camping Tent",
    description: "4-person tent in excellent condition. Waterproof and easy to set up. Perfect for weekend getaways.",
    imageUrl: "https://images.unsplash.com/photo-1504280390367-361c6e9f38f4?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
    lenderName: "Sarah Johnson"
  },
  {
    id: 3,
    name: "Electric Drill",
    description: "Powerful cordless drill with multiple attachments. Includes charger and carrying case.",
    imageUrl: "https://images.unsplash.com/photo-1586985289688-ca3cf47d3b9e?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
    lenderName: "Mike Brown"
  },
  {
    id: 4,
    name: "Mountain Bike",
    description: "Well-maintained mountain bike suitable for trails and city riding. Includes helmet and lock.",
    imageUrl: "https://images.unsplash.com/photo-1485968579580-b6d095142e6e?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
    lenderName: "Emma Wilson"
  },
  {
    id: 5,
    name: "Projector",
    description: "HD projector with HDMI input. Perfect for movie nights or presentations.",
    imageUrl: "https://images.unsplash.com/photo-1581094794329-c8112c4e1b0d?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
    lenderName: "David Lee"
  },
  {
    id: 6,
    name: "Gaming Console",
    description: "Latest gaming console with two controllers and several popular games.",
    imageUrl: "https://images.unsplash.com/photo-1606813907291-d86efa9b94db?ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60",
    lenderName: "Alex Turner"
  }
];

function Home() {
  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-gray-800 mb-8">Available Items</h1>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {dummyItems.map(item => (
            <ItemCard key={item.id} item={item} />
          ))}
        </div>
      </div>
    </div>
  );
}

export default Home; 