import { register } from "../api/userApi";
import handleNotification from "./handleNotification";

const handleRegistration = async (newUser, lang) => {
  const response = await register(newUser, lang);

  if (response.success) {
    return true; // Success
  } else {
    // Map backend error codes/messages to translation keys
    let errorKey = null;
    const errorMsg = response.message || response.errorCode || response.error;
    if (errorMsg) {
      if (errorMsg.includes("duplicate") || errorMsg.includes("already exists")) errorKey = "errorDuplicateEntry";
      else if (errorMsg.includes("invalid data")) errorKey = "errorInvalidData";
      else if (errorMsg.includes("server")) errorKey = "errorServerIssue";
      // Add more mappings as needed
    }
    handleNotification("error", errorKey || "registerError");
    return false; // Failure
  }
};

export default handleRegistration;