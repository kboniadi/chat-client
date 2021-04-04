import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Client client = new Client(stage, "localhost");
        Thread thread = new Thread(client);
        thread.start();
    }
}
