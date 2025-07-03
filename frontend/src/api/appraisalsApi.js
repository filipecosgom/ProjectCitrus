import { api } from "./api";

const appraisalsEndpoint = "/appraisals";

export const fetchPaginatedAppraisals = async ({
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
    if (isManager !== null) params.append("isManager", isManager);
    if (isAdmin !== null) params.append("isAdmin", isAdmin);
    if (isManaged !== null) params.append("isManaged", isManaged);

    // Add pagination and sorting parameters
    params.append("parameter", parameter);
    params.append("order", order);
    params.append("offset", offset);
    params.append("limit", limit);

    const response = await api.get(`${appraisalsEndpoint}?${params.toString()}`);
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

export const updateAppraisal = async (updateAppraisalDTO) => {
  console.log("updateAppraisal called with:", updateAppraisalDTO);
  try {
    const response = await api.patch(
      appraisalsEndpoint,
      updateAppraisalDTO,
      { withCredentials: true }
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
