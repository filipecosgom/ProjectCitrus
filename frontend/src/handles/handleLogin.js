import { login } from "../api/authenticationApi";
import { handleApiError } from "../api/api";
import { fetchUserInformation } from "../api/userApi";
import useAuthStore from "../stores/useAuthStore";

export const handleLogin = async (logginInformation, intl) => {
  console.log(logginInformation);
  const response = await login(logginInformation); // Authenticate user (JWT stored in cookie)
  // Fetch user details after successful login
  if (response.success) {
    let user = await fetchUserInformation();
    user = user.data.data;
    console.log("userdata");
    console.log(user);
    useAuthStore.setState({ user }); // Store user in Zustand
    console.log(useAuthStore.getState().user);
    return true; // Success
  } else {
    return false; // Failure
  }
};
export default handleLogin;
