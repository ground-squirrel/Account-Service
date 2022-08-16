package account.business.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Base64;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Autowired
    SecurityEventService securityEventService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response
            , AuthenticationException authException) throws IOException {

//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

        //Substring of the authorization header with "Basic ..." value
        String authHeader = request.getHeader("authorization");

        if (null != authHeader) {
            String credentials = authHeader.substring(6);
            String[] decoded = new String(Base64.getDecoder().decode(credentials)).split(":");

            if (2 == decoded.length) {
                String subject = decoded[0];

                //No security events after account was locked
                if (!authException.getMessage().equals("User account is locked")) {

                    SecurityEvent event = new SecurityEvent(
                            LocalDateTime.now(),
                            EventName.LOGIN_FAILED,
                            subject,
                            request.getServletPath(),
                            request.getServletPath());
                    SecurityEvent eventAdded = securityEventService.saveEvent(event);

                    securityEventService.addLoginFail(subject);

                    if (securityEventService.isBruteForceAttempt(eventAdded.getSubject())) {
                        securityEventService.bruteForceLock(eventAdded);
                    }
                }
            }
        }

        resolver.resolveException(request, response, null, authException);
    }

}