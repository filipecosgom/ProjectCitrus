import { createContext, useContext } from "react";
import { useIntl } from "react-intl";

const IntlContext = createContext(null);
let globalIntl = null; // Global storage

export const IntlProvider = ({ children }) => {
  const intl = useIntl();
  globalIntl = intl; // Store intl globally

  return <IntlContext.Provider value={intl}>{children}</IntlContext.Provider>;
};

export const useGlobalIntl = () => {
  return useContext(IntlContext);
};

export const getGlobalIntl = () => globalIntl; // Getter for Axios interceptor