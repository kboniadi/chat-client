package data;

public class AcceptedData extends AbstractDataType {
    public final String type = "Accepted";
    public final String name;

    private AcceptedData(String name) {
        this.name = name;
    }

    public static IDataType of(String name) {
        return new AcceptedData(name);
    }
}
