package uz.ieltszone.ieltszoneuserservice.exceptions;

public class CustomDuplicateStudentException extends RuntimeException {

    public CustomDuplicateStudentException(String message) {
        super(message);
    }
}