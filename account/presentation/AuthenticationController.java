package account.presentation;

import account.business.Account;
import account.business.AccountWrapper;
import account.business.security.UserAlreadyExistsException;
import account.persistence.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder encoder;


    @PostMapping("/signup")
    public AccountWrapper signup(@Valid @RequestBody Account account) {
        account.setPassword(encoder.encode(account.getPassword()));


        if (null == accountRepository.findByEmail(account.getEmail())) {
            return new AccountWrapper(accountRepository.addAccount(account));
        } else {
            throw new UserAlreadyExistsException();
        }

    }

    @PostMapping("/changepass")
    public Account changePassword() {
        return null;
    }
}
