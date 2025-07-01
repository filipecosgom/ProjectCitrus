import { sendMessageApi } from "../api/messagesApi";

export const handleSendMessageViaApi = async (receiverId, content) => {
  const response = await sendMessageApi(receiverId, content);

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
