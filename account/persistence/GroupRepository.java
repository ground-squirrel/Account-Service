package account.persistence;

import account.business.Group;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group, Long> {

    Group findByName(String name);

    boolean existsBy();
}
