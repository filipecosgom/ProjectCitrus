import { getRoles, getOffices, getAppraisalStates, getCourseAreas } from '../api/enumsApi';
import handleNotification from './handleNotification';

export const handleGetRoles = async () => {
  try {
    const roles = await getRoles();
    return roles; // returns the transformed array of roles
  } catch (error) {
    handleNotification("error", "enums.errorFetchRoles");
    return [];
  }
};

export const handleGetOffices = async () => {
  try {
    const offices = await getOffices();
    return offices;
  } catch (error) {
    handleNotification("error", "enums.errorFetchOffices");
    return [];
  }
};

export const handleGetAppraisalStates = async () => {
  try {
    const appraisalStates = await getAppraisalStates();
    return appraisalStates;
  } catch (error) {
    handleNotification("error", "enums.errorFetchAppraisalStates");
    return [];
  }
};

export const handleGetCourseAreas = async () => {
  try {
    const courseAreas = await getCourseAreas();
    return courseAreas;
  } catch (error) {
    handleNotification("error", "enums.errorFetchCourseAreas");
    return [];
  }
};

export default { handleGetRoles, handleGetOffices, handleGetAppraisalStates, handleGetCourseAreas };