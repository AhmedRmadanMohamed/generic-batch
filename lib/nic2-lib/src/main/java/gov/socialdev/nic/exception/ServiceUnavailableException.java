package gov.socialdev.nic.exception;

public class ServiceUnavailableException extends  RuntimeException{
    public static final String DEFAULT_MESSAGE = "External System Down";
    public static final String DEFAULT_CODE = "SERVICE_UNAVAILABLE";
    public ServiceUnavailableException(){
        super(DEFAULT_MESSAGE);
    }
    public ServiceUnavailableException(String message){
        super(message);
    }
}
