import React from 'react';
import { useTranslation } from 'react-i18next';

const LanguageSelector = () => {
  const { i18n } = useTranslation();

  const languages = [
    { code: 'en', label: 'EN' },
    { code: 'es', label: 'ES' },
    { code: 'de', label: 'DE' }
  ];

  const changeLanguage = (lng) => {
    i18n.changeLanguage(lng);
  };

  return (
    <div className="flex justify-end gap-2 p-2">
      {languages.map((lang) => (
        <button
          key={lang.code}
          onClick={() => changeLanguage(lang.code)}
          className="px-2 py-1 text-sm rounded bg-gray-200 hover:bg-gray-300"
        >
          {lang.label}
        </button>
      ))}
    </div>
  );
};

export default LanguageSelector;