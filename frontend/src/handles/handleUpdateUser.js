import handleNotification from "./handleNotification";

// Esta função deve ser chamada no botão Save do Profile.js
// userData: objeto com os dados do utilizador a atualizar
// onSuccess: callback a executar em caso de sucesso (ex: atualizar o estado no Profile.js)
export async function handleUpdateUser(userData, onSuccess) {
  try {
    // Aqui farás o request real ao backend (exemplo com fetch)
    // const response = await fetch("/api/user/update", {
    //   method: "PUT",
    //   headers: { "Content-Type": "application/json" },
    //   body: JSON.stringify(userData),
    // });
    // const result = await response.json();

    // Mock de resposta do backend (simula sucesso)
    const result = { success: true };

    if (result.success) {
      handleNotification("success", "profile.update.success");
      if (onSuccess) onSuccess();
    } else {
      handleNotification("error", "profile.update.error");
    }
  } catch (error) {
    handleNotification("error", "profile.update.error");
  }
}
