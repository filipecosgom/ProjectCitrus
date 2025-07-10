import { login } from "../api/authenticationApi";
import useAuthStore from "../stores/useAuthStore";
import handleNotification from "./handleNotification";

export const handleLogin = async (loggingInformation) => {
  try {
    let loginResponse = await login(loggingInformation); // Authenticate user (JWT stored in cookie)
    if (loginResponse.success) {
      // Extract sessionDuration (in minutes) from login response
      const sessionDurationMinutes = loginResponse.data?.sessionDuration;
      const sessionDurationMs = sessionDurationMinutes ? sessionDurationMinutes * 60 * 1000 : null;

      // Fetch user details after successful login
      const userInfoResponse = await useAuthStore.getState().fetchAndSetUserInformation();
      if (userInfoResponse.success) {
        // Set session duration in store if available
        if (sessionDurationMs) {
          useAuthStore.setState({ sessionDuration: sessionDurationMs });
        }
        if (userInfoResponse.data.user.hasAvatar) {
          const avatarResponse = await useAuthStore.getState().fetchAndSetUserAvatar();
          if (avatarResponse?.success) {
            handleNotification("success", "login.success");
            return true;
          } else {
            handleNotification("error", "login.avatarError");
            return false;
          }
        }
        handleNotification("success", "login.success");
        return true;
      }
    }
    // If loginResponse.success is false, handle error
    let errorKey = null;
    const errorMsg = loginResponse.message || loginResponse.errorCode || loginResponse.error;
    if (errorMsg) {
      if (errorMsg.includes("inactive")) errorKey = "errorAccountInactive";
      else if (errorMsg.includes("excluded")) errorKey = "errorAccountExcluded";
      else if (errorMsg.includes("forbidden")) errorKey = "errorForbidden";
      else if (errorMsg.includes("not found")) errorKey = "errorUserNotFound";
      else if (errorMsg.includes("invalid credentials") || errorMsg.includes("wrong username") || errorMsg.includes("incorrect")) errorKey = "errorWrongUsernamePassword";
      // Add more mappings as needed
    }
    handleNotification("error", errorKey || "login.failed");
    return false; // Failure case
  } catch (error) {
    handleNotification("error", "login.failed");
    return false;
  }
};

export default handleLogin;