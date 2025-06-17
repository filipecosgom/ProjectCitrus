import { changePassword } from "../api/authenticationApi";
export const handleChangePassword = async (passwordResetToken, newPassword) => {
    const password = {
        password: newPassword
    };
    const response = await changePassword(passwordResetToken, password);
    console.log("Change Password Response:", response); // Debugging log
    return response.success;
}

export default handleChangePassword;