import { getRoles, getOffices } from '../api/enumsApi';

export const handleGetRoles = async () => {
  try {
    const roles = await getRoles();
    // Transform each role: "MANAGER" becomes "Manager"
    const formattedRoles = roles.map(
      role => role.charAt(0).toUpperCase() + role.slice(1).toLowerCase()
    );
    return formattedRoles; // returns the transformed array of roles
  } catch (error) {
    console.error("Error retrieving roles", error);
    return [];
  }
};

export const handleGetOffices = async () => {
  try {
    const offices = await getOffices();
    // Similarly, format the offices array.
    const formattedOffices = offices.map(
      office => office.charAt(0).toUpperCase() + office.slice(1).toLowerCase()
    );
    return formattedOffices;
  } catch (error) {
    console.error("Error retrieving offices", error);
    return [];
  }
};

export default { handleGetRoles, handleGetOffices };