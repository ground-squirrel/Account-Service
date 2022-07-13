package account.presentation;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PutMapping("/user/role")
    public void changeUserRoles() {

    }

    @DeleteMapping("/user")
    public void deleteUser() {

    }

    @GetMapping("/user")
    public void getAllUserInfo() {

    }
}
