package account.business;

import account.persistence.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    AccountRepository repository;

    public List<AccountWrapper> getAllAccounts() {
        return repository.findByOrderByIdAsc().stream().
                map(AccountWrapper::new).collect(Collectors.toList());
    }

    public Account findByEmail(String email) {
        return repository.findByEmail(email.toLowerCase());
    }

    public void deleteAccount(Account account) {
        repository.delete(account);
    }
}
