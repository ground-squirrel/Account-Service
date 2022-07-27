package account.persistence;

import account.business.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    default Account addAccount(Account account) {
        return this.save(account);
    }

    Account findByEmail(String email);

    boolean existsBy();

    List<Account> findByOrderByIdAsc();

}
