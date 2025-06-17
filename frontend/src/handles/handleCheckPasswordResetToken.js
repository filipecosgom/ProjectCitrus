import { checkPasswordResetToken } from "../api/authenticationApi";

export const handleCheckPasswordResetToken = async (token) => {
  try {
    const response = await checkPasswordResetToken(token);
    console.log(response);
    return response.success; // true if valid, false if not
  } catch (error) {
    // This block might never be reached if your interceptor always resolves.
    console.error("Caught error:", error);
    return { success: false, message: error.message };
  }
};


export default handleCheckPasswordResetToken;