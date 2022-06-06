package no.zrayc.games.minesweeper;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {
    private Stage stage;
    private Scene scene;
    
    @Override
    public void start(Stage stage) {
        try {
            this.stage = stage;
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MinesweeperGame.fxml"));
            Parent startScene = loader.load();
            MinesweeperGameController minesweeperGameController = loader.getController();
            
            scene = new Scene(startScene);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("main.css")).toExternalForm());
            minesweeperGameController.newGame();
            minesweeperGameController.init();
            
            stage.setScene(scene);
            stage.show();
            stage.setMaximized(true);
            stage.getIcons().add(new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream("images/Bomb@3x.png"))));
            stage.setTitle("Minesweeper");
            stage.setOnCloseRequest(MinesweeperGameController::exitApplication);
        } catch (Exception e) {
            MinesweeperGameController.getErrorAlert(e, "The program failed to open. ");
            Platform.exit();
        }
    }
    
    @Override
    public void stop() {
        System.exit(0);
    }
    
    public Stage getStage() {
        return this.stage;
    }
    
    public Scene getScene() {
        return this.scene;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
