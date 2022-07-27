package account.business;

import lombok.Data;

@Data
public class RoleChange {

    private String user;
    private String role;
    private ROLE_OPERATIONS operation;

    public String getUser() {
        return this.user.toLowerCase();
    }

    public void setUser(String user) {
        this.user = user.toLowerCase();
    }

    public String getFullRoleName() {
        return String.format("ROLE_%s", this.role);
    }
}

;

