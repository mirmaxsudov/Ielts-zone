package uz.ieltszone.ieltszoneuserservice.exceptions;

import lombok.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import uz.ieltszone.ieltszoneuserservice.payload.ApiErrorResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        Map<String, String> validationErrors = new HashMap<>();
        List<ObjectError> validationErrorList = ex.getBindingResult().getAllErrors();


        validationErrorList.forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            validationErrors.put(fieldName, message);
        });
        return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(CustomNotFoundException.class)
    public ApiErrorResponse handleNotFoundException(CustomNotFoundException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now(), 404);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CustomConflictException.class)
    public ApiErrorResponse handleAlreadyExistException(CustomConflictException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.CONFLICT, LocalDateTime.now(), 409);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CustomBadRequestException.class)
    public ApiErrorResponse handleBadRequestException(CustomBadRequestException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now(), 400);
    }


    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    public ApiErrorResponse handleInvalidTokenException(InvalidTokenException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED, LocalDateTime.now(), 401);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(CustomInvalidDtoException.class)
    public ApiErrorResponse handleInvalidDtoException(CustomInvalidDtoException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY, LocalDateTime.now(), 422);
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(CustomInvalidFileException.class)
    public ApiErrorResponse handleInvalidFileException(CustomInvalidFileException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY, LocalDateTime.now(), 422);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(CustomAlreadyExistException.class)
    public ApiErrorResponse handleCustomAlreadyExistException(CustomAlreadyExistException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.CONFLICT, LocalDateTime.now(), 410);
    }

    @ResponseStatus(HttpStatus.FOUND)
    @ExceptionHandler(CustomDuplicateStudentException.class)
    public ApiErrorResponse handleCustomDuplicateStudentException(CustomDuplicateStudentException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.CONFLICT, LocalDateTime.now(), 411);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CustomInternalErrorException.class)
    public ApiErrorResponse handleInternalErrorException(CustomInternalErrorException exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), 500);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiErrorResponse handleException(Exception exception) {
        return new ApiErrorResponse(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now(), 500);
    }
}