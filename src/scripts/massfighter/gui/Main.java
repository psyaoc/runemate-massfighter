package scripts.massfighter.gui;

import com.runemate.game.api.hybrid.Environment;
import com.runemate.game.api.hybrid.util.Resources;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scripts.massfighter.MassFighter;

import java.io.InputStream;

public class Main extends Stage {

    private static Controller controller;
    public static Stage stage;

    public Main() {
        try {
            start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void start(Stage stage) throws Exception {
        InputStream input = Resources.getAsStream("scripts/massfighter/gui/FighterGUI-V2.fxml");
        if (input != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setController(new Controller());
            Parent root = loader.load(input);
            Scene scene = new Scene(root);
            stage.setTitle("MassFighter");
            stage.setScene(scene);
            Main.stage = stage;
            Main.controller = loader.getController();
            controller.initialize();
            stage.setOnCloseRequest(event -> {
                System.out.println("UI Closed - Stopping Script");
                if (Environment.getScript() != null && Environment.getScript().isRunning()) {
                    Environment.getScript().stop();
                }
                stage.close();
            });
            stage.show();
        } else {
            MassFighter.status = "GUI Fail";
        }
    }

}
