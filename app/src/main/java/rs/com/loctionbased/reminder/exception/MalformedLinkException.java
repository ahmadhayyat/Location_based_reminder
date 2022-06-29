package rs.com.loctionbased.reminder.exception;


public class MalformedLinkException extends Exception {

    private static final String DEFAULT_MESSAGE = "Link is invalid, malformed.";

    public MalformedLinkException(String message) {
        super(message);
    }

}
