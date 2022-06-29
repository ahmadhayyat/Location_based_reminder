package rs.com.loctionbased.reminder.exception;


public class CouldNotDeleteDataException extends Exception {

    private static final String DEFAULT_MESSAGE = "Data could not be deleted from the database.";

    public CouldNotDeleteDataException(String message, Throwable cause) {
        super(message, cause);
    }

}
