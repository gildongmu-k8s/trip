package gildongmu.trip.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static gildongmu.trip.exception.ErrorCode.REQUEST_ARGUMENT_NOT_VALID;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final String LOG_FORMAT = "Class : {}, Code : {}, Message : {}";

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponse<List<String>>> handleUserException(ConstraintViolationException e) {
        List<String> messages = e.getConstraintViolations()
                .stream().map(ConstraintViolation::getMessage).collect(Collectors.toList());
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), REQUEST_ARGUMENT_NOT_VALID, REQUEST_ARGUMENT_NOT_VALID.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse<>(REQUEST_ARGUMENT_NOT_VALID.name(), messages));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionResponse<String>> handleUserException(UserException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode(), e.getErrorCode().getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(new ExceptionResponse<>(e.getErrorCode().name(), e.getErrorCode().getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse<List<String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> messages = e.getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        log.info(LOG_FORMAT, e.getClass().getSimpleName(), REQUEST_ARGUMENT_NOT_VALID, REQUEST_ARGUMENT_NOT_VALID.getMessage());
        return ResponseEntity.status(e.getStatusCode())
                .body(new ExceptionResponse<>(REQUEST_ARGUMENT_NOT_VALID.name(), messages));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse<String>> handleException(Exception e) {
        log.error(LOG_FORMAT, e.getClass().getSimpleName(), HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        return ResponseEntity.internalServerError()
                .body(new ExceptionResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.name(), e.getMessage()));
    }
}
