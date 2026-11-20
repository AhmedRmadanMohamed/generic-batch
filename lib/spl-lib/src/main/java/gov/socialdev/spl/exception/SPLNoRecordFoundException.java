package gov.socialdev.spl.exception;

public class SPLNoRecordFoundException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "No address record found";
    public static final String DEFAULT_CODE = "NO_RECORD_FOUND";

    public SPLNoRecordFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public SPLNoRecordFoundException(String message) {
        super(message);
    }
}
