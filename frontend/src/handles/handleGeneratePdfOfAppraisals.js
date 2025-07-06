import { fetchAppraisalsPdf } from "../api/appraisalsApi";
import useLocaleStore from "../stores/useLocaleStore";
import handleNotification from "./handleNotification";

// Map sortOrder to backend lowercase
const mapOrderToBackend = (order) => {
  if (!order) return "descending";
  if (order.toLowerCase().startsWith("asc")) return "ascending";
  return "descending";
};

export const handleGeneratePdfOfAppraisals = async (params = {}) => {
  const pdfParams = { ...params };
  pdfParams.language = useLocaleStore.getState().locale || "en";
  if (params.sortOrder || params.order) {
    pdfParams.order = mapOrderToBackend(params.sortOrder || params.order);
  }

  const response = await fetchAppraisalsPdf(pdfParams);

  if (response.success) {
    handleNotification("success", 'appraisal.pdfGenerated');
    return {
      success: true,
      blob: response.data,
      status: response.status,
    };
  } else {
    if (response.error?.errorHandled) {
      return {
        success: false,
        error: response.error,
        suppressToast: true
      };
    }
    handleNotification("error", 'appraisal.pdfGenerationFailed');
    return {
      success: false,
      error: response.error || { message: "Unknown error" },
      shouldNotify: true,
    };
  }
};
