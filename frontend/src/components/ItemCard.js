import React from 'react';

function ItemCard({ item }) {
  return (
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
        <div className="flex items-center text-sm text-gray-500 mb-4">
          <span className="mr-2">Lender:</span>
          <span className="font-medium">{item.lenderName}</span>
        </div>
        <button className="w-full bg-blue-600 text-white py-2 px-4 rounded-md hover:bg-blue-700 transition-colors duration-300">
          Borrow
        </button>
      </div>
    </div>
  );
}

export default ItemCard; 