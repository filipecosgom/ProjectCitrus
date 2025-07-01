import { fetchUserInformation, updateUserInformation } from "../api/userApi"; // ✅ USAR userApi

/**
 * Handle para atribuir manager aos users selecionados usando PATCH existente
 * @param {Object} assignmentData - Dados da atribuição
 * @param {string} assignmentData.newManagerId - ID do user que será promovido a manager
 * @param {Array} assignmentData.userIds - IDs dos users que receberão o manager
 * @returns {Promise<Object>} Resultado da operação
 */
export const handleAssignManager = async (assignmentData) => {
  console.log("🎯 handleAssignManager - Starting assignment:", assignmentData);

  try {
    const { newManagerId, userIds } = assignmentData;

    // ✅ PASSO 1: Buscar user usando userApi (MESMA FORMA que funciona)
    console.log("🔍 Step 1: Getting user data...");
    const getUserResult = await fetchUserInformation(newManagerId);

    console.log("📦 GET User Result:", getUserResult);

    if (!getUserResult.success) {
      throw new Error(
        `Failed to get user data: ${
          getUserResult.error?.message || "User not found"
        }`
      );
    }

    // ✅ VERIFICAR estrutura da resposta do userApi
    const currentUser = getUserResult.data?.data;

    if (!currentUser) {
      throw new Error("User not found in response");
    }

    console.log("👤 Current user data:", currentUser);

    // ✅ PASSO 2: Promover a manager (se ainda não for)
    let promotionResult = null;
    if (!currentUser.userIsManager) {
      console.log("🚀 Step 2: Promoting user to manager...");

      promotionResult = await updateUserInformation(newManagerId, {
        userIsManager: true,
      });

      console.log("📦 Promotion result:", promotionResult);

      // ✅ VERIFICAR se a response do updateUserInformation tem success
      if (!promotionResult || promotionResult.success === false) {
        throw new Error(
          `Failed to promote user to manager: ${
            promotionResult?.error?.message || "Unknown error"
          }`
        );
      }
    } else {
      console.log("✅ User is already a manager, skipping promotion");
    }

    // ✅ PASSO 3: Atribuir manager_id aos users selecionados
    console.log("🚀 Step 3: Assigning manager to users...");

    const assignmentPromises = userIds.map(async (userId) => {
      try {
        console.log(`📡 Assigning manager to user ${userId}...`);

        const result = await updateUserInformation(userId, {
          manager: {
            id: parseInt(newManagerId),
            name: currentUser.name,
            surname: currentUser.surname,
            email: currentUser.email,
          },
        });

        console.log(`📦 Assignment result for user ${userId}:`, result);

        // ✅ VERIFICAR se assignment foi bem-sucedida
        if (!result || result.success === false) {
          throw new Error(
            `Assignment failed for user ${userId}: ${
              result?.error?.message || "Unknown error"
            }`
          );
        }

        return { userId, success: true, data: result.data };
      } catch (error) {
        console.error(`❌ Error assigning manager to user ${userId}:`, error);
        return { userId, success: false, error: error.message };
      }
    });

    // ✅ AGUARDAR todas as atribuições
    console.log("⏳ Waiting for all assignments to complete...");
    const assignmentResults = await Promise.all(assignmentPromises);

    const successfulAssignments = assignmentResults.filter((r) => r.success);
    const failedAssignments = assignmentResults.filter((r) => !r.success);

    console.log("📊 Assignment summary:", {
      total: userIds.length,
      successful: successfulAssignments.length,
      failed: failedAssignments.length,
      successfulUsers: successfulAssignments.map((r) => r.userId),
      failedUsers: failedAssignments.map((r) => r.userId),
    });

    return {
      success: failedAssignments.length === 0,
      message:
        failedAssignments.length === 0
          ? `Manager assigned successfully to ${successfulAssignments.length} user(s)`
          : `Manager assigned to ${successfulAssignments.length} user(s), ${failedAssignments.length} failed`,
      data: {
        promotedManager: promotionResult?.data?.data || currentUser,
        successfulAssignments: successfulAssignments,
        failedAssignments: failedAssignments,
        totalRequested: userIds.length,
        totalSuccessful: successfulAssignments.length,
        totalFailed: failedAssignments.length,
      },
    };
  } catch (error) {
    console.error("❌ handleAssignManager - Error:", error);
    console.error("❌ Error stack:", error.stack);

    return {
      success: false,
      message: error.message || "Failed to assign manager",
      error: error,
      data: {
        totalRequested: assignmentData.userIds?.length || 0,
        totalSuccessful: 0,
        totalFailed: assignmentData.userIds?.length || 0,
        successfulAssignments: [],
        failedAssignments: [],
      },
    };
  }
};
