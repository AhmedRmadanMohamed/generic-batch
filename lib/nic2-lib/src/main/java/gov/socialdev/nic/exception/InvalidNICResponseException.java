package gov.socialdev.nic.exception;

public class InvalidNICResponseException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Invalid Id or Birthdate";
    public static final String DEFAULT_CODE = "INVALID_DATA";
    public InvalidNICResponseException() {
        super(DEFAULT_MESSAGE);
    }
    public InvalidNICResponseException(String errorAr) {
        super(errorAr);
    }
}
