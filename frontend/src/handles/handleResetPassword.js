
export const handleResetPassword = async (newPassword) => {
    try {
        const response = await fetch("/api/reset-password", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(newPassword),
        });
    
        if (!response.ok) {
        throw new Error("Error resetting password");
        }
    
        const data = await response.json();
        return { success: true, message: data.message };
    } catch (error) {
        console.error("Error resetting password:", error);
        return { success: false, message: error.message };
    }
    };

export default handleResetPassword;