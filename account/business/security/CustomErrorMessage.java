package account.business.security;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CustomErrorMessage {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}