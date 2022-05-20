package project.Codelivery.domain;

import project.Codelivery.domain.Messages;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.charset.Charset;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
@Slf4j
public class Exceptions {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Messages> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("handleMethodArgumentNotValidException", e);
        Messages messages = Messages.builder()
                .httpStatus(400)
                .message("MethodArgumentNotValidException")
                .data(null)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(messages, headers, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    protected ResponseEntity<Messages> handleHttpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error("handleHttpRequestMethodNotSupportedException", e);
        Messages messages = Messages.builder()
                .httpStatus(405)
                .message("HttpRequestMethodNotSupportedException")
                .data(null)
                .build();
        HttpHeaders headers =  new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(messages, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    protected ResponseEntity<Messages> IllegalArgumentException (IllegalArgumentException e) {
        log.error("IllegalArgumentException", e);
        Messages messages = Messages.builder()
                .httpStatus(500)
                .message("IllegalArgumentException" + e.getMessage())
                .data(null)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity(messages, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Messages> handleException(Exception e) {
        log.error("handleEntityNotFoundException", e);
        Messages messages = Messages.builder()
                .httpStatus(520)
                .message("handleEntityNotFoundException : unknown exception")
                .data(null)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));
        return new ResponseEntity<>(messages, HttpStatus.SERVICE_UNAVAILABLE);
        //service_unavaliable?
    }

}