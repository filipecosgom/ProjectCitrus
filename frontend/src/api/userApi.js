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

export const updateUserInformation = async (userId, userData) => {
  try {
    const response = await api.patch(
      `${userEndpoint}/${userId}`,
      userData,
      { headers: { "Content-Type": "application/json" } }
    );
    return response;
  } catch (error) {
    return { success: false, error };
  }
};

export const uploadUserAvatar = async (userId, avatarFile) => {
  // Extract file extension from original filename
    const fileExtension = avatarFile.name.split('.').pop();
    // Create custom filename: "2.jpg" (for userId=2)
    const fileName = `${userId}.${fileExtension}`;
  try {
    const formData = new FormData();
    formData.append("file", avatarFile, fileName);
    console.log(formData);

    const response = await api.post(
      `${userEndpoint}/${userId}/avatar`,
      formData,
      {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }
    );
    return response;
  } catch (error) {
    return { success: false, error };
  }
};

export const fetchUserAvatar = async (userId) => {
  const response = await api.get(`${userEndpoint}/${userId}/avatar`, {
    responseType: 'blob', // Required for binary image responses
    headers: {
      'Accept': 'image/jpeg, image/png, image/webp' // Match backend @Produces
    }
  });

  
  return {
    success: true,
    status: response.status,
    contentType: response.headers['content-type'],
    blob: response.data // Return the blob in case you need it
  };
};