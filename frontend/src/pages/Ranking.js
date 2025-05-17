import React, { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import axios from 'axios';
import { FaStar, FaMedal } from 'react-icons/fa';

function Ranking() {
  const { t } = useTranslation();
  const [rankingData, setRankingData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const getMedalColor = (rank) => {
    if (rank === 1) return 'text-yellow-400';
    if (rank === 2) return 'text-gray-400';
    if (rank === 3) return 'text-yellow-600';
    return 'text-gray-500';
  };

  useEffect(() => {
    axios.get('http://localhost:8080/api/ranking', { withCredentials: true })
      .then(({ data }) => {
        // Asigna el campo "rank" en base al índice +1
        const withRank = data.map((u, i) => ({ ...u, rank: i + 1 }));
        setRankingData(withRank);
      })
      .catch(err => {
        console.error(err);
        setError(t('ranking.fetchError', 'Error cargando el ranking.'));
      })
      .finally(() => setLoading(false));
  }, [t]);

  if (loading) {
    return <p className="text-center mt-8">{t('ranking.loading', 'Cargando...')}</p>;
  }
  if (error) {
    return <p className="text-center mt-8 text-red-500">{error}</p>;
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8 px-4 sm:px-6 lg:px-8">
      <div className="container mx-auto max-w-3xl">
        <h1 className="text-4xl font-extrabold text-gray-800 text-center mb-12 tracking-tight">
          {t('ranking.title', 'User Rankings')}
        </h1>

        {rankingData.length > 0 ? (
          <div className="bg-white shadow-xl rounded-lg overflow-hidden">
            <ul className="divide-y divide-gray-200">
              {rankingData.map(user => (
                <li key={user.id} className={`p-4 sm:p-6 hover:bg-gray-100 transition-colors duration-150 flex items-center justify-between ${
                  user.rank <= 3 ? 'bg-gray-50' : ''
                }`}>
                  <div className="flex items-center space-x-4">
                    <div className={`text-3xl font-bold w-12 text-center ${getMedalColor(user.rank)}`}>
                      {user.rank <= 3 ? <FaMedal /> : user.rank}
                    </div>
                    <div className="min-w-0">
                      <p className="text-lg font-semibold text-gray-800 truncate">{user.name}</p>
                      <div className="flex items-center mt-1">
                        <FaStar className="text-yellow-500 mr-1" />
                        <p className="text-sm text-gray-600">
                          {t('ranking.rating', 'Rating:')} {user.averageRating.toFixed(1)}
                        </p>
                      </div>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-sm text-gray-500">{t('ranking.penalties', 'Penalties:')}</p>
                    <p className={`text-lg font-bold ${user.penalties > 0 ? 'text-red-500' : 'text-green-500'}`}>
                      {user.penalties}
                    </p>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        ) : (
          <div className="text-center mt-10 bg-white p-8 rounded-lg shadow-xl">
            <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path vectorEffect="non-scaling-stroke" strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 13h6m-3-3v6m-9 1V7a2 2 0 012-2h6l2 2h6a2 2 0 012 2v8a2 2 0 01-2 2H5a2 2 0 01-2-2z" />
            </svg>
            <h3 className="mt-2 text-sm font-medium text-gray-900">{t('ranking.noDataTitle', 'No Rankings Yet')}</h3>
            <p className="mt-1 text-sm text-gray-500">{t('ranking.noDataPlaceholder', 'Check back later to see the leaderboard!')}</p>
          </div>
        )}
      </div>
    </div>
  );
}

export default Ranking;
