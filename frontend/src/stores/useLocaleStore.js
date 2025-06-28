import { create } from 'zustand';
import i18n from '../i18n';

const LOCALE_KEY = 'locale';

const useLocaleStore = create((set) => ({
  locale: localStorage.getItem(LOCALE_KEY) || 'en',
  setLocale: (newLocale) => {
    const validLocale = newLocale || 'en';
    localStorage.setItem(LOCALE_KEY, validLocale);
    i18n.changeLanguage(validLocale); // Notify i18next of language change
    set({ locale: validLocale }, false, { type: "locale/changed" }); // Add action type
  }
}));
export default useLocaleStore;