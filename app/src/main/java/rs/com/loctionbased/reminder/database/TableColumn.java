package rs.com.loctionbased.reminder.database;

public class TableColumn {

    private DataType datatype;
    private String name;

    public TableColumn(DataType datatype, String name) {
        this.datatype = datatype;
        this.name = name;
    }

    public DataType getDataType() {
        return datatype;
    }

    public String getName() {
        return name;
    }

}
