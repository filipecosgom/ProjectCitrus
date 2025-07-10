import axios from "axios";
import { apiBaseUrl } from "../config";

const cyclesEndpoint = `${apiBaseUrl}/cycles`;

/**
 * Busca todos os ciclos com filtros opcionais
 * @param {Object} params - Parâmetros de filtro
 * @returns {Promise<Object>} Response com lista de ciclos
 */
export const fetchCycles = async (params = {}) => {
  console.log(params);
  try {
    const response = await axios.get(cyclesEndpoint, {
      params,
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    });
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};

/**
 * Cria um novo ciclo
 * @param {Object} cycleData - Dados do ciclo
 * @returns {Promise<Object>} Response com resultado da operação
 */
export const createCycle = async (cycleData) => {
  console.log(cycleData);
  try {
    const response = await axios.post(cyclesEndpoint, cycleData, {
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    });
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};

/**
 * Fecha um ciclo
 * @param {number} id - ID do ciclo
 * @returns {Promise<Object>} Response com resultado da operação
 */
export const closeCycle = async (id) => {
  try {
    const response = await axios.post(
      `${cyclesEndpoint}/${id}/close`,
      {},
      {
        withCredentials: true,
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};

/**
 * Validates if a cycle can be closed
 * @param {number} cycleId - The cycle ID
 * @returns {Promise<Object>} API response with validation details
 */
export const canCloseCycle = async (cycleId) => {
  try {
    const response = await axios.get(`${cyclesEndpoint}/${cycleId}/can-close`, {
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    });
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    console.error("Error validating cycle closure:", error);
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};

/**
 * Conta o número de usuários ativos (não admin e não deletados)
 * @returns {Promise<Object>} Response com contagem de usuários
 */
export const fetchActiveUsersCount = async () => {
  try {
    const response = await axios.get(`${apiBaseUrl}/users`, {
      params: {
        isAdmin: false,
        isDeleted: false,
        limit: 1, // Só precisamos da contagem
      },
      withCredentials: true,
      headers: {
        "Content-Type": "application/json",
      },
    });
    return {
      success: true,
      status: response.status,
      data: response.data,
    };
  } catch (error) {
    return {
      success: false,
      status: error.response?.status || 500,
      error: error.response?.data || error.message,
    };
  }
};
