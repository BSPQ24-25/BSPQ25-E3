import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axiosInstance from '../axiosInstance';

function Home() {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchItems = async () => {
      try {
        const res = await axiosInstance.get('/items'); // Ruta protegida
        setItems(res.data);
      } catch (error) {
        console.error('Error al cargar los ítems:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchItems();
  }, []);

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  return (
    <div style={{ padding: '2rem' }}>
      <h1>Bienvenido al sistema de préstamo de material escolar</h1>
      <button onClick={handleLogout}>Cerrar sesión</button>

      {loading ? (
        <p>Cargando materiales...</p>
      ) : (
        <>
          <h2>Material disponible:</h2>
          {items.length === 0 ? (
            <p>No hay materiales disponibles.</p>
          ) : (
            <ul>
              {items.map(item => (
                <li key={item.id}>
                  {item.name} - {item.description}
                </li>
              ))}
            </ul>
          )}
        </>
      )}
    </div>
  );
}

export default Home;
