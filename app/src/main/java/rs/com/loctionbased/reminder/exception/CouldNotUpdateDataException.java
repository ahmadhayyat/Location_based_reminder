package rs.com.loctionbased.reminder.exception;


public class CouldNotUpdateDataException extends Exception {

    private static final String DEFAULT_MESSAGE = "Data could not be updated on the database.";

    public CouldNotUpdateDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
