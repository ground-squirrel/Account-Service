package account.business;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "payments"
        , uniqueConstraints = { @UniqueConstraint(columnNames = { "employee", "period" }) })
@Data
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;

    /**
     * User email
     */
    private String employee;


    /**
     * mm-YYYY format
     */
//    @Pattern(regexp = "(0[1-9]|1[0-2])-\\d{4}", message = "Wrong date!")
//    private String period;

    private YearMonth period;

    public void setPeriod(String input) {
        this.period = YearMonth.parse(input, DateTimeFormatter.ofPattern("MM-yyyy"));
    }

    /**
     * Salary in cents
     */
    @Min(value = 0, message = "Salary must be non negative!")
    private long salary;
}
