package account.business.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;


@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    SecurityEventService securityEventService;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response
            , AccessDeniedException accessDeniedException) throws IOException {

        //

        SecurityEvent event = new SecurityEvent(
                LocalDateTime.now(),
                EventName.ACCESS_DENIED,
                null != request.getRemoteUser() ? request.getRemoteUser() : "Anonymous",
                request.getServletPath(),
                request.getServletPath());
        securityEventService.saveEvent(event);

        response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied!");

    }

}
