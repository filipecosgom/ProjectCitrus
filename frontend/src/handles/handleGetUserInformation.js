import { fetchUserInformation } from '../api/userApi';
import { transformArrayLocalDatetoLocalDate, dateToFormattedDate } from '../utils/utilityFunctions';

export const handleGetUserInformation = async (id) => {
  const response = await fetchUserInformation(id);
  console.log(response);
  // Fetch user details after successful login
  if (response.success) {
    response.data.data.birthdate = transformArrayLocalDatetoLocalDate(response.data.data.birthdate);
    console.log(response.data.data)
    return response.data.data; // Success
  } else {
    return null; // Failure
  }
};
export default handleGetUserInformation;