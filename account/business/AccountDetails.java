package account.business;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AccountDetails implements UserDetails {

    private final long id;

    private final String name;

    private final String lastname;

    private final String email;

    private final String password;

    private final List<GrantedAuthority> rolesAndAuthorities;

    private boolean locked;

    public AccountDetails(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.lastname = account.getLastname();
        this.email = account.getEmail();
        this.password = account.getPassword();
        this.rolesAndAuthorities = account.getAccountGroups().stream()
                .map(Group::getName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        this.locked = account.isLocked();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return rolesAndAuthorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
