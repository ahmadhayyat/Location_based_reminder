package rs.com.loctionbased.reminder.exception;

public class PlaceNotFoundException extends Exception {

    private static final String DEFAULT_MESSAGE = "Place not found in the database.";

    public PlaceNotFoundException(String message) {
        super(message);
    }

}
