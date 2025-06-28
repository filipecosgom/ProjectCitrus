import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import en from './utils/translations/en';
import pt from './utils/translations/pt';

const resources = {
  en: { translation: en },
  pt: { translation: pt },
};

i18n
  .use(initReactI18next)
  .init({
    resources,
    lng: 'en', // default language
    fallbackLng: 'en',
    interpolation: {
      escapeValue: false, // react already safes from xss
    },
    react: {
      useSuspense: false,
    },
  });

export default i18n;
