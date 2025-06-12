import { login } from "../api/authenticationApi";
import useAuthStore from "../stores/useAuthStore";

export const handleLogin = async (loggingInformation) => {
  const response = await login(loggingInformation); // Authenticate user (JWT stored in cookie)
  console.log(response);
  // Fetch user details after successful login
  if (response.success) {
    console.log("Login successful:", response);
    // Use getState() and await the async call.
    await useAuthStore.getState().fetchAndSetUserInformation();
    return true; // Success
  } else {
    return false; // Failure
  }
};
export default handleLogin;