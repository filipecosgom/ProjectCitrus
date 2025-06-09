import { createIntl, createIntlCache } from 'react-intl';
import useLocaleStore from '../stores/useLocaleStore';
import languages from './translations/index'; // Your existing index file

const cache = createIntlCache();
let intlInstance;

// Initialize or update intl instance
const updateIntl = (locale) => {
  intlInstance = createIntl(
    {
      locale,
      messages: languages[locale],
      defaultLocale: 'en',
      onError: (error) => {
        if (error.code === 'MISSING_TRANSLATION') {
          console.warn('Missing translation:', error.message);
          return;
        }
        throw error;
      }
    },
    cache
  );
};

// Get current intl instance
export const getIntl = () => {
  if (!intlInstance) {
    const currentLocale = useLocaleStore.getState().locale;
    updateIntl(currentLocale);
  }
  return intlInstance;
};

// Subscribe to locale changes
useLocaleStore.subscribe(
  (state) => state.locale,
  (newLocale) => updateIntl(newLocale)
);