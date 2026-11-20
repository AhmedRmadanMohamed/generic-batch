package gov.socialdev.spl.exception;

public class InvalidSPLResponseException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Invalid SPL response";
    public static final String DEFAULT_CODE = "INVALID_DATA";

    public InvalidSPLResponseException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidSPLResponseException(String message) {
        super(message);
    }
}
