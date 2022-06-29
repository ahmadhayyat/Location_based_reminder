package rs.com.loctionbased.reminder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import rs.com.loctionbased.reminder.util.FileUtil;

public class RemindyDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Remindy.db";
    private static final int DATABASE_VERSION = 2;
    private static final String COMMA_SEP = ", ";

    private String mAppDbFilepath;
    private String mDbExternalBackupFilepath;
    private Context mContext;

    public RemindyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mAppDbFilepath =  context.getDatabasePath(DATABASE_NAME).getPath();
        mDbExternalBackupFilepath = Environment.getExternalStorageDirectory().getPath() + "/" + DATABASE_NAME;
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createDatabase(sqLiteDatabase);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        deleteDatabase(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    public boolean exportDatabase() throws IOException {

        close();
        File appDatabase = new File(mAppDbFilepath);
        File backupDatabase = new File(mDbExternalBackupFilepath);

        if (appDatabase.exists()) {
            FileUtil.copyFile(new FileInputStream(appDatabase), new FileOutputStream(backupDatabase));
            return true;
        }
        return false;
    }

    public boolean importDatabase() throws IOException {

        close();
        File appDatabase = new File(mAppDbFilepath);
        File backupDatabase = new File(mDbExternalBackupFilepath);

        if (backupDatabase.exists()) {
            FileUtil.copyFile(new FileInputStream(backupDatabase), new FileOutputStream(appDatabase));

            getWritableDatabase().close();
            return true;
        }
        return false;
    }

    private void createDatabase(SQLiteDatabase sqLiteDatabase) {
        String statement;

        statement = "CREATE TABLE " + RemindyContract.OneTimeReminderTable.TABLE_NAME + " (" +
                RemindyContract.OneTimeReminderTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_TASK_FK.getName() + " " + RemindyContract.OneTimeReminderTable.COLUMN_NAME_TASK_FK.getDataType() +
                " REFERENCES " + RemindyContract.TaskTable.TABLE_NAME + "(" + RemindyContract.TaskTable._ID + ") " + COMMA_SEP +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_DATE.getName() + " " + RemindyContract.OneTimeReminderTable.COLUMN_NAME_DATE.getDataType() + COMMA_SEP +
                RemindyContract.OneTimeReminderTable.COLUMN_NAME_TIME.getName() + " " + RemindyContract.OneTimeReminderTable.COLUMN_NAME_TIME.getDataType() +
                " ); " ;
        sqLiteDatabase.execSQL(statement);

        statement = "CREATE TABLE " + RemindyContract.RepeatingReminderTable.TABLE_NAME + " (" +
                RemindyContract.RepeatingReminderTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_TASK_FK.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_TASK_FK.getDataType() +
                " REFERENCES " + RemindyContract.TaskTable.TABLE_NAME + "(" + RemindyContract.TaskTable._ID + ") " + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_DATE.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_DATE.getDataType() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_TIME.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_TIME.getDataType() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_TYPE.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_INTERVAL.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_INTERVAL.getDataType() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_TYPE.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_NUMBER_OF_EVENTS.getDataType() + COMMA_SEP +
                RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_DATE.getName() + " " + RemindyContract.RepeatingReminderTable.COLUMN_NAME_REPEAT_END_DATE.getDataType() +
                " ); " ;
        sqLiteDatabase.execSQL(statement);

        statement = "CREATE TABLE " + RemindyContract.PlaceTable.TABLE_NAME + " (" +
                RemindyContract.PlaceTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RemindyContract.PlaceTable.COLUMN_NAME_ALIAS.getName() + " " + RemindyContract.PlaceTable.COLUMN_NAME_ALIAS.getDataType() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_ADDRESS.getName() + " " + RemindyContract.PlaceTable.COLUMN_NAME_ADDRESS.getDataType() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_LATITUDE.getName() + " " + RemindyContract.PlaceTable.COLUMN_NAME_LATITUDE.getDataType() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_LONGITUDE.getName() + " " + RemindyContract.PlaceTable.COLUMN_NAME_LONGITUDE.getDataType() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_RADIUS.getName() + " " + RemindyContract.PlaceTable.COLUMN_NAME_RADIUS.getDataType() + COMMA_SEP +
                RemindyContract.PlaceTable.COLUMN_NAME_IS_ONE_OFF.getName() + " " + RemindyContract.PlaceTable.COLUMN_NAME_IS_ONE_OFF.getDataType() +
                " ); " ;
        sqLiteDatabase.execSQL(statement);

        statement = "CREATE TABLE " + RemindyContract.LocationBasedReminderTable.TABLE_NAME + " (" +
                RemindyContract.LocationBasedReminderTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                RemindyContract.LocationBasedReminderTable.COLUMN_NAME_TASK_FK.getName() + " " + RemindyContract.LocationBasedReminderTable.COLUMN_NAME_TASK_FK.getDataType() +
                " REFERENCES " + RemindyContract.TaskTable.TABLE_NAME + "(" + RemindyContract.TaskTable._ID + ") " + COMMA_SEP +
                RemindyContract.LocationBasedReminderTable.COLUMN_NAME_PLACE_FK.getName() + " " + RemindyContract.LocationBasedReminderTable.COLUMN_NAME_PLACE_FK.getDataType() +
                " REFERENCES " + RemindyContract.PlaceTable.TABLE_NAME + "(" + RemindyContract.PlaceTable._ID + ") " + COMMA_SEP +
                RemindyContract.LocationBasedReminderTable.COLUMN_NAME_TRIGGER_ENTERING.getName() + " " + RemindyContract.LocationBasedReminderTable.COLUMN_NAME_TRIGGER_ENTERING.getDataType() + COMMA_SEP +
                RemindyContract.LocationBasedReminderTable.COLUMN_NAME_TRIGGER_EXITING.getName() + " " + RemindyContract.LocationBasedReminderTable.COLUMN_NAME_TRIGGER_EXITING.getDataType() +
                " ); " ;
        sqLiteDatabase.execSQL(statement);

        statement = "CREATE TABLE " + RemindyContract.TaskTable.TABLE_NAME + " (" +
                RemindyContract.TaskTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_STATUS.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_STATUS.getDataType() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_TITLE.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_TITLE.getDataType() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_DESCRIPTION.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_DESCRIPTION.getDataType() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_CATEGORY.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_CATEGORY.getDataType() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_REMINDER_TYPE.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_REMINDER_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.TaskTable.COLUMN_NAME_DONE_DATE.getName() + " " + RemindyContract.TaskTable.COLUMN_NAME_DONE_DATE.getDataType() +
                " ); ";
        sqLiteDatabase.execSQL(statement);



        statement = "CREATE TABLE " + RemindyContract.AttachmentTable.TABLE_NAME + " (" +
                RemindyContract.AttachmentTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                RemindyContract.AttachmentTable.COLUMN_NAME_TASK_FK.getName() + " " + RemindyContract.AttachmentTable.COLUMN_NAME_TASK_FK.getDataType() +
                " REFERENCES " + RemindyContract.TaskTable.TABLE_NAME + "(" + RemindyContract.TaskTable._ID + ") " + COMMA_SEP +
                RemindyContract.AttachmentTable.COLUMN_NAME_TYPE.getName() + " " + RemindyContract.AttachmentTable.COLUMN_NAME_TYPE.getDataType() + COMMA_SEP +
                RemindyContract.AttachmentTable.COLUMN_NAME_CONTENT_TEXT.getName() + " " + RemindyContract.AttachmentTable.COLUMN_NAME_CONTENT_TEXT.getDataType() + COMMA_SEP +
                RemindyContract.AttachmentTable.COLUMN_NAME_CONTENT_BLOB.getName() + " " + RemindyContract.AttachmentTable.COLUMN_NAME_CONTENT_BLOB.getDataType() +
                " ); ";
        sqLiteDatabase.execSQL(statement);
    }

    private void deleteDatabase(SQLiteDatabase sqLiteDatabase) {
        String statement ;

        statement = "DROP TABLE IF EXISTS " + RemindyContract.AttachmentTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.TaskTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.LocationBasedReminderTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.PlaceTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.RepeatingReminderTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

        statement = "DROP TABLE IF EXISTS " + RemindyContract.OneTimeReminderTable.TABLE_NAME + "; ";
        sqLiteDatabase.execSQL(statement);

    }
}
