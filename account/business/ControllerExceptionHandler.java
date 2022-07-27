package account.business;

import account.business.security.CustomErrorMessage;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<CustomErrorMessage> handleAuthExceptions(
            AuthenticationException e, HttpServletRequest request) {

        CustomErrorMessage body = new CustomErrorMessage(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "",
                request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<CustomErrorMessage> handleAccessDeniedExceptions(
            AccessDeniedException e, HttpServletRequest request) {

        CustomErrorMessage body = new CustomErrorMessage(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "Access Denied!",
                request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomErrorMessage> handleValidationExceptions(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        String message = "";
        BindingResult bindingResult = e.getBindingResult();

        //Account validation errors
        if (bindingResult.getObjectName().equals("account")) {
            //non-valid password
            List<FieldError> passwordList = bindingResult.getFieldErrors("password");
            if (passwordList.size() > 0) {
                //too short password
                if (passwordList.stream().map(DefaultMessageSourceResolvable::getCodes)
                        .filter(Objects::nonNull).flatMap(Arrays::stream)
                        .anyMatch(code -> code.contains("Size.account.password"))) {
                    message = "The password length must be at least 12 chars!";
                }
            }

            //non-valid email
            List<FieldError> emailList = bindingResult.getFieldErrors("email");
            if (emailList.size() > 0) {
                //empty email
                if (emailList.stream().map(DefaultMessageSourceResolvable::getCodes)
                        .filter(Objects::nonNull).flatMap(Arrays::stream)
                        .anyMatch(code -> code.contains("NotEmpty.account.email"))) {
                    message = "The email field is absent!";
                }

                //wrong email domain
                if (emailList.stream().map(DefaultMessageSourceResolvable::getCodes)
                        .filter(Objects::nonNull).flatMap(Arrays::stream)
                        .anyMatch(code -> code.contains("Pattern.account.email"))) {
                    message = "The email is in the wrong domain!";
                }
            }
        }

        //Payment validation errors
        if (bindingResult.getObjectName().equals("payment")) {
            //non-valid period
            List<FieldError> periodList = bindingResult.getFieldErrors("period");
            if (periodList.size() > 0) {
                String rejected = periodList.stream()
                        .filter(
                                error -> Arrays.stream(
                                                Objects.requireNonNull(error.getCodes()))
                                        .anyMatch(code -> code.contains("Pattern.payment.period")
                                        )
                        )
                        .map(FieldError::getRejectedValue)
                        .map(String::valueOf)
                        .collect(Collectors.joining(", "));
                if (rejected.length() > 0) {
                    message = String.format("Wrong payment periods: %s", rejected);
                }
            }

            //non-valid salary value
            List<FieldError> salaryList = bindingResult.getFieldErrors("salary");
            if (salaryList.size() > 0) {
                if (salaryList.stream().map(DefaultMessageSourceResolvable::getCodes)
                        .filter(Objects::nonNull).flatMap(Arrays::stream)
                        .anyMatch(code -> code.contains("Min.payment.salary"))) {
                    message = "Salary must be non negative!";
                }
            }
        }

        CustomErrorMessage body = new CustomErrorMessage(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomErrorMessage> handleConstraintExceptions(
            ConstraintViolationException e, HttpServletRequest request) {

        String message = e.getMessage();

        CustomErrorMessage body = new CustomErrorMessage(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomErrorMessage> handleConstraintExceptions(
            org.hibernate.exception.ConstraintViolationException e, HttpServletRequest request) {

        String message = "";

        if (e.getConstraintName().contains("PUBLIC.PAYMENTS(EMPLOYEE, PERIOD)")) {
            message = "Duplicated periods!";
        }

        CustomErrorMessage body = new CustomErrorMessage(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonMappingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomErrorMessage> handleConstraintExceptions(
            JsonMappingException e, HttpServletRequest request) {

        String message = "";

        if (e.getMessage().contains("Invalid value for MonthOfYear")) {
            message = "Wrong date!";
        }

        CustomErrorMessage body = new CustomErrorMessage(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                request.getRequestURI());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
