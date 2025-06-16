import { fetchUserInformation } from '../api/userApi';

export const handleGetUserInformation = async (id) => {
  const response = await fetchUserInformation(id);
  console.log(response);
  // Fetch user details after successful login
  if (response.success) {
    return response.data.data; // Success
  } else {
    return null; // Failure
  }
};
export default handleGetUserInformation;