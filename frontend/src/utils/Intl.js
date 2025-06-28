// intlInstance.js
import { createIntl, createIntlCache } from 'react-intl';
import useLocaleStore from '../stores/useLocaleStore';
import languages from './translations/index';

const cache = createIntlCache();
let intlInstance;

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

// Initialize on first load
updateIntl(useLocaleStore.getState().locale);

// Export functions
export const getIntl = () => intlInstance;
export const getCurrentLocale = () => useLocaleStore.getState().locale;

// Subscription management
let unsubscribe;

export const setupIntlSubscription = () => {
  if (!unsubscribe) {
    unsubscribe = useLocaleStore.subscribe(
      (state) => state.locale,
      (newLocale) => {
        console.log("Changing locale to:", newLocale);
        updateIntl(newLocale);
      }
    );
  }
};

export const cleanupIntlSubscription = () => {
  if (unsubscribe) {
    unsubscribe();
    unsubscribe = null;
  }
};