package account.business;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SortNatural;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.*;
import java.util.stream.Collectors;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
public class Account {

    @Id
    @GeneratedValue
    private long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String lastname;

    @NotEmpty
    @Email
    @Pattern(regexp = ".+@acme.com", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String email;

    @NotEmpty
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    private String password;

    @SortNatural
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    }, fetch = FetchType.EAGER)
    @JoinTable(name = "account_groups",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"
            ))
    private Set<Group> accountGroups = new TreeSet<>();

    public String getEmail() {
        return email.toLowerCase();
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    public void addGroup(Group group) {
        accountGroups.add(group);
    }

    public void removeGroup(Group group) {
        accountGroups.remove(group);
    }

    public List<String> getRoleNames() {
        return accountGroups.stream().map(Group::getName).collect(Collectors.toList());
    }

    public boolean isAdministrator() {
        return this.getRoleNames().contains("ROLE_ADMINISTRATOR");
    }

    public boolean hasRole(Group group) {
        return this.getAccountGroups().contains(group);
    }

    public boolean hasOnlyOneRole() {
        return 1 == this.accountGroups.size();
    }

}
