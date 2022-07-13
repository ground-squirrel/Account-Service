package account.business;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AccountWrapper {

    private long id;
    private String name;

    private String lastname;

    private String email;

    public AccountWrapper(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.lastname = account.getLastname();
        this.email = account.getEmail();
    }

}
