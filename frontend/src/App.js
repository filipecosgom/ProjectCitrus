import { IntlProvider } from "react-intl";
import languages from "./utils/translations";
import useLocaleStore from "./stores/useLocaleStore";
import { BrowserRouter as Router } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import AppRoutes from "./AppRoutes";
import "./App.css";

function App() {
  const locale = useLocaleStore((state) => state.locale);

  return (
    <IntlProvider locale={locale} messages={languages[locale]}>
      <Router>
        <AppRoutes />
        <ToastContainer limit={3} />
      </Router>
    </IntlProvider>
  );
}

export default App;
