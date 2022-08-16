package account.presentation;

import account.business.*;
import account.business.security.EventName;
import account.business.security.SecurityEvent;
import account.business.security.SecurityEventService;
import account.persistence.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    AccountService accountService;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    SecurityEventService securityEventService;

    @PutMapping("/user/role")
    @Transactional
    public AccountWrapper changeUserRoles(@RequestBody RoleChange change,
                                          @AuthenticationPrincipal UserDetails details) {
        Account account = accountService.findByEmail(change.getUser());

        if (null == account) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }

        Group group = groupRepository.findByName(change.getFullRoleName());

        if (null == group) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }

        if (RoleOperations.GRANT == change.getOperation()) {
            //administrative user can't be granted a business role or vice versa,
            if ((account.isAdministrator() && !change.getRole().equals("ADMINISTRATOR"))
            || (!account.isAdministrator() && change.getRole().equals("ADMINISTRATOR"))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST
                        , "The user cannot combine administrative and business roles!");
            }
            account.addGroup(group);

            SecurityEvent event = new SecurityEvent(
                    LocalDateTime.now(),
                    EventName.GRANT_ROLE,
                    details.getUsername(),
                    String.format("Grant role %s to %s", change.getRole(), account.getEmail()),
                    "/api/admin/user/role");
            securityEventService.saveEvent(event);

        } else if (RoleOperations.REMOVE == change.getOperation()) {
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

                    SecurityEvent event = new SecurityEvent(
                            LocalDateTime.now(),
                            EventName.REMOVE_ROLE,
                            details.getUsername(),
                            String.format("Remove role %s from %s", change.getRole(), account.getEmail()),
                            "/api/admin/user/role");
                    securityEventService.saveEvent(event);

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
    public Map<String, String> deleteUser(@PathVariable String email,
                                          @AuthenticationPrincipal UserDetails details) {
        Account account = accountService.findByEmail(email);
        if (null == account) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        } else if(account.isAdministrator()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        } else {
            accountService.deleteAccount(account);

            SecurityEvent event = new SecurityEvent(
                    LocalDateTime.now(),
                    EventName.DELETE_USER,
                    details.getUsername(),
                    account.getEmail(),
                    "/api/admin/user");
            securityEventService.saveEvent(event);
        }

        return Map.of("user", email, "status", "Deleted successfully!");
    }

    @GetMapping("/user")
    public List<AccountWrapper> getAllUserInfo() {
        return accountService.getAllAccounts();
    }

    @PutMapping("/user/access")
    @Transactional
    public Map<String, String> changeAccess(@RequestBody AccessChange change,
                                            @AuthenticationPrincipal UserDetails details) {

        String email = change.getUser().toLowerCase();

        Account account = accountService.findByEmail(email);
        boolean locking;

        switch (change.getOperation()) {
            case "LOCK": {
                if (account.isAdministrator()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
                }

                account.setLocked(true);
                locking = true;
                break;
            }
            case "UNLOCK": {
                account.setLocked(false);
                locking = false;
                securityEventService.clearLoginFails(account.getEmail());
                break;
            }
            default:
                throw new UnsupportedOperationException();
        }

        SecurityEvent event = new SecurityEvent(
                LocalDateTime.now(),
                locking ? EventName.LOCK_USER : EventName.UNLOCK_USER,
                details.getUsername(),
                locking ?
                        String.format("Lock user %s", email)
                        : String.format("Unlock user %s", email),
                "/api/admin/user");
        securityEventService.saveEvent(event);

        String status = locking ?
                "User " + email + " locked!" :
                "User " + email + " unlocked!";

        return Map.of("status", status);
    }

}
