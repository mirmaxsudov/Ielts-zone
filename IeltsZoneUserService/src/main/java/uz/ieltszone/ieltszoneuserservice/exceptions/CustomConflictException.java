package uz.ieltszone.ieltszoneuserservice.exceptions;

public class CustomConflictException extends RuntimeException{
    public CustomConflictException(String message) {
        super(message);
    }
}
