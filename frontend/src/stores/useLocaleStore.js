import { create } from 'zustand';

const LOCALE_KEY = 'locale';

const getInitialLocale = () => {
  const stored = localStorage.getItem(LOCALE_KEY);
  return stored || 'en';
};

// Store responsável pela gestão do idioma
export const useLocaleStore = create((set) => ({
  locale: getInitialLocale(),
  setLocale: (newLocale) => {
    localStorage.setItem(LOCALE_KEY, newLocale);
    set({ locale: newLocale });
  },
}));

export default useLocaleStore;