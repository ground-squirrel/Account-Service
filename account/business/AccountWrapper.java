package account.business;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AccountWrapper {

    private long id;
    private String name;

    private String lastname;

    private String email;

    @JsonSerialize(using = RolesWrapperSerializer.class)
    private List<String> roles;

    public AccountWrapper(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.lastname = account.getLastname();
        this.email = account.getEmail();
        this.roles = account.getRoleNames();
    }

}
