package data;

public class SubmissionData extends AbstractDataType {
    public final String type = "Submission";
    public final String name;
    public final String[] messages;

    private SubmissionData(String name, String[] messages) {
        this.name = name;
        this.messages = messages;
    }

    public static IDataType of(String name, String... interests) {
        return new SubmissionData(name, interests);
    }
}
