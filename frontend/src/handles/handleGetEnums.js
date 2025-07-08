import { getRoles, getOffices, getAppraisalStates, getCourseAreas } from '../api/enumsApi';

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

export const handleGetAppraisalStates = async () => {
  try {
    const appraisalStates = await getAppraisalStates();
    return appraisalStates;
  } catch (error) {
    console.error("Error retrieving appraisal states", error);
    return [];
  }
};

export const handleGetCourseAreas = async () => {
  try {
    const courseAreas = await getCourseAreas();
    return courseAreas;
  } catch (error) {
    console.error("Error retrieving course areas", error);
    return [];
  }
};

export default { handleGetRoles, handleGetOffices, handleGetAppraisalStates, handleGetCourseAreas };