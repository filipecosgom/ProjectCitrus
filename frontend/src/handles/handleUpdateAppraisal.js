import { updateAppraisal } from "../api/appraisalsApi";

export const handleUpdateAppraisal = async (appraisal) => {
  try {
    const response = await updateAppraisal(appraisal);
    // Only treat as success if status is 200-299 and success is true
    if (response && response.success === true && (!response.status || (response.status >= 200 && response.status < 300))) {
      return {
        success: true,
        data: response.data,
      };
    } else {
      return {
        success: false,
        error: response.error || response.message || "Unknown error",
        status: response.status,
      };
    }
  } catch (error) {
    return {
      success: false,
      error: error.message || "Unknown error",
      status: 500,
    };
  }
};
