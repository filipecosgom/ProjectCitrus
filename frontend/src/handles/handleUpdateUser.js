import { updateUserInformation, uploadUserAvatar } from "../api/userApi";
import handleNotification from "./handleNotification";

// Esta função deve ser chamada no botão Save do Profile.js
// userData: objeto com os dados do utilizador a atualizar
// onSuccess: callback a executar em caso de sucesso (ex: atualizar o estado no Profile.js)
export async function handleUpdateUserInfo(userId, user, updatedData) {
  const updates = {};
  let avatarUploadSuccess = true;

  for (const key of Object.keys(updatedData)) {
    const original = user[key];
    const updated = updatedData[key];

    if (
      ["id", "evaluationsGiven", "evaluationsReceived", "completedCourses", "creationDate"].includes(key)
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

  console.log("Computed updates:", updates);

  // 🖼️ Step 1: Upload avatar separately if present
  if (updatedData.avatar && updatedData.avatar[0] instanceof File) {
    const avatarResponse = await uploadUserAvatar(userId, updatedData.avatar[0]);
    avatarUploadSuccess = avatarResponse?.data?.success;

    if (avatarUploadSuccess && avatarResponse.data.avatar) {
      updates.avatar = avatarResponse.data.avatar; // server-provided filename
    } else {
      handleNotification("error", "avatarUploadFailed");
      return false;
    }
  }

  // 📝 Step 2: Send updates if there are any
  if (Object.keys(updates).length > 0) {
    const response = await updateUserInformation(userId, updates);
    if (response?.data?.success) {
      handleNotification("success", "profileUserUpdated");
      return true;
    }
  }

  return false;
}