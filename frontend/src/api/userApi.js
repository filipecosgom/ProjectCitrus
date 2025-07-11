import { GiConsoleController } from "react-icons/gi";
import { api } from "./api"; // No need to import handleApiError here

const userEndpoint = "/users";

export const register = async (newUser, lang) => {
  try {
    const response = await api.post(userEndpoint, newUser, {
      headers: {
        "Accept-Language": lang,
      },
    });
    return { success: true, status: response.status, data: response.data };
  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
};

export const fetchSelfInformation = async () => {
  try {
    const response = await api.get(`${userEndpoint}/me`);
    return { success: true, status: response.status, data: response.data };
  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
};

export const fetchUserInformation = async (userId) => {
  try {
    const response = await api.get(`${userEndpoint}/${userId}`);
    return { success: true, status: response.status, data: response.data };
  } catch (error) {}
};

export const updateUserInformation = async (userId, userData) => {
  try {
    const response = await api.patch(`${userEndpoint}/${userId}`, userData, {
      headers: { "Content-Type": "application/json" },
    });
    return response;
  } catch (error) {
    return { success: false, error };
  }
};

export const uploadUserAvatar = async (userId, avatarFile) => {
  // Extract file extension from original filename
  const fileExtension = avatarFile.name.split(".").pop();
  // Create custom filename: "2.jpg" (for userId=2)
  const fileName = `${userId}.${fileExtension}`;
  try {
    const formData = new FormData();
    formData.append("file", avatarFile, fileName);

    const response = await api.patch(
      `${userEndpoint}/${userId}/avatar`,
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );
    return response;
  } catch (error) {
    return { success: false, error };
  }
};

export const fetchUserAvatar = async (userId) => {
  const response = await api.get(`${userEndpoint}/${userId}/avatar`, {
    responseType: "blob", // Required for binary image responses
    headers: {
      Accept: "image/jpeg, image/png, image/webp", // Match backend @Produces
    },
  });

  return {
    success: true,
    status: response.status,
    contentType: response.headers["content-type"],
    blob: response.data, // Return the blob in case you need it
  };
};

export const fetchPaginatedUsers = async ({
  id = null,
  email = null,
  name = null,
  phone = null,
  accountState = null,
  role = null,
  office = null,
  isManager = null,
  isAdmin = null,
  isManaged = null,
  parameter = "name",
  order = "ASCENDING",
  offset = 0,
  limit = 10,
} = {}) => {
  try {
    const params = new URLSearchParams();

    // Add optional parameters if they exist
    if (id) params.append("id", id);
    if (email) params.append("email", email);
    if (name) params.append("name", name);
    if (phone) params.append("phone", phone);
    if (accountState) params.append("accountState", accountState);
    if (role) params.append("role", role);
    if (office) params.append("office", office);
    // Only send isManager, isAdmin, isManaged if value is exactly true/false (boolean) or "true"/"false" (string)
    if (isManager === true || isManager === "true") params.append("isManager", "true");
    if (isManager === false || isManager === "false") params.append("isManager", "false");
    if (isAdmin === true || isAdmin === "true") params.append("isAdmin", "true");
    if (isAdmin === false || isAdmin === "false") params.append("isAdmin", "false");
    if (isManaged === true || isManaged === "true") params.append("isManaged", "true");
    if (isManaged === false || isManaged === "false") params.append("isManaged", "false");

    // Add pagination and sorting parameters
    params.append("parameter", parameter);
    params.append("order", order);
    params.append("offset", offset);
    params.append("limit", limit);

    console.log(`Fetching users with param`, params.toString());

    const response = await api.get(`${userEndpoint}?${params.toString()}`);
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};

export const fetchUsersCSV = async ({
  id = null,
  email = null,
  name = null,
  phone = null,
  accountState = null,
  role = null,
  office = null,
  isManager = null,
  isAdmin = null,
  isManaged = null,
  parameter = "name",
  order = "ASCENDING",
  language = "en",
} = {}) => {
  try {
    const params = new URLSearchParams();
    if (id) params.append("id", id);
    if (email) params.append("email", email);
    if (name) params.append("name", name);
    if (phone) params.append("phone", phone);
    if (accountState) params.append("accountState", accountState);
    if (role) params.append("role", role);
    if (office) params.append("office", office);
    if (isManager === true || isManager === "true") params.append("isManager", "true");
    if (isManager === false || isManager === "false") params.append("isManager", "false");
    if (isAdmin === true || isAdmin === "true") params.append("isAdmin", "true");
    if (isAdmin === false || isAdmin === "false") params.append("isAdmin", "false");
    if (isManaged === true || isManaged === "true") params.append("isManaged", "true");
    if (isManaged === false || isManaged === "false") params.append("isManaged", "false");
    params.append("parameter", parameter);
    params.append("order", order);
    params.append("language", language);
    console.log(`Fetching users CSV with params`, params.toString());

    const response = await api.get(
      `${userEndpoint}/export/csv?${params.toString()}`,
      {
        responseType: "blob",
        headers: { Accept: "text/csv" },
      }
    );
    return {
      success: true,
      status: response.status,
      contentType: response.headers["content-type"],
      blob: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};

export const fetchUsersXLSX = async ({
  id = null,
  email = null,
  name = null,
  phone = null,
  accountState = null,
  role = null,
  office = null,
  isManager = null,
  isAdmin = null,
  isManaged = null,
  parameter = "name",
  order = "ASCENDING",
  language = "en",
} = {}) => {
  try {
    const params = new URLSearchParams();
    if (id) params.append("id", id);
    if (email) params.append("email", email);
    if (name) params.append("name", name);
    if (phone) params.append("phone", phone);
    if (accountState) params.append("accountState", accountState);
    if (role) params.append("role", role);
    if (office) params.append("office", office);
    if (isManager === true || isManager === "true") params.append("isManager", "true");
    if (isManager === false || isManager === "false") params.append("isManager", "false");
    if (isAdmin === true || isAdmin === "true") params.append("isAdmin", "true");
    if (isAdmin === false || isAdmin === "false") params.append("isAdmin", "false");
    if (isManaged === true || isManaged === "true") params.append("isManaged", "true");
    if (isManaged === false || isManaged === "false") params.append("isManaged", "false");
    params.append("parameter", parameter);
    params.append("order", order);
    params.append("language", language);
    console.log(`Fetching users XLSX with params`, params.toString());

    const response = await api.get(
      `${userEndpoint}/export/xlsx?${params.toString()}`,
      {
        responseType: "blob",
        headers: {
          Accept:
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        },
      }
    );
    return {
      success: true,
      status: response.status,
      contentType: response.headers["content-type"],
      blob: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};
