package uz.ieltszone.ieltszoneuserservice.exceptions;

public class CustomInvalidFileException extends RuntimeException {
    public CustomInvalidFileException(String message) {
        super(message);
    }
}