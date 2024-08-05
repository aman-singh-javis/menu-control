package ai.javis.menucontrol.handlers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import ai.javis.menucontrol.dto.ApiResponse;
import ai.javis.menucontrol.exception.CompanyAlreadyExists;
import ai.javis.menucontrol.exception.UserAlreadyExists;
import ai.javis.menucontrol.exception.UserNotFound;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidRequest(MethodArgumentNotValidException exception) {
        Map<String, Object> errorMap = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach((error) -> {
            errorMap.put(error.getField(), error.getDefaultMessage());
        });

        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFound.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFound exception) {
        ApiResponse<?> resp = new ApiResponse<>(exception.getMessage(), null);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExists.class)
    public ResponseEntity<?> handleUserAlreadyExistsException(UserAlreadyExists exception) {
        ApiResponse<?> resp = new ApiResponse<>(exception.getMessage(), null);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CompanyAlreadyExists.class)
    public ResponseEntity<?> handleCompanyAlreadyExistsException(CompanyAlreadyExists exception) {
        ApiResponse<?> resp = new ApiResponse<>(exception.getMessage(), null);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }
}
