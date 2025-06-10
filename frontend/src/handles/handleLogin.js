import { login } from "../api/authenticationApi";
import { handleApiError } from "../api/api";
import { fetchUserInformation } from "../api/userApi";
import handleNotification from "./handleNotification";
import useAuthStore from "../stores/useAuthStore";

export const handleLogin = async (user, intl) => {
  console.log(user);
  const response = await login(user); // Authenticate user (JWT stored in cookie)
  // Fetch user details after successful login
  //const response = await fetchUserInformation();
  //useAuthStore.setState({ user: response.data }); // Store user in Zustand
  if (response.success) {
    return true; // Success
  } else {
    handleApiError(response.error, intl); // Handle error here
    return false; // Failure
  }
};
export default handleLogin;
