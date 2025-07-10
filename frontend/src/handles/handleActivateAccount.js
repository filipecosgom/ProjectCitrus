import { activateAccount } from '../api/activationApi';
import handleNotification from './handleNotification';

export const handleActivateAccount = async (token) => {
  const response = await activateAccount(token);
  if (response.success) {
    return true; // Success   
  } else {
    // Map backend error codes/messages to translation keys
    let errorKey = null;
    const errorMsg = response.message || response.errorCode || response.error;
    if (errorMsg) {
      if (errorMsg.includes('Invalid token')) errorKey = 'errorInvalidToken';
      else if (errorMsg.includes('Token expired')) errorKey = 'errorTokenExpired';
      else if (errorMsg.includes('activation failed')) errorKey = 'errorAccountActivationFailed';
      else if (errorMsg.includes('server')) errorKey = 'errorServerIssue';
      // Add more mappings as needed
    }
    handleNotification('error', errorKey || 'errorAccountActivationFailed');
    return false;
  }
};
export default handleActivateAccount;