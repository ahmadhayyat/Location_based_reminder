package rs.com.loctionbased.reminder.exception;

public class CouldNotGetDataException extends Exception {

    private static final String DEFAULT_MESSAGE = "Data could not be fetched from the database.";

    public CouldNotGetDataException(String message) {
        super(message);
    }
    public CouldNotGetDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
