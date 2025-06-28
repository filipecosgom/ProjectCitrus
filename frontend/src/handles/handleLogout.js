import { logoutRequest } from "../api/authenticationApi";
import useAuthStore from "../stores/useAuthStore";
import handleNotification from "./handleNotification";

const handleLogout = async (navigate) => {
  const response = await logoutRequest();
  if (response?.success) {
    const userName = useAuthStore.getState().user?.name || "User";
    // Call Zustand logout to clear local state
    await useAuthStore.getState().logout();
    handleNotification("success", "goodByeMessage", {name: userName});
    navigate("/");
  }
  return response;
};

export default handleLogout;
