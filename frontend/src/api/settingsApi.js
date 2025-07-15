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

export async function getTwoFactorAuthStatus() {
  try {
    const response = await fetch("/api/settings/twofactor", {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
      },
    });
    const data = await response.json();
    if (!response.ok)
      throw new Error(data.message || "Erro ao obter estado 2FA");
    return data.enabled; // espera que o backend devolva { enabled: true/false }
  } catch (error) {
    throw error;
  }
}
