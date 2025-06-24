import { IntlProvider } from "react-intl";
import languages from "./utils/translations";
import useLocaleStore from "./stores/useLocaleStore";
import { BrowserRouter as Router } from "react-router-dom";
import AppRoutes from "./AppRoutes";
import "./App.css";

function App() {
  const locale = useLocaleStore((state) => state.locale);

  return (
    <IntlProvider locale={locale} messages={languages[locale]}>
      <Router>
        <AppRoutes />
      </Router>
    </IntlProvider>
  );
}

export default App;
