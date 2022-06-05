/**
 * Opens the ui-package for JavaFX.
 */
module Minesweeper {
    requires javafx.controls;
    requires javafx.fxml;
    
    opens no.zrayc.games.minesweeper to javafx.fxml, javafx.graphics;
}