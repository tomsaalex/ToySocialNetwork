package socialnetwork.toysocialnetwork.domain.validators;

/**
 * An exception to be thrown when an invalid object is found.
 */
public class ValidationException extends RuntimeException {
    public ValidationException(){}
    public ValidationException(String message)
    {
        super(message);
    }
    public ValidationException(Throwable cause)
    {
        super(cause);
    }
    public ValidationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
