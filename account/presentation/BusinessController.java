package account.presentation;


import account.business.*;
import account.business.security.UserNotFoundException;
import account.persistence.AccountRepository;
import account.persistence.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/api")
public class BusinessController {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    PaymentService paymentService;

    @GetMapping("/empl/payment")
    @Transactional
    public List<PaymentWrapped> getEmployeePayrolls(@AuthenticationPrincipal UserDetails details
            , @Nullable @RequestParam String period) {

        if (null != period) {
            if (period.matches("(0[1-9]|1[0-2])-\\d{4}")) {
                Account user = accountRepository.findByEmail(details.getUsername().toLowerCase());
                return List.of(paymentService.getPaymentInfo(user, period));
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong date!");
            }
        } else {
            Account user = accountRepository.findByEmail(details.getUsername().toLowerCase());
            return paymentService.getAllPaymentInfo(user);
        }
    }

    @PostMapping("/acct/payments")
    @Transactional
    public Map<String, String> updatePayrolls(@RequestBody List<@Valid Payment> payments) {

        for (Payment p : payments) {
            if (null != accountRepository.findByEmail(p.getEmployee())) {
                paymentRepository.save(p);
            } else {
                throw new UserNotFoundException(p.getEmployee());
            }
        }

        return Map.of("status", "Added successfully!");
    }

    @PutMapping("/acct/payments")
    @Transactional
    public Map<String, String> updatePaymentInfo(@Valid @RequestBody Payment payment) {

        if (null != accountRepository.findByEmail(payment.getEmployee())) {
            paymentService.updateSalary(payment);
        } else {
            throw new UserNotFoundException(payment.getEmployee());
        }

        return Map.of("status", "Updated successfully!");
    }
}
