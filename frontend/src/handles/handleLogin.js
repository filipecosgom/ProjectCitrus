import { login } from "../api/authenticationApi";
import useAuthStore from "../stores/useAuthStore";
import handleNotification from "./handleNotification";

export const handleLogin = async (loggingInformation) => {
  console.log("stsart handleLogin");
  let response = await login(loggingInformation); // Authenticate user (JWT stored in cookie)
  console.log(response)
  if (response.success) {
    // Fetch user details after successful login
    const response = await useAuthStore.getState().fetchAndSetUserInformation();
    console.log(response);
    if (response.success) {
      const userId = useAuthStore.getState().user?.id; // Get userId from store
      if (response.data.user.hasAvatar) {
        const response = useAuthStore.getState().fetchAndSetUserAvatar();
        if (response?.success) {
          handleNotification("success", "login.success");
          return true;
        }
        else {
          handleNotification("error", "login.avatarError");
          return false;
        }
      }
    }
  }
  handleNotification("error", "login.failed");
  return false; // Failure case
};

export default handleLogin