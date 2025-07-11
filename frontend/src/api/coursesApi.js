import { api } from "./api"; // No need to import handleApiError here

const courseEndpoint = "/courses";

export const fetchCourses = async (params = {}) => {
  // Transform {query, searchType, ...} into {[searchType]: query, ...}
  let newParams = { ...params };
  if (newParams.query && newParams.searchType) {
    newParams[newParams.searchType] = newParams.query;
    delete newParams.query;
    delete newParams.searchType;
  }
  console.log("Fetching courses with params:", newParams);
  try {
    const query = new URLSearchParams();
    Object.entries(newParams).forEach(([key, value]) => {
      if (value !== null && value !== undefined) {
        // Ensure booleans are sent as strings
        query.append(key, typeof value === 'boolean' ? String(value) : value);
      }
    });
    const response = await api.get(`${courseEndpoint}?${query.toString()}`);
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

export const fetchCourseImage = async (courseId) => {
  try {
    const response = await api.get(`${courseEndpoint}/${courseId}/image`, {
      responseType: "blob",
      headers: {
        Accept: "image/jpeg, image/png, image/webp",
      },
    });
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

export const uploadCourseImage = async (courseId, imageFile) => {
  const fileExtension = imageFile.name.split(".").pop();
  const fileName = `${courseId}.${fileExtension}`;
  try {
    const formData = new FormData();
    formData.append("file", imageFile, fileName);

    const response = await api.put(
      `${courseEndpoint}/${courseId}/image`,
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

export const createCourse = async (courseData) => {
  try {
    const response = await api.post(
      courseEndpoint,
      courseData,
      {
        headers: {
          'Content-Type': 'application/json'
        },
        withCredentials: true,
      }
    );
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

export const updateCourse = async (courseId, updateData) => {
  try {
    const response = await api.put(
      `${courseEndpoint}/${courseId}`,
      updateData,
      {
        headers: {
          'Content-Type': 'application/json',
        },
        withCredentials: true,
      }
    );
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