import { activateAccount } from '../api/activationApi';

export const handleActivateAccount = async (token) => {
  const response = await activateAccount(token);
  console.log(response);
  // Fetch user details after successful login
  if (response.success) {
    return true; // Success   
  } else {
    return false; // Failure
  }
};
export default handleActivateAccount;