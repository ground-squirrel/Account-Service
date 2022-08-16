package account.business;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class AccessChange {

    @NotEmpty
    private String user;

    private String operation;
}
