import { create } from 'zustand';

const LOCALE_KEY = 'locale';

const getInitialLocale = () => {
  const stored = localStorage.getItem(LOCALE_KEY);
  if (stored && stored.trim() !== '' && stored !== 'null') {
    return stored;
  }
  // If no valid value exists, store and return the default locale 'en'
  localStorage.setItem(LOCALE_KEY, 'en');
  return 'en';
};

// Store responsável pela gestão do idioma
export const useLocaleStore = create((set) => ({
  locale: getInitialLocale(),
  setLocale: (newLocale) => {
    const validLocale = newLocale || 'en';
    localStorage.setItem(LOCALE_KEY, validLocale);
    set({ locale: validLocale });
  },
}));



export default useLocaleStore;