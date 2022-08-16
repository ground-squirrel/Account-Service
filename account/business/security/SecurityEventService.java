package account.business.security;

import account.business.Account;
import account.business.AccountService;
import account.persistence.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SecurityEventService {

    @Autowired
    private SecurityEventRepository repository;

    @Autowired
    AccountService accountService;

    Map<String, Integer> loginFailMap = new HashMap<>();

    public void addLoginFail(String email) {
        if (loginFailMap.containsKey(email)) {
            loginFailMap.replace(email, loginFailMap.get(email) + 1);
        } else {
            loginFailMap.put(email, 1);
        }
    }

    public void clearLoginFails(String email) {
        if (loginFailMap.containsKey(email)) {
            loginFailMap.put(email, 0);
        }
    }

    public SecurityEvent saveEvent(SecurityEvent event) {
        return repository.save(event);
    }

    public List<SecurityEvent> getEvents() {
        return repository.findByOrderByIdAsc();
    }

    public boolean isBruteForceAttempt(SecurityEvent event) {
        List<SecurityEvent> list = repository.findBySubjectAndActionAndIdBetween(
                event.getSubject(), EventName.LOGIN_FAILED, event.getId() - 4, event.getId());

        return list.size() >= 5;
    }

    public boolean isBruteForceAttempt(String email) {
        return loginFailMap.getOrDefault(email, 0) >= 5;
    }

    @Transactional
    public void bruteForceLock(SecurityEvent event) {
        SecurityEvent bruteForceEvent = new SecurityEvent(
                event.getDate(),
                EventName.BRUTE_FORCE,
                event.getSubject(),
                event.getPath(),
                event.getPath());
        this.saveEvent(bruteForceEvent);

        Account account = accountService.findByEmail(event.getSubject());

        //Block if not an administrator
        if (null != account && !account.isAdministrator()) {
            SecurityEvent lockEvent = new SecurityEvent(
                    event.getDate(),
                    EventName.LOCK_USER,
                    event.getSubject(),
                    String.format("Lock user %s", event.getSubject()),
                    event.getPath());
            this.saveEvent(lockEvent);
            account.setLocked(true);
        }

    }
}
