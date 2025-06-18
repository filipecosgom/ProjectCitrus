import { getRoles, getOffices } from '../api/enumsApi';

export const handleGetRoles = async () => {
  try {
    const roles = await getRoles();
    return roles; // returns the transformed array of roles
  } catch (error) {
    console.error("Error retrieving roles", error);
    return [];
  }
};

export const handleGetOffices = async () => {
  try {
    const offices = await getOffices();
    return offices;
  } catch (error) {
    console.error("Error retrieving offices", error);
    return [];
  }
};

export default { handleGetRoles, handleGetOffices };