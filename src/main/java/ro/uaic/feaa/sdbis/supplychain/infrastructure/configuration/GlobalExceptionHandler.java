package ro.uaic.feaa.sdbis.supplychain.infrastructure.configuration;

import com.stripe.exception.SignatureVerificationException;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ro.uaic.feaa.sdbis.supplychain.shared.exception.DomainValidationException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * Global REST Exception Handler conform specificatiei RFC 7807 (ProblemDetail) (FIX REST-02).
 * Pastreaza extensii de proprietati ("error", "fieldErrors", "timestamp") pentru compatibilitate 100%.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation Failed");
        pd.setTitle("Validation Failed");
        pd.setProperty("error", "Validation Failed");
        pd.setProperty("timestamp", Instant.now());

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        pd.setProperty("fieldErrors", fieldErrors);
        return pd;
    }

    @ExceptionHandler(ServletRequestBindingException.class)
    public ProblemDetail handleServletRequestBinding(ServletRequestBindingException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Bad Request");
        pd.setProperty("error", "Bad Request");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Constraint Violation");
        pd.setProperty("error", "Constraint Violation");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(DomainValidationException.class)
    public ProblemDetail handleDomainValidation(DomainValidationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle(ex.getCodEroare());
        pd.setProperty("error", ex.getCodEroare());
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Bad Request");
        pd.setProperty("error", "Bad Request");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(IllegalStateException.class)
    public ProblemDetail handleIllegalState(IllegalStateException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setTitle("Unprocessable Entity");
        pd.setProperty("error", "Unprocessable Entity");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ProblemDetail handleNoSuchElement(NoSuchElementException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setTitle("Not Found");
        pd.setProperty("error", "Not Found");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(BindException.class)
    public ProblemDetail handleBindException(BindException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation Failed");
        pd.setTitle("Validation Failed");
        pd.setProperty("error", "Validation Failed");
        pd.setProperty("timestamp", Instant.now());

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        pd.setProperty("fieldErrors", fieldErrors);
        return pd;
    }

    @ExceptionHandler(SignatureVerificationException.class)
    public ProblemDetail handleStripeSignatureVerification(SignatureVerificationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Invalid Stripe Signature");
        pd.setProperty("error", "Invalid Stripe Signature");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, ex.getMessage());
        pd.setTitle("Unauthorized");
        pd.setProperty("error", "Unauthorized");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler({SecurityException.class, AccessDeniedException.class})
    public ProblemDetail handleSecurityException(Exception ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Acces interzis.";
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, msg);
        pd.setTitle("Forbidden");
        pd.setProperty("error", "Forbidden");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler({OptimisticLockingFailureException.class, OptimisticLockException.class})
    public ProblemDetail handleOptimisticLockException(Exception ex) {
        log.warn("Conflict de concurenta detectat: {}", ex.getMessage());
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Resursa a fost modificata de o alta tranzactie concurenta. Va rugam reincarcati si reincercati operatiunea."
        );
        pd.setTitle("Conflict de Concurenta");
        pd.setProperty("error", "Conflict");
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(DataAccessException.class)
    public ProblemDetail handleDataAccessException(DataAccessException ex) {
        String incidentId = UUID.randomUUID().toString();
        log.error("[Incident {}] Database access exception: {}", incidentId, ex.getMessage(), ex);

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "A aparut o eroare la nivelul bazei de date. Referinta incident: " + incidentId
        );
        pd.setTitle("Database Error");
        pd.setProperty("error", "Database Error");
        pd.setProperty("incidentId", incidentId);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        String incidentId = UUID.randomUUID().toString();
        log.error("[Incident {}] Unhandled server exception: {}", incidentId, ex.getMessage(), ex);

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "A aparut o eroare interna neasteptata. Referinta incident: " + incidentId
        );
        pd.setTitle("Internal Server Error");
        pd.setProperty("error", "Internal Server Error");
        pd.setProperty("incidentId", incidentId);
        pd.setProperty("timestamp", Instant.now());
        return pd;
    }
}
