package account.business;

import account.persistence.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PaymentService {

    @Autowired
    PaymentRepository repository;

    public PaymentWrapped getPaymentInfo(Account user, String period) {

        YearMonth datePeriod = YearMonth.parse(period, DateTimeFormatter.ofPattern("MM-yyyy"));

        Payment payment = repository.findByEmployeeAndPeriod(user.getEmail(), datePeriod);

        return new PaymentWrapped(user, payment);
    }

    public List<PaymentWrapped> getAllPaymentInfo(Account user) {
        List<Payment> payments = repository.findByEmployeeOrderByPeriodDesc(user.getEmail());

        return payments.stream()
                .map(p -> new PaymentWrapped(user, p))
                .collect(Collectors.toList());
    }


    public void updateSalary(Payment payment) {
        Payment saved = repository.findByEmployeeAndPeriod(
                payment.getEmployee()
                , payment.getPeriod()
        );

        saved.setSalary(payment.getSalary());

        repository.save(saved);
    }
}
