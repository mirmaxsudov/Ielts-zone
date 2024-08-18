package uz.ieltszone.ieltszonefileservice.exceptions;

public class CustomBadRequestException extends RuntimeException {
    public CustomBadRequestException(String message) {
        super(message);
    }
}
