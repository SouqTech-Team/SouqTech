package org.stand.springbootecommerce.error.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.stand.springbootecommerce.dto.response.ErrorBaseResponseBody;
import org.stand.springbootecommerce.error.*;
import java.util.*;

@ControllerAdvice
@ResponseStatus
@RequiredArgsConstructor
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    /*
     * Exception: AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(
            AccessDeniedException exception,
            WebRequest request) {
        Locale locale = Locale.getDefault();
        ErrorBaseResponseBody responseBody = new ErrorBaseResponseBody(
                HttpStatus.FORBIDDEN,
                messageSource.getMessage("user.access.error.denied", null, Objects.requireNonNull(locale)));

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody);
    }

    /*
     * Exception: BadCredentialsException
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBaseException(
            BadCredentialsException exception,
            WebRequest request) {
        Locale locale = Locale.getDefault();
        ErrorBaseResponseBody responseBody = new ErrorBaseResponseBody(
                HttpStatus.BAD_REQUEST,
                messageSource.getMessage("user.authentication.error", null, Objects.requireNonNull(locale)));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    /*
     * Exception: BaseException
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<Object> handleBaseException(
            BaseException exception,
            WebRequest request) {
        String messageCode = null;
        List<Object> messageArgs = new ArrayList<>();

        if (exception.getClass().equals(UserEmailAlreadyTakenException.class)) {
            messageCode = "user.register.error.field.taken";
            messageArgs.add("EMAIL");
        } else if (exception.getClass().equals(UserUsernameAlreadyTakenException.class)) {
            messageCode = "user.register.error.field.taken";
            messageArgs.add("USERNAME");
        } else if (exception.getClass().equals(UserNotFoundException.class)) {
            messageCode = "user.register.error.not.found";
            messageArgs.add("USER");
        }

        Locale locale = Locale.getDefault();
        String message = messageCode != null ? messageSource.getMessage(messageCode, messageArgs.toArray(), locale)
                : exception.getMessage();

        ErrorBaseResponseBody responseBody = new ErrorBaseResponseBody(
                HttpStatus.BAD_REQUEST,
                message);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    /*
     * Exception: MethodArgumentNotValidException
     */
    @SuppressWarnings("java:S2638") // Spring Framework 6 contract is correctly implemented with package-level
                                    // @NonNullApi
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        // MethodArgumentNotValidException to Map<fieldname, List<errormessage>>
        Map<String, List<String>> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            if (!errors.containsKey(fieldName)) {
                errors.put(fieldName, new ArrayList<>());
            }
            errors.get(fieldName).add(errorMessage);
        });

        ErrorBaseResponseBody responseBody = new ErrorBaseResponseBody(
                HttpStatus.BAD_REQUEST,
                errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    /*
     * Exception: Exception (generic exceptions handler)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(
            Exception exception,
            WebRequest request) {
        ErrorBaseResponseBody responseBody = new ErrorBaseResponseBody(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }
}
