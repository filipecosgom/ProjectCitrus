import { fetchAllConversations } from "../api/messagesApi";

export const handleFetchAllConversations = async () => {
  const response = await fetchAllConversations();

  if (response.success) {
    // Return only the conversations array for simplicity
    return response.data?.data || [];
  } else {
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
