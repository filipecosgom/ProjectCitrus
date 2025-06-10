import { login } from "../api/authenticationApi";
import { handleApiError } from "../api/api";
import { fetchUserInformation } from "../api/userApi";
import useAuthStore from "../stores/useAuthStore";

export const handleLogin = async (logginInformation, intl) => {
  console.log(logginInformation);
  const response = await login(logginInformation); // Authenticate user (JWT stored in cookie)
  // Fetch user details after successful login
  if (response.success) {
    const user = await fetchUserInformation();
    useAuthStore.setState({ user: user.data }); // Store user in Zustand
    return true; // Success
  } else {
    return false; // Failure
  }
};
export default handleLogin;
