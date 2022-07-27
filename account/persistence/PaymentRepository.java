package account.persistence;

import account.business.Payment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;

@Repository
public interface PaymentRepository extends CrudRepository<Payment, Long> {

    Payment findByEmployeeAndPeriod(String employee, YearMonth period);

    List<Payment> findByEmployeeOrderByPeriodDesc(String employee);
}
