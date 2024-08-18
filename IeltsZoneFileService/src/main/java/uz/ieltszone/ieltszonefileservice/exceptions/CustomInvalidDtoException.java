package uz.ieltszone.ieltszonefileservice.exceptions;

public class CustomInvalidDtoException extends RuntimeException {
    public CustomInvalidDtoException(String message) {
        super(message);
    }
}