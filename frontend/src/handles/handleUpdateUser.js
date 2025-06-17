import axios from "axios";
import handleNotification from "./handleNotification";
import { apiBaseUrl } from "../config";

// Esta função deve ser chamada no botão Save do Profile.js
// userData: objeto com os dados do utilizador a atualizar
// onSuccess: callback a executar em caso de sucesso (ex: atualizar o estado no Profile.js)
export async function handleUpdateUser(userId, userData, onSuccess) {
  try {
    const response = await axios.patch(
      `${apiBaseUrl}/users/${userId}`,
      userData,
      { withCredentials: true }
    );
    handleNotification("success", "profile.update.success");
    if (onSuccess) onSuccess(response.data);
  } catch (error) {
    handleNotification("error", "profile.update.error");
  }
}
