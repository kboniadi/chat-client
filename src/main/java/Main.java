import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Client client = new Client(stage);
        Thread thread = new Thread(client);
        thread.start();
    }
}
