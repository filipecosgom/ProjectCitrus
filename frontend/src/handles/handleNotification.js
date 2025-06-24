
import { showSuccessToast, showErrorToast, showInfoToast } from "../utils/toastConfig/toastConfig";
import { getIntl } from "../utils/Intl";

const handleNotification = (type, messageId, params = {}) => {
  const intl = getIntl();

  const translatedMessage = intl.formatMessage({ id: messageId }, params);
  console.log(intl);
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