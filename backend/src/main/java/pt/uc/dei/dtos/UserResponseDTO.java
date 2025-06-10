package pt.uc.dei.dtos;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import jakarta.validation.constraints.NotBlank;

public class UserResponseDTO {
    @NotBlank(message = "Email missing")
    private String email;
    private Boolean isAdmin;
    private Boolean isManager;

    public UserResponseDTO() {}

    public UserResponseDTO(String email, Boolean isAdmin, Boolean isManager) {
        this.email = email;
        this.isAdmin = isAdmin;
        this.isManager = isManager;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getManager() {
        return isManager;
    }

    public void setManager(Boolean manager) {
        isManager = manager;
    }
}
