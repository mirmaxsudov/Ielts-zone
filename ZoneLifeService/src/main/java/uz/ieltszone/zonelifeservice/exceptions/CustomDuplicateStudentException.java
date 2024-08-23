package uz.ieltszone.zonelifeservice.exceptions;

public class CustomDuplicateStudentException extends RuntimeException {

    public CustomDuplicateStudentException(String message) {
        super(message);
    }
}