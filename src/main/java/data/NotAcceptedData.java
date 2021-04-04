package data;

public class NotAcceptedData extends AbstractDataType {
    public final String type = "Not Accepted";

    private NotAcceptedData() {}

    public static IDataType flag() {
        return new NotAcceptedData();
    }
}
