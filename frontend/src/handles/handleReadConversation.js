import { readConversationApi } from "../api/messagesApi";

export const handleReadConversation = async (senderId) => {
  console.log("handleReadConversation called with senderId:", senderId);
  const response = await readConversationApi(senderId);

  if (response.success) {
    return {
      success: true,
      data: response.data?.data || null,
    };
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
