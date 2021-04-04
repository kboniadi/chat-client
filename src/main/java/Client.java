import com.google.gson.JsonObject;
import data.MessageData;
import data.SubmissionData;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.BufferWrapper;
import utils.GsonWrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client implements Runnable {
    private static final String serverAddress = "45.57.226.7";
    private BufferWrapper buffer;
    private final Stage main;
    private final TextField field;
    private final TextArea area;
    private boolean running;

    public Client(Stage stage) {
        main = stage;
        field = new TextField();
        area = new TextArea();
        running = true;

        field.setEditable(false);
        area.setEditable(false);

        // Send on enter then clear to prepare for next message
        field.setOnAction(event -> {
            buffer.writeLine(GsonWrapper.toJson(MessageData.of(field.getText())));
            if ((field.getText()).equals("\\quit")) {
                running = false;
                Platform.exit();
            }
            field.setText("");
        });

        VBox vbox = new VBox();
        vbox.getChildren().addAll(area, field);
        main.setScene(new Scene(vbox));
        main.setOnCloseRequest(event -> {
            buffer.writeLine(GsonWrapper.toJson(MessageData.of("\\quit")));
            running = false;
            Platform.exit();
        });
        main.show();
    }

    private String getName() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(main);
        dialog.setHeaderText("Screen name selection");
        dialog.setContentText("Choose a screen name");
//        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
//        TextField inputField = dialog.getEditor();
//        BooleanBinding isInValid = Bindings.createBooleanBinding(() -> !isValid(inputField.getText()), inputField.textProperty());
//        okButton.disableProperty().bind(isInValid);
        var result = dialog.showAndWait();
        if (result.isEmpty()) {
            running = false;
            Platform.exit();
            return "\\quit";
        }
        return result.get();
    }

//    private Boolean isValid(String text) throws IOException {
//        buffer.writeLine(GsonWrapper.toJson(SubmissionData.of(text, "Message")));
//        JsonObject json = GsonWrapper.fromJson(buffer.readLine());
//        return json != null && json.get("type").getAsString().equals("Accepted");
//    }

    /**
     * When an object implementing interface {@code Runnable} is used
     * to create a thread, starting the thread causes the object's
     * {@code run} method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method {@code run} is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        boolean isAccepted = false;
        try (var socket = new Socket(serverAddress, 9000)) {
            buffer = new BufferWrapper.Builder()
                    .withWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)))
                    .withReader(new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)))
                    .build();

            while (running) {
                if (!isAccepted) {
                    Platform.runLater(() -> {
                        buffer.writeLine(GsonWrapper.toJson(SubmissionData.of(getName(), "Message")));
                    });
                    JsonObject json = GsonWrapper.fromJson(buffer.readLine());

                    if (json != null && json.get("type").getAsString().equals("Accepted")) {
                        isAccepted = true;
                        Platform.runLater(() -> {
                            this.main.setTitle("Chat Room - " + json.get("name").getAsString());
                            field.setEditable(true);
                        });
                    }
                } else {
                    JsonObject json = GsonWrapper.fromJson(buffer.readLine());
                    if (json != null && json.get("type").getAsString().equals("Message")) {
                        Platform.runLater(() -> {
                            area.appendText(json.get("message").getAsString() +  "\n");
                        });
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Server down");
            e.printStackTrace();
            Platform.exit();
        }
    }
}
