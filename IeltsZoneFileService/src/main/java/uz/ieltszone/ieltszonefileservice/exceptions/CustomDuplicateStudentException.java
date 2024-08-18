package uz.ieltszone.ieltszonefileservice.exceptions;

public class CustomDuplicateStudentException extends RuntimeException {

    public CustomDuplicateStudentException(String message) {
        super(message);
    }
}