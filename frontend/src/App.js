import i18n from "./i18n";
import { I18nextProvider } from "react-i18next";
import useLocaleStore from "./stores/useLocaleStore";
import { BrowserRouter as Router } from "react-router-dom";
import AppRoutes from "./AppRoutes";
import "./App.css";
// App.js
function App() {
  const locale = useLocaleStore((state) => state.locale);
  const setLocale = useLocaleStore((state) => state.setLocale);
  const storedLang = localStorage.getItem("locale");
  if (storedLang && i18n.language !== storedLang) {
    i18n.changeLanguage(storedLang);
  }

  return (
    <I18nextProvider i18n={require("./i18n").default}>
      <Router>
        <AppRoutes currentLocale={locale} setLocale={setLocale} />
      </Router>
    </I18nextProvider>
  );
}

export default App;
