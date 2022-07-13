package account.presentation;


import account.business.AccountWrapper;
import account.persistence.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BusinessController {

    @Autowired
    AccountRepository accountRepository;

    @GetMapping("/empl/payment")
    public AccountWrapper getEmployeePayrolls(@AuthenticationPrincipal UserDetails details) {
        return new AccountWrapper(accountRepository.findByEmail(details.getUsername().toLowerCase()));
    }

    @PostMapping("/acct/payments")
    public void updatePayrolls() {

    }

    @PutMapping("/acct/payments")
    public void updatePaymentInfo() {

    }
}
