import { fetchCourses } from "../api/coursesApi";

// paramsBuilder is a function or object that builds the params for the API call
export const handleGetCourses = async ({
  area = null,
  language = null,
  adminId = null,
  isActive = null,
  parameter = "title",
  order = "ASCENDING",
  offset = 0,
  limit = 10,
  ...rest
} = {}) => {
  // Build params for the API
  const params = {};
  if (area) params.area = area;
  if (language) params.language = language;
  if (adminId) params.adminId = adminId;
  if (isActive !== null) params.isActive = isActive;
  params.parameter = parameter;
  params.order = order;
  params.offset = offset;
  params.limit = limit;
  // Add any extra params
  Object.assign(params, rest);

  const response = await fetchCourses(params);

  if (response.success) {
    // You can process courses here if needed
    const courses = response.data?.data?.courses || [];
    const paginationInfo = {
      totalCourses: response.data?.data?.totalCourses || 0,
      offset: response.data?.data?.offset || 0,
      limit: response.data?.data?.limit || 10
    };
    return {
      courses,
      pagination: paginationInfo,
      success: true
    };
  } else {
    // Handle error cases
    if (response.error?.errorHandled) {
      return {
        success: false,
        error: response.error,
        suppressToast: true
      };
    }
    return {
      success: false,
      error: response.error
    };
  }
};
