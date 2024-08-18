package uz.ieltszone.ieltszonefileservice.exceptions;

public class CustomInvalidFileException extends RuntimeException {
    public CustomInvalidFileException(String message) {
        super(message);
    }
}