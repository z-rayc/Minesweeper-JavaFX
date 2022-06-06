package no.zrayc.games.minesweeper;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class OptionsMenuController implements Initializable {
    @FXML private TextField tileSizeInput;
    @FXML private TextField gridWidthInput;
    @FXML private TextField gridHeightInput;
    @FXML private TextField totalBombsInput;
    
    @FXML private Button gameOptionsApplyButton;
    @FXML private Button appearanceApplyButton;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set previous values in
        tileSizeInput.setText(String.valueOf(MinesweeperGameController.getTileSize()));
        gridWidthInput.setText(String.valueOf(MinesweeperGameController.getTileAmountX()));
        gridHeightInput.setText(String.valueOf(MinesweeperGameController.getTileAmountY()));
        totalBombsInput.setText(String.valueOf(MinesweeperGameController.getTotalBombs()));
        
        // Set inputs to numeric only
        List<TextField> numericOnlyInput = new ArrayList<>();
        numericOnlyInput.add(tileSizeInput);
        numericOnlyInput.add(gridWidthInput);
        numericOnlyInput.add(gridHeightInput);
        
        /*numericOnlyInput.forEach(textField -> textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9]*")) {
                textField.setText(oldValue);
            }
        }));*/
    
        tileSizeInput.setTextFormatter(new TextFormatter<Integer>(change -> {
            // Deletion should always be possible.
            if (change.isDeleted()) {
                
                return change;
            }
        
            // How would the text look like after the change?
            String txt = change.getControlNewText();
        
            // There shouldn't be leading zeros.
            if (txt.matches("0\\d+")) {
                return null;
            }
        
            // Try parsing and check if the result is in [0, 64].
            try {
                int n = Integer.parseInt(txt);
                return (0 <= n && n <= 64 ? change : null);
            } catch (NumberFormatException e) {
                return null;
            }
        }));
        
        // TODO: Enable game options apply button only upon change
    }
    
    @FXML
    private void setTileAndGridOptions() {
        int tileSize = Integer.parseInt(tileSizeInput.getText());
        MinesweeperGameController.setTileSize(tileSize);
        
        int tileAmountX = Integer.parseInt(gridWidthInput.getText());
        MinesweeperGameController.setTileAmountX(tileAmountX);
        
        int tileAmountY = Integer.parseInt(gridHeightInput.getText());
        MinesweeperGameController.setTileAmountY(tileAmountY);
    }
}
