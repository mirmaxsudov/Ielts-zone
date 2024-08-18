package uz.ieltszone.ieltszonefileservice.exceptions;

public class CustomInternalErrorException extends RuntimeException{

    public CustomInternalErrorException(String message) {
        super(message);
    }
}
