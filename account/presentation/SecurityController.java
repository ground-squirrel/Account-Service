package account.presentation;

import account.business.security.SecurityEvent;
import account.business.security.SecurityEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SecurityController {
    @Autowired
    SecurityEventService securityEventService;

    @GetMapping("/api/security/events")
    public List<SecurityEvent> getAllSecurityEvents() {
        return securityEventService.getEvents();
    }
}
