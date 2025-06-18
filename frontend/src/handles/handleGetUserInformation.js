import { fetchUserInformation } from '../api/userApi';
import { transformArrayLocalDatetoLocalDate, dateToFormattedDate } from '../utils/utilityFunctions';

export const handleGetUserInformation = async (id) => {
  const response = await fetchUserInformation(id);
  // Fetch user details after successful login
  if (response.success) {
    if(response.data.data.birthdate) {
        response.data.data.birthdate = dateToFormattedDate(transformArrayLocalDatetoLocalDate(response.data.data.birthdate));
    }
    return response.data.data; // Success
  } else {
    return null; // Failure
  }
};
export default handleGetUserInformation;