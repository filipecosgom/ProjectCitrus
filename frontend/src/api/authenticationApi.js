import { api } from "./api"; // Import the global Axios instance

const authenticationEndpoint = "/auth"; // Keep only the relative endpoint


// User Login
export const login = async (loginInformation) => {
  try {
    const response = await api.post(`${authenticationEndpoint}/login`, loginInformation);
    return response.data;
  } catch (error) {
    throw error; // para propagar o erro
  }
};

// User Login
export const requestSecretKey = async (userInformation) => {
  try {
    const response = await api.post(`${authenticationEndpoint}`, userInformation);
    return response.data;
  } catch (error) {
    throw error;
  }
};

export const requestPasswordReset = async (email, lang) => {
  try {
    const response = await api.post(`${authenticationEndpoint}/password-reset`, email, {
      headers: {
        "Accept-Language": lang,
      },
    });
    return { success: true, status: response.status, data: response.data };
  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
};

export const checkPasswordResetToken = async (token) => {
  try {
    const response = await api.get(`${authenticationEndpoint}/password-reset`, {
      headers: {
        "token": token,
      },
    });
    // If response.data is present, assume the successful format
    if (response.data) {
      // Normalize to a common format:
      return {
        success: response.data.success,
        status: response.status,
        data: response.data,
      };
    }
    
    // Otherwise, assume it's already normalized (error response from interceptor)
    return response;

  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
};

export const changePassword = async (passwordResetToken, newPassword) => {
  try {
    const response = await api.patch(`${authenticationEndpoint}/password-reset`, newPassword,  {
      headers: {
        "token": passwordResetToken,
      }
  });
    // If response.data is present, assume the successful format
    if (response.data) {
      // Normalize to a common format:
      return {
        success: response.data.success,
        status: response.status,
        data: response.data,
      };
    }
    
    // Otherwise, assume it's already normalized (error response from interceptor)
    return response;
  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
};

// Calls the backend logout endpoint
export const logoutRequest = async () => {
  try {
    const response = await api.post(`${authenticationEndpoint}/logout`);
    return response.data;
  } catch (error) {
    // Optionally handle error
    return { success: false, error };
  }
};
