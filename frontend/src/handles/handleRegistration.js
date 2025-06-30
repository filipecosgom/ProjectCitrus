import { register } from "../api/userApi";
import { handleApiError } from "../api/api";

const handleRegistration = async (newUser, lang) => {
  const response = await register(newUser, lang);

  if (response.success) {
    return true; // Success
  } else {
    handleApiError(response.error); // Now uses i18n via handleNotification
    return false; // Failure
  }
};

export default handleRegistration;