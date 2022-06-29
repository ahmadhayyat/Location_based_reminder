package rs.com.loctionbased.reminder.exception;


public class CouldNotInsertDataException extends Exception {

    private static final String DEFAULT_MESSAGE = "Could not insert data into the database.";

    public CouldNotInsertDataException(String message) {
        super(message);
    }
    public CouldNotInsertDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
