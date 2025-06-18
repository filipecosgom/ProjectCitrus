import { fetchUserInformation } from '../api/userApi';
import { transformArrayLocalDatetoLocalDate, dateToFormattedDate } from '../utils/utilityFunctions';

export const handleGetUserInformation = async (id) => {
  const response = await fetchUserInformation(id);
  // Fetch user details after successful login
  if (response.success) {
    if(response.data.data.birthdate) {
        response.data.data.birthdate = dateToFormattedDate(transformArrayLocalDatetoLocalDate(response.data.data.birthdate));
    }
    let office = response.data.data.office;
    let role = response.data.data.role;
    office =  office.charAt(0).toUpperCase() + office.slice(1).toLowerCase();
    role = role.charAt(0).toUpperCase() + role.slice(1).toLowerCase();
    response.data.data.office = office;
    response.data.data.role = role;
    return response.data.data; // Success
  } else {
    return null; // Failure
  }
};
export default handleGetUserInformation;