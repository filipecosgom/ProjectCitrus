import { requestSecretKey } from "../api/authenticationApi";


const handleRequestSecretKey = async (userInformation) => {
  try{
  const response = await requestSecretKey(userInformation);
  if (response?.errorHandled) {
      console.warn("Handled error:", response);
      return null; // Return safe fallback instead of breaking execution
    }
  if (response?.success) {
    return response.data.authCode; // Success
  }
 } catch (error) {
    console.error("Unexpected error:", error);
    throw error; // Only throw errors that are truly unexpected
  }
};


export default handleRequestSecretKey;