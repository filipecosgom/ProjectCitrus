package pt.uc.dei.dtos;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import jakarta.validation.constraints.NotBlank;
import pt.uc.dei.enums.AccountState;

public class UserResponseDTO {
    private Long id;
    @NotBlank(message = "Email missing")
    private String email;
    private Boolean isAdmin;
    private Boolean isManager;
    private AccountState accountState;

    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String email, Boolean isAdmin, Boolean isManager, AccountState accountState) {
        this.id = id;
        this.email = email;
        this.isAdmin = isAdmin;
        this.isManager = isManager;
        this.accountState = accountState;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public AccountState getAccountState() {
        return accountState;
    }

    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }
}
