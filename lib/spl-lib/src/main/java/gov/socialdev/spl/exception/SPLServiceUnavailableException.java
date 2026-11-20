package gov.socialdev.spl.exception;

public class SPLServiceUnavailableException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Saudi Post service unavailable";
    public static final String DEFAULT_CODE = "SERVICE_UNAVAILABLE";

    public SPLServiceUnavailableException() {
        super(DEFAULT_MESSAGE);
    }

    public SPLServiceUnavailableException(String message) {
        super(message);
    }
}
