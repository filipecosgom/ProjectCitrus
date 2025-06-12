import { login } from "../api/authenticationApi";
import useAuthStore from "../stores/useAuthStore";

export const handleLogin = async (logginInformation) => {
  console.log(logginInformation);
  const response = await login(logginInformation); // Authenticate user (JWT stored in cookie)
  // Fetch user details after successful login
  if (response.success) {
    useAuthStore.get().fetchAndSetUserInformation();
    return true; // Success
  } else {
    return false; // Failure
  }
};
export default handleLogin;
