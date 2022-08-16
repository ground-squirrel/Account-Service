package account.persistence;

import account.business.security.EventName;
import account.business.security.SecurityEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecurityEventRepository extends CrudRepository<SecurityEvent, Long> {

    List<SecurityEvent> findByOrderByIdAsc();

    List<SecurityEvent> findBySubjectAndActionAndIdBetween(String email, EventName action, long from, long to);
}
