import { requestPasswordReset } from "../api/authenticationApi";
import handleNotification from "./handleNotification";

const handleRequestPasswordReset = async (email, lang) => {
  await requestPasswordReset(email, lang);
  handleNotification("success", "passwordResetRequestSuccess");
};

export default handleRequestPasswordReset;
