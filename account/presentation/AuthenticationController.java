package account.presentation;

import account.business.Account;
import account.business.AccountWrapper;
import account.business.security.UserAlreadyExistsException;
import account.persistence.AccountRepository;
import account.persistence.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    PasswordEncoder encoder;

    private static List<String> breachedPasswords = List.of("PasswordForJanuary"
            , "PasswordForFebruary", "PasswordForMarch", "PasswordForApril"
            , "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust"
            , "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");


    @PostMapping("/signup")
    @Transactional
    public AccountWrapper signup(@Valid @RequestBody Account account) {

        if (breachedPasswords.contains(account.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST
                    , "The password is in the hacker's database!");
        }

        account.setPassword(encoder.encode(account.getPassword()));

        if (accountRepository.existsBy()) {
            account.addGroup(groupRepository.findByName("ROLE_USER"));
        } else {
            account.addGroup(groupRepository.findByName("ROLE_ADMINISTRATOR"));
        }

        if (null == accountRepository.findByEmail(account.getEmail())) {
            return new AccountWrapper(accountRepository.addAccount(account));
        } else {
            throw new UserAlreadyExistsException();
        }

    }

    @PostMapping("/changepass")
    public Map<String, String> changePassword(@RequestBody Map<String, String> input
            , @AuthenticationPrincipal UserDetails details) {
        Account account = accountRepository.findByEmail(details.getUsername());

        String passwordNew = input.get("new_password");

        if (passwordNew.length() < 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST
                    , "Password length must be 12 chars minimum!");
        }

        if (encoder.matches(passwordNew, account.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST
                    , "The passwords must be different!");
        }

        if (breachedPasswords.contains(passwordNew)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST
                    , "The password is in the hacker's database!");
        }

        //all ok
        account.setPassword(encoder.encode(input.get("new_password")));
        accountRepository.save(account);

        return Map.of("email", account.getEmail()
                , "status", "The password has been updated successfully");
    }
}
