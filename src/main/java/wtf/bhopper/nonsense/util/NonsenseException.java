package wtf.bhopper.nonsense.util;

public class NonsenseException extends RuntimeException {

    public NonsenseException(String message)  {
        super(message);
    }

    public NonsenseException(Throwable cause) {
        super(cause);
    }

    public NonsenseException(String message, Throwable cause) {
        super(message, cause);
    }

}
