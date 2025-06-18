import { updateUserInformation } from "../api/userApi";
import handleNotification from "./handleNotification";

// Esta função deve ser chamada no botão Save do Profile.js
// userData: objeto com os dados do utilizador a atualizar
// onSuccess: callback a executar em caso de sucesso (ex: atualizar o estado no Profile.js)
export async function handleUpdateUserInfo(userId, user, updatedData) {
  const updates = {};

  for (const key of Object.keys(updatedData)) {
    const original = user[key];
    const updated = updatedData[key];

    // Ignore fields that should never be patched
    if (
      [
        "id",
        "evaluationsGiven",
        "evaluationsReceived",
        "completedCourses",
        "creationDate",
      ].includes(key)
    ) {
      continue;
    }

    // Special case: nested manager object (compare by ID)
    if (key === "manager") {
      if (!original?.id || !updated?.id || original.id !== updated.id) {
        updates[key] = updated;
      }
      continue;
    }

    // Special case: birthdate and similar are sometimes sent as arrays — convert both to ISO before comparing
    if (Array.isArray(original) && Array.isArray(updated)) {
      const originalStr = new Date(...original).toISOString();
      const updatedStr = new Date(...updated).toISOString();
      if (originalStr !== updatedStr) {
        updates[key] = updated;
      }
      continue;
    }

    // Default shallow comparison
    if (updated !== original) {
      updates[key] = updated;
    }
  }

  console.log("Computed updates:", updates);
  if (Object.keys(updates).length > 0) {
    const response = await updateUserInformation(userId, updates);
    if (response.data.success) {
      handleNotification("success", "profileUserUpdated")
      return true;
    }
    else {
      return false;
    }
  } else {
    return false;
  }
}
