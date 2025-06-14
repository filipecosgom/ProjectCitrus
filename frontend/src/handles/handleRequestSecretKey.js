import { requestSecretKey } from "../api/authenticationApi";


const handleRequestSecretKey = async (userInformation) => {
  console.log("user info:", userInformation);

  const response = await requestSecretKey(userInformation);

  if (response.success) {
    console.log(response);
    return response.data.authCode; // Success
  } else {
    return null; // Failure
  }
};

export default handleRequestSecretKey;