import { login } from "../api/authenticationApi";
import useAuthStore from "../stores/useAuthStore";
import handleNotification from "./handleNotification";

export const handleLogin = async (loggingInformation) => {
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
    }
  }
  handleNotification("error", "login.failed");
  return false; // Failure case
};

export default handleLogin;