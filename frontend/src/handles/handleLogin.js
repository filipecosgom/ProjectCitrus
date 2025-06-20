import { login } from "../api/authenticationApi";
import useAuthStore from "../stores/useAuthStore";

export const handleLogin = async (loggingInformation) => {
  console.log("stsart handleLogin");
  let response = await login(loggingInformation); // Authenticate user (JWT stored in cookie)
  console.log(response)
  if (response.success) {
    // Fetch user details after successful login
    const userResponse = await useAuthStore.getState().fetchAndSetUserInformation();
    console.log(userResponse);
    if (userResponse.success) {
      const userId = useAuthStore.getState().user?.id; // Get userId from store
      console.log(userId);
      if (userId) {
        const response = useAuthStore.getState().fetchAndSetUserAvatar();
        if (response?.success) {
          return true;
        }
        else {
          return false;
        }
      }
    }
  }
  return false; // Failure case
};

export default handleLogin