package pt.uc.dei.dtos;

import jakarta.validation.constraints.NotBlank;
import pt.uc.dei.enums.AccountState;

public class UserResponseDTO {
    private Long id;
    @NotBlank(message = "Email missing")
    private String email;
    private Boolean userIsAdmin;
    private Boolean userIsManager;
    private AccountState accountState;

    public UserResponseDTO() {}

    public UserResponseDTO(Long id, String email, Boolean userIsAdmin, Boolean userIsManager, AccountState accountState) {
        this.id = id;
        this.email = email;
        this.userIsAdmin = userIsAdmin;
        this.userIsManager = userIsManager;
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

    public Boolean getUserIsAdmin() {
        return userIsAdmin;
    }

    public void setUserIsAdmin(Boolean userIsAdmin) {
        this.userIsAdmin = userIsAdmin;
    }

    public Boolean getUserIsManager() {
        return userIsManager;
    }

    public void setUserIsManager(Boolean userIsManager) {
        this.userIsManager = userIsManager;
    }

    public AccountState getAccountState() {
        return accountState;
    }

    public void setAccountState(AccountState accountState) {
        this.accountState = accountState;
    }
}
