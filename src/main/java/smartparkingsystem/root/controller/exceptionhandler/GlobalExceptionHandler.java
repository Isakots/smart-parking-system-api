package smartparkingsystem.root.controller.exceptionhandler;

import com.google.gson.Gson;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import smartparkingsystem.root.exception.SensorDataNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SensorDataNotFoundException.class)
    protected ResponseEntity<?> handleDataNotFound(SensorDataNotFoundException e) {
        return new ResponseEntity<>(new Gson().toJson(new Message(e.getMessage())), null, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?> handleUnknownException() {
        return new ResponseEntity<>(new Gson()
                .toJson(new Message("Unknown error occured.")), null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
