import { requestPasswordReset } from "../api/authenticationApi";
import handleNotification from "./handleNotification";

const handleRequestPasswordReset = async (email, lang) => {
  console.log("email:", email);
  console.log("locale:", lang);

  const response = await requestPasswordReset(email, lang);
  console.log("API Response:", response); // Debugging log
  handleNotification("success", "Password reset request sent successfully");

  if (response.success) {
    return true; // Success
  } else {
    return false; // Failure
  }
};

export default handleRequestPasswordReset;
