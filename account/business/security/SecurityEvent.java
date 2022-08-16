package account.business.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Entity
@Table(name="security_events")
@Data
@NoArgsConstructor
public class SecurityEvent {
    @Id
    @GeneratedValue
    private long id;

    private LocalDateTime date;

    private EventName action;

    private String subject;

    private String object;

    private String path;

    public SecurityEvent(LocalDateTime date, EventName event, String subject, String object, String path) {
        this.date = date;
        this.action = event;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }
}
