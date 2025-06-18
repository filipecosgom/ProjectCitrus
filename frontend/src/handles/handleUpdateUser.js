import axios from "axios";
import handleNotification from "./handleNotification";
import { apiBaseUrl } from "../config";

// Esta função deve ser chamada no botão Save do Profile.js
// userData: objeto com os dados do utilizador a atualizar
// onSuccess: callback a executar em caso de sucesso (ex: atualizar o estado no Profile.js)
export async function handleUpdateUser(userId, user, updatedData) {
  console.log("Updating user information...");
  console.log("User ID:", userId);
  console.log("Updated Data:", updatedData);

}
