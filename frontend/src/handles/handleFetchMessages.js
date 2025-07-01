import { fetchMessages } from "../api/messagesApi";

export const handleFetchMessages = async (otherUserId) => {
  const response = await fetchMessages(otherUserId);

  if (response.success) {
    // Extract messages list from the response (API returns data inside response.data)
    const messages = response.data?.data || [];
    return {
      messages,
      success: true,
    };
  } else {
    // Handle error cases based on your interceptor logic
    if (response.error?.errorHandled) {
      return {
        success: false,
        error: response.error,
        suppressToast: true,
      };
    }
    return {
      success: false,
      error: response.error || { message: "Unknown error" },
      shouldNotify: true,
    };
  }
};