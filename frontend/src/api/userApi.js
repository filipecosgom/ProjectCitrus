import { api } from "./api"; // No need to import handleApiError here

const userEndpoint = "/users";

export const register = async (newUser, lang) => {
  try {
    const response = await api.post(userEndpoint, newUser, {
      headers: {
        "Accept-Language": lang,
      },
    });
    console.log("API Response:", response); // Debugging log
    return { success: true, status: response.status, data: response.data };
  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
};

export const fetchSelfInformation = async() => {
  try {
    const response = await api.get(`${userEndpoint}/me`);
    return { success: true, status: response.status, data: response.data };
  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
}

export const fetchUserInformation = async (userId) => {
  try {
    const response = await api.get(`${userEndpoint}/${userId}`);
    return { success: true, status: response.status, data: response.data };
  } catch (error) {
    
  }
}

export const updateUserInformation = async (userId, updatedData) => {
  try {
    let dataToSend = updatedData;
    let headers = {};

    // If avatar is a File (from input), use FormData
    if (updatedData.avatar && updatedData.avatar instanceof File) {
      const formData = new FormData();
      Object.entries(updatedData).forEach(([key, value]) => {
        if (key === "avatar" && value instanceof File) {
          formData.append("avatar", value);
        } else {
          formData.append(key, value);
        }
      });
      dataToSend = formData;
      headers["Content-Type"] = "multipart/form-data";
    }

    const response = await api.patch(
      `${userEndpoint}/${userId}`,
      dataToSend,
      { headers }
    );
    return response;
  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
};

