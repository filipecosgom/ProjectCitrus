import { login } from "../api/authenticationApi";
import fetchUserInformation from '../api/userApi';
import handleNotification from "./handleNotification";
import useAuthStore from '../stores/useAuthStore';

export const handleLogin = async (email, password, intl) => {
  if (!email.trim() || !password.trim()) {
    handleNotification(intl, "error", "noPassword");
    return;
  }

  try {
    await login(email, password); // Authenticate user (JWT stored in cookie)

    // Fetch user details after successful login
    const response = await fetchUserInformation();
    useAuthStore.setState({ user: response.data }); // Store user in Zustand
  } catch (error) {
    handleNotification(intl, "error", error.message);
  }
};