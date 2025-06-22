import { updateUserInformation } from "../api/userApi";
import handleUpdateAvatar from "./handleUpdateAvatar";
import handleNotification from "./handleNotification";

// Esta função deve ser chamada no botão Save do Profile.js
// userData: objeto com os dados do utilizador a atualizar
// onSuccess: callback a executar em caso de sucesso (ex: atualizar o estado no Profile.js)
// Main update function
export async function handleUpdateUserInfo(
  userId,
  user,
  updatedData,
  avatarFile
) {
  const updates = {};
  let avatarResult = { success: true };

  // 1. Handle avatar update first if file exists
  if (avatarFile) {
    avatarResult = await handleUpdateAvatar(userId, avatarFile);
    if (!avatarResult.success) {
      handleNotification("error", avatarResult.error || "avatarUploadFailed");
      return false;
    }
  }

  for (const key of Object.keys(updatedData)) {
    const original = user[key];
    const updated = updatedData[key];
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
    if (key === "manager") {
      if (!original?.id || !updated?.id || original.id !== updated.id) {
        updates[key] = updated;
      }
      continue;
    }
    if (Array.isArray(original) && Array.isArray(updated)) {
      const originalStr = new Date(...original).toISOString();
      const updatedStr = new Date(...updated).toISOString();
      if (originalStr !== updatedStr) {
        updates[key] = updated;
      }
      continue;
    }
    if (updated !== original) {
      updates[key] = updated;
    }
  }
  if (avatarResult.avatar) {
    updates.avatar = avatarResult.avatar;
    updates.hasAvatar = true;
  }

  // 4. Send other updates if any exist
  if (Object.keys(updates).length > 0) {
    const response = await updateUserInformation(userId, updates);
    if (response?.data?.success) {
      handleNotification("success", "profileUserUpdated");
      return { 
        ...response.data,
        avatar: avatarResult.avatar // Return new avatar info
      };
    }
  }

  return { success: true }; // Case where only avatar was updated
}
