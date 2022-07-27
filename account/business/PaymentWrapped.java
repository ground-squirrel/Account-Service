package account.business;

import lombok.Data;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Data
public class PaymentWrapped {
    /**
     * user name
     */
    private String name;
    /**
     * user lastname
     */
    private String lastname;
    /**
     * name of month-YYYY
     */
    private String period;
    /**
     * X dollar(s) Y cent(s)
     */
    private String salary;

    public PaymentWrapped(Account user, Payment payment) {
        this.name = user.getName();
        this.lastname = user.getLastname();
//        this.period = YearMonth.parse(payment.getPeriod(), DateTimeFormatter.ofPattern("MM-yyyy"))
//                .format(DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.US));
        this.period = payment.getPeriod()
                .format(DateTimeFormatter.ofPattern("MMMM-yyyy", Locale.US));
        long salaryLong = payment.getSalary();
        this.salary = String.format("%d dollar(s) %d cent(s)", salaryLong/100, salaryLong % 100);
    }
}
