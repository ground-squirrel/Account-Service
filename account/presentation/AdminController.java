package account.presentation;

import account.business.*;
import account.persistence.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    AccountService accountService;

    @Autowired
    GroupRepository groupRepository;

    @PutMapping("/user/role")
    @Transactional
    public AccountWrapper changeUserRoles(@RequestBody RoleChange change) {
        Account account = accountService.findByEmail(change.getUser());

        if (null == account) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        Group group = groupRepository.findByName(change.getFullRoleName());

        if (null == group) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }

        if (ROLE_OPERATIONS.GRANT == change.getOperation()) {
            //administrative user can't be granted a business role or vice versa,
            if ((account.isAdministrator() && !change.getRole().equals("ADMINISTRATOR"))
            || (!account.isAdministrator() && change.getRole().equals("ADMINISTRATOR"))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST
                        , "The user cannot combine administrative and business roles!");
            }
            account.addGroup(group);
        } else if (ROLE_OPERATIONS.REMOVE == change.getOperation()) {
            //can't remove the role the account doesn't have
            if (account.hasRole(group)) {
                //can't remove the Administrator role
                if (change.getFullRoleName().equals("ROLE_ADMINISTRATOR")) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST
                            , "Can't remove ADMINISTRATOR role!");
                }

                //can't remove the only role
                if (account.hasOnlyOneRole()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST
                            , "The user must have at least one role!");
                } else {
                    account.removeGroup(group);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
            }
        }

        //account is persisted automatically thanks to Transactional annotation

        return new AccountWrapper(accountService.findByEmail(change.getUser()));
    }

    @DeleteMapping("/user/{email}")
    @Transactional
    public Map<String, String> deleteUser(@PathVariable String email) {
        Account account = accountService.findByEmail(email);
        if (null == account) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        } else if(account.isAdministrator()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        } else {
            accountService.deleteAccount(account);
        }

        return Map.of("user", email, "status", "Deleted successfully!");
    }

    @GetMapping("/user")
    public List<AccountWrapper> getAllUserInfo() {
        return accountService.getAllAccounts();
    }
}
