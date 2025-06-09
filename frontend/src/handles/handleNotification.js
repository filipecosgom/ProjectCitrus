import { showSuccessToast, showErrorToast, showInfoToast } from "../utils/toastConfig/toastConfig";

const handleNotification = (intl, type, messageId, params = {}) => {
  console.log(messageId)

  const translatedMessage = intl.formatMessage({ id: messageId }, params);
  console.log(translatedMessage);

  if (type === "success") {
    showSuccessToast(translatedMessage);
  } else if (type === "error") {
    showErrorToast(translatedMessage);
  } else if (type === "info") {
    showInfoToast(translatedMessage)
  }
};

export default handleNotification;