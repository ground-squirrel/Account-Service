package account.persistence;

import account.business.Account;
import org.springframework.data.repository.CrudRepository;

public interface AccountRepository extends CrudRepository<Account, Long> {

    default Account addAccount(Account account) {
        return this.save(account);
    }

    Account findByEmail(String email);
}
