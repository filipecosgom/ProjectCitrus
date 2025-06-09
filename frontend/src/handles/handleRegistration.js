import { register } from "../api/userApi";
import { handleApiError } from "../api/api";

const handleRegistration = async (newUser, intl) => {
  const response = await register(newUser);

  if (response.success) {
    return true; // Success
  } else {
    handleApiError(response.error, intl); // Handle error here
    return false; // Failure
  }
};

export default handleRegistration;