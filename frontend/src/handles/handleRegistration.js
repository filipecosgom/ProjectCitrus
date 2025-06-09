import { register } from "../api/userApi";
import { handleApiError } from "../api/api";

const handleRegistration = async (newUser, intl) => {
  const response = await register(newUser);

  if (response.success) {
    console.log("Registration successful:", response.data);
    return true; // Success
  } else {
    console.warn("Registration failed:", response.status);
    handleApiError(response.error, intl); // Handle error here
    return false; // Failure
  }
};

export default handleRegistration;