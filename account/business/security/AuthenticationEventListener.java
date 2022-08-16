package account.business.security;

import account.business.AccountDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationEventListener implements
        ApplicationListener<AuthenticationSuccessEvent> {

    @Autowired
    SecurityEventService securityEventService;

    @Override
    public void onApplicationEvent (AuthenticationSuccessEvent event) {

        securityEventService.clearLoginFails(event.getAuthentication().getName());
    }
}
