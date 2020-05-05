package biz.netcentric.exceptions;

/**
 * Created by RafaelLopez on 2/21/17.
 */
public class SlightlyException extends RuntimeException {

    private String message;

    public SlightlyException() {
        super();
    }

    public SlightlyException(final Throwable cause) {
        super(cause);
    }

    public SlightlyException(final String message) {
        super(message);
    }

    public SlightlyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
