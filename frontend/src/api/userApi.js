/**
 * API module for user-related operations.
 * Provides functions for registration, user information retrieval, updates, avatar management, and exporting user data.
 * All requests are authenticated and interact with the backend user endpoints.
 * @module userApi
 */
import { api } from "./api"; // No need to import handleApiError here

const userEndpoint = "/users";

/**
 * Registers a new user.
 * @async
 * @function register
 * @param {Object} newUser - The user data to register.
 * @param {string} lang - The language for the response.
 * @returns {Promise<Object>} An object containing success status, HTTP status, and registered user data or error details.
 */
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

/**
 * Fetches information about the currently authenticated user.
 * @async
 * @function fetchSelfInformation
 * @returns {Promise<Object>} An object containing success status, HTTP status, and user data or error details.
 */
export const fetchSelfInformation = async () => {
  try {
    const response = await api.get(`${userEndpoint}/me`);
    return { success: true, status: response.status, data: response.data };
  } catch (error) {
    return { success: false, status: error.response?.status || 500, error };
  }
};

/**
 * Fetches information for a specific user by ID.
 * @async
 * @function fetchUserInformation
 * @param {number|string} userId - The ID of the user to fetch.
 * @returns {Promise<Object>} An object containing success status, HTTP status, and user data or error details.
 */
export const fetchUserInformation = async (userId) => {
  try {
    const response = await api.get(`${userEndpoint}/${userId}`);
    return { success: true, status: response.status, data: response.data };
  } catch (error) {}
};

/**
 * Updates information for a specific user.
 * @async
 * @function updateUserInformation
 * @param {number|string} userId - The ID of the user to update.
 * @param {Object} userData - The updated user data.
 * @returns {Promise<Object>} An object containing success status and updated user data or error details.
 */
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

/**
 * Uploads a new avatar image for a user.
 * @async
 * @function uploadUserAvatar
 * @param {number|string} userId - The ID of the user.
 * @param {File} avatarFile - The avatar image file to upload.
 * @returns {Promise<Object>} An object containing success status and updated avatar data or error details.
 */
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

/**
 * Fetches the avatar image for a specific user.
 * @async
 * @function fetchUserAvatar
 * @param {number|string} userId - The ID of the user.
 * @returns {Promise<Object>} An object containing success status, HTTP status, content type, and avatar blob data.
 */
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

/**
 * Fetches a paginated list of users with optional filters and sorting.
 * @async
 * @function fetchPaginatedUsers
 * @param {Object} [options] - Optional query parameters for filtering and pagination.
 * @returns {Promise<Object>} An object containing success status, HTTP status, and user list data or error details.
 */
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
    if (isManager === true || isManager === "true")
      params.append("isManager", "true");
    if (isManager === false || isManager === "false")
      params.append("isManager", "false");
    if (isAdmin === true || isAdmin === "true")
      params.append("isAdmin", "true");
    if (isAdmin === false || isAdmin === "false")
      params.append("isAdmin", "false");
    if (isManaged === true || isManaged === "true")
      params.append("isManaged", "true");
    if (isManaged === false || isManaged === "false")
      params.append("isManaged", "false");

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

/**
 * Fetches user data as a CSV file with optional filters and sorting.
 * @async
 * @function fetchUsersCSV
 * @param {Object} [options] - Optional query parameters for filtering and sorting.
 * @returns {Promise<Object>} An object containing success status, HTTP status, content type, and CSV blob data or error details.
 */
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
    if (isManager === true || isManager === "true")
      params.append("isManager", "true");
    if (isManager === false || isManager === "false")
      params.append("isManager", "false");
    if (isAdmin === true || isAdmin === "true")
      params.append("isAdmin", "true");
    if (isAdmin === false || isAdmin === "false")
      params.append("isAdmin", "false");
    if (isManaged === true || isManaged === "true")
      params.append("isManaged", "true");
    if (isManaged === false || isManaged === "false")
      params.append("isManaged", "false");
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

/**
 * Fetches user data as an XLSX file with optional filters and sorting.
 * @async
 * @function fetchUsersXLSX
 * @param {Object} [options] - Optional query parameters for filtering and sorting.
 * @returns {Promise<Object>} An object containing success status, HTTP status, content type, and XLSX blob data or error details.
 */
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
    if (isManager === true || isManager === "true")
      params.append("isManager", "true");
    if (isManager === false || isManager === "false")
      params.append("isManager", "false");
    if (isAdmin === true || isAdmin === "true")
      params.append("isAdmin", "true");
    if (isAdmin === false || isAdmin === "false")
      params.append("isAdmin", "false");
    if (isManaged === true || isManaged === "true")
      params.append("isManaged", "true");
    if (isManaged === false || isManaged === "false")
      params.append("isManaged", "false");
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

/**
 * Adds a finished course for a user.
 * @async
 * @function addFinishedCourseToUser
 * @param {number|string} userId - The ID of the user.
 * @param {number|string} courseId - The ID of the finished course.
 * @returns {Promise<Object>} An object containing success status, HTTP status, and finished course data or error details.
 */
export const addFinishedCourseToUser = async (userId, courseId) => {
  try {
    const response = await api.post(`/users/${userId}/course/${courseId}`);
    return {
      success: true,
      status: response.status,
      data: response.data, // Should be FinishedCourseDTO
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};

/**
 * Fetches all users (up to 1000) from the backend.
 * @async
 * @function fetchAllUsers
 * @returns {Promise<Array>} An array of user objects if successful, otherwise throws an error.
 */
export const fetchAllUsers = async () => {
  try {
    const response = await api.get("/users?limit=1000"); // ou sem limite, conforme backend
    // Espera que a resposta tenha { success, data: { users: [...] } }
    if (response.data?.success && response.data?.data?.users) {
      return response.data.data.users;
    }
    throw new Error("Failed to fetch users");
  } catch (error) {
    throw error;
  }
};

/**
 * Updates the admin permission for a specific user.
 * @async
 * @function updateAdminPermission
 * @param {number|string} userId - The ID of the user.
 * @param {boolean} isAdmin - Whether the user should have admin permissions.
 * @returns {Promise<Object>} The updated user data if successful, otherwise throws an error.
 */
export const updateAdminPermission = async (userId, isAdmin) => {
  try {
    const response = await api.put(
      `/users/${userId}/admin-permissions`,
      { isAdmin },
      { headers: { "Content-Type": "application/json" } }
    );
    if (response.data?.success) {
      return response.data.data;
    }
    throw new Error(
      response.data?.message || "Failed to update admin permission"
    );
  } catch (error) {
    throw error;
  }
};
