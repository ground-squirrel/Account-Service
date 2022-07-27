package account.persistence;

import account.business.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private GroupRepository groupRepository;

    @Autowired
    public DataLoader(GroupRepository groupRepository) {
        this.groupRepository = groupRepository;
        createRoles();
    }

    private void createRoles() {
        if (!groupRepository.existsBy()) {
            try {
                groupRepository.save(new Group("ROLE_ADMINISTRATOR"));
                groupRepository.save(new Group("ROLE_USER"));
                groupRepository.save(new Group("ROLE_ACCOUNTANT"));
            } catch (Exception e) {
                //
            }
        }
    }
}
