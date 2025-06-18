import { updateUserInformation } from '../api/userApi';

// Esta função deve ser chamada no botão Save do Profile.js
// userData: objeto com os dados do utilizador a atualizar
// onSuccess: callback a executar em caso de sucesso (ex: atualizar o estado no Profile.js)
export async function handleUpdateUser(userId, user, updatedData) {

  const updates = Object.keys(updatedData).reduce((acc, key) => {
    if (updatedData[key] !== user[key]) {
      acc[key] = updatedData[key];
    }
    return acc;
  }, {});
  const userUpdate = {
    "user": updates
  }

  console.log("Computed updates:", updates);
  const response = await updateUserInformation(userId, updates);


}
