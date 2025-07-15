export async function updateTwoFactorAuth(enabled) {
  try {
    const response = await fetch(`/api/settings/twofactor?enabled=${enabled}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
      },
    });
    const data = await response.json();
    if (!response.ok) throw new Error(data.message || "Erro ao atualizar 2FA");
    return data;
  } catch (error) {
    throw error;
  }
}
