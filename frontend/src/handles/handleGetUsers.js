import { fetchPaginatedUsers, fetchUsersCSV } from "../api/userApi";
import { dateToFormattedDate, transformArrayLocalDatetoLocalDate } from '../utils/utilityFunctions';
import useLocaleStore from "../stores/useLocaleStore";

export const handleGetUsers = async (params = {}) => {
  const response = await fetchPaginatedUsers(params);
  
  if (response.success) {
    // Process the response data if needed
    const users = response.data?.data?.users || [];
    const paginationInfo = {
      totalUsers: response.data?.data?.totalUsers || 0,
      offset: response.data?.data?.offset || 0,
      limit: response.data?.data?.limit || 10
    };

    // Process birthdates for each user if needed
    const processedUsers = users.map(user => {
      if (user.birthdate) {
        return {
          ...user,
          birthdate: dateToFormattedDate(transformArrayLocalDatetoLocalDate(user.birthdate))
        };
      }
      return user;
    });

    return {
      users: processedUsers,
      pagination: paginationInfo,
      success: true
    };
  } else {
    // Handle error cases based on your interceptor logic
    if (response.error?.errorHandled) {
      // Error was already handled by interceptor
      return { 
        success: false, 
        error: response.error,
        suppressToast: true // If you want to prevent duplicate toasts
      };
    }

    // For unhandled errors (should be rare with your interceptor)
    return { 
      success: false, 
      error: response.error || { message: 'Unknown error' },
      shouldNotify: true // Flag to show notification if needed
    };
  }
};

export const handleGetUsersCSV = async (params = {}) => {
  const locale = useLocaleStore.getState().locale || "en";
  console.log("Fetching users CSV with params:", { ...params, language: locale });
  const response = await fetchUsersCSV({ ...params, language: locale });

  if (response.success) {
    // Return the blob and content type for download
    return {
      success: true,
      blob: response.blob,
      contentType: response.contentType
    };
  } else {
    if (response.error?.errorHandled) {
      return {
        success: false,
        error: response.error,
        suppressToast: true
      };
    }
    return {
      success: false,
      error: response.error || { message: 'Unknown error' },
      shouldNotify: true
    };
  }
};