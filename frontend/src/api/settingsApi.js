import axios from "axios";
import { apiBaseUrl } from "../config";

export async function updateTwoFactorAuth(enabled) {
  try {
    const response = await axios.put(
      `${apiBaseUrl}/settings/twofactor?enabled=${enabled}`,
      {},
      { withCredentials: true }
    );
    return response.data;
  } catch (error) {
    throw error;
  }
}

export async function getTwoFactorAuthStatus() {
  try {
    const response = await axios.get(`${apiBaseUrl}/settings/twofactor`, {
      withCredentials: true,
    });
    return response.data.enabled;
  } catch (error) {
    throw error;
  }
}
