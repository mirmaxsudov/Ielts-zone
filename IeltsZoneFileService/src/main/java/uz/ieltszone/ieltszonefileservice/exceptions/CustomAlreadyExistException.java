package uz.ieltszone.ieltszonefileservice.exceptions;

public class CustomAlreadyExistException extends RuntimeException {
    public CustomAlreadyExistException(String message) {
        super(message);
    }
}
