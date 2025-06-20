import { fetchUserAvatar } from "../api/userApi";

export const handleGetUserAvatar = async (id) => {
  let response = await fetchUserAvatar(id);
  console.log('Avatar response:', response);

  if (response.success) {
    // Create object URL for the image blob
    const imageUrl = URL.createObjectURL(response.data);
    // Fetch user details after successful avatar load
    console.log('User info response:', response);
    if (response.success) {
      return {
        avatar: imageUrl,
        userData: response.data
      };
    }
  }
  return false; // Failure case (interceptors will have handled errors)
};