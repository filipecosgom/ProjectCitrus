import { showSuccessToast, showErrorToast, showInfoToast } from "../utils/toastConfig/toastConfig";

const handleNotification = (intl, type, messageId, params = {}) => {

  const translatedMessage = intl.formatMessage({ id: messageId }, params);

  if (type === "success") {
    showSuccessToast(translatedMessage);
  } else if (type === "error") {
    showErrorToast(translatedMessage);
  } else if (type === "info") {
    showInfoToast(translatedMessage)
  }
};

export default handleNotification;