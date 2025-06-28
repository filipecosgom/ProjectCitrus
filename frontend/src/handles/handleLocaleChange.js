import useLocaleStore from "../stores/useLocaleStore";

export const handleLocaleChange = (newLocale, setLanguage) => {
  setLanguage(newLocale); // Just use the passed setter
  // Add any additional logic here if needed
};

export default handleLocaleChange;