import { showSuccessToast, showErrorToast, showInfoToast } from "../utils/toastConfig/toastConfig";
import i18n from '../i18n';

// Accept t (from useTranslation) as an optional argument for fresh translations
const handleNotification = (type, messageId, params = {}) => {
  const t = i18n.t.bind(i18n);
  console.log(i18n.language, "Current language");

  const translatedMessage = t(messageId, params);
  console.log("Translated message:", translatedMessage);

  if (type === "success") {
    showSuccessToast(translatedMessage);
  } else if (type === "error") {
    showErrorToast(translatedMessage);
  } else if (type === "info") {
    showInfoToast(translatedMessage)
  }
};

export default handleNotification;