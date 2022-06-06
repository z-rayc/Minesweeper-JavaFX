package no.zrayc.games.minesweeper;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Responsible for the Graphic User Interface.
 * Creates a grid with tiles.
 */
public class MinesweeperGameController implements Initializable {
    private Random random = new Random();
    
    private static int TILE_SIZE = 35;
    private static int TILE_AMOUNT_X = 30;
    private static int TILE_AMOUNT_Y = 16;
    private static int HEIGHT;
    private static int WIDTH;
    private static int TOTAL_BOMBS = 99;
    
    private int remainingBombs;
    private int unopenedClearTiles;
    
    private Tile[][] grid;
    private GridPane gridPane;
    
    private boolean firstClick = true;
    private boolean gameOver = false;
    
    @FXML private BorderPane root;
    @FXML private Text bombAmountDisplay;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.ESCAPE == event.getCode()) {
                openOptionsMenu(event);
            }
        });
    
        root.addEventHandler(KeyEvent.KEY_RELEASED, (KeyEvent event) -> {
            if (KeyCode.N == event.getCode()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm");
                alert.setHeaderText("New game?");
                alert.setContentText("Are you sure you want to start a new game?");
    
                Optional<ButtonType> result = alert.showAndWait();
    
                if (result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        newGame();
                    } else {
                        event.consume();
                    }
                }
            }
        });
    }
    
    public static void setTileSize(int tileSize) {
        TILE_SIZE = tileSize;
    }
    public static void setTileAmountX(int tileAmountX) {
        TILE_AMOUNT_X = tileAmountX;
    }
    public static void setTileAmountY(int tileAmountY) {
        TILE_AMOUNT_Y = tileAmountY;
    }
    public static void setTotalBombs(int totalBombs) {
        TOTAL_BOMBS = totalBombs;
    }
    public static int getTileSize() {
        return TILE_SIZE;
    }
    public static int getTileAmountX() {
        return TILE_AMOUNT_X;
    }
    public static int getTileAmountY() {
        return TILE_AMOUNT_Y;
    }
    public static int getTotalBombs() {
        return TOTAL_BOMBS;
    }
    
    public void init() {
        updateBombAmountDisplay();
        
        // TODO: This solution is kinda very jank.
        //  Because it has to check every single tile
        root.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.SPACE) {
                for (int y = 0; y < TILE_AMOUNT_Y; y++) {
                    for (int x = 0; x < TILE_AMOUNT_X; x++) {
                        Tile tile = grid[x][y];
                        if (tile.hovered && !tile.isOpen) {
                            tile.flag();
                        } else if (tile.hovered) {
                            openNeighbours(tile);
                        }
                    }
                }
            }
        });
    }
    
    private void updateBombAmountDisplay() {
        bombAmountDisplay.setText(String.valueOf(this.remainingBombs));
    }
    
    /**
     * Represents a tile. A tile can have a bomb,
     * and keeps track of nearby bombs.
     */
    private class Tile extends StackPane {
        private int xPos; // X-coordinate in the grid
        private int yPos; // Y-coordinate in the grid
        
        private boolean hasBomb;
        private int nearbyBombs;
        private boolean isOpen = false;
        private boolean isFlagged = false;
        private boolean hovered = false;
        
        private static Paint UNOPENED_BG = ColourSchemeHandler.getUnopenedBg(); // Click shift while hover to show colour preview. Works sometimes apparently...
        private static Paint UNOPENED_STROKE = ColourSchemeHandler.getUnopenedStroke();
        private static Paint OPENED_BG = ColourSchemeHandler.getOpenedBg();
        private static Paint OPENED_STROKE = ColourSchemeHandler.getOpenedStroke();
        
        private final Rectangle rectangle = new Rectangle(
                TILE_SIZE - 2.0, TILE_SIZE - 2.0);
        private final Text text = new Text();
        private ImageView imageView = new ImageView();
        
        public Tile(int xPos, int yPos) {
            // TODO: To prevent centerPane clipping, the height/width must not have a fixed size
            // Maybe try to let top be on top?
            this.xPos = xPos;
            this.yPos = yPos;
            
            imageView.preserveRatioProperty();
            imageView.setFitHeight(TILE_SIZE - (TILE_SIZE * 0.20));
            imageView.setFitWidth(TILE_SIZE - (TILE_SIZE * 0.20));
            
            rectangle.setFill(UNOPENED_BG);
            rectangle.setStroke(UNOPENED_STROKE);
            
            Font font = Font.font("Verdana", FontWeight.BOLD, 24);
            text.setFont(font);
            this.getChildren().addAll(rectangle, text, imageView);
            
            // Open or flag a tile
            this.setOnMouseClicked(event -> {
               if (event.getButton() == MouseButton.PRIMARY) {
                   this.open();
               } else if (event.getButton() == MouseButton.SECONDARY) {
                   this.flag();
                   if (this.isOpen) {
                       openNeighbours(this);
                   }
               }
            });
            
            this.hoverProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    rectangle.setFill(OPENED_BG);
                    hovered = true;
                } else if (oldValue) {
                    if (!isOpen) {
                        rectangle.setFill(UNOPENED_BG);
                    }
                    hovered = false;
                }
            });
            
            // TODO: Update GUI and make it lighter, create a dark-mode version
                // Change colour scheme so it's not a blatant rip-off of minesweeperonline.com
            // TODO: Create a timer that maxes at 999 seconds
                // Create a high-score system (let the chooser decide if it is saved
                // Create persistence through file
                // Add user name, time used, and date time of when high-score happened
            // TODO: Create a menu
                // Let the user decide tile size (zoom in)
                // Let the user decide height and width (number of tiles)
                // Let the user decide amount of bombs
            
                // Let the user decide between light-mode and dark-mode
                // Add a user guide
                // Access high-score table
                // Customizable colour scheme? Customizable graphics (from selection)?
            // TODO: CHECKSTYLE CODE
        }
    
        private void updateNearbyBombs() {
            // Check amount of neighbouring bombs for tile
            long bombs = getNeighbours(this).stream().filter(t -> t.hasBomb).count();
            this.nearbyBombs = (int) bombs;
        }
        
        /**
         * Update the text with amount of nearby bombs.
         * Set the text colour based on amount of nearby bombs.
         * If the tile has a bomb, set a bomb icon in the tile instead.
         */
        private void updateText() {
            if (hasBomb || this.nearbyBombs > 0) {
                this.text.setText(hasBomb ? "" : String.valueOf(this.nearbyBombs));
            }
            
            Color color;
            if (hasBomb) {
                color = Color.WHITE;
                this.setImage("images/Bomb@3x.png");
            } else {
                color = switch (nearbyBombs) {
                    case 1 -> Color.BLUE;
                    case 2 -> Color.FORESTGREEN;
                    case 3 -> Color.RED;
                    case 4 -> Color.MIDNIGHTBLUE;
                    case 5 -> Color.DARKRED;
                    case 6 -> Color.TEAL;
                    case 7 -> Color.BLACK;
                    case 8 -> Color.DIMGREY;
                    default -> Color.SILVER;
                };
            }
            
            text.setFill(color);
        }
    
        /**
         *
         */
        private void open() {
            if (firstClick) {
                placeBombs(this);
                getAllTiles().forEach(Tile::updateNearbyBombs);
                updateBombAmountDisplay();
                firstClick = false;
            }
            
            if (!isOpen && !isFlagged && !gameOver) {
                revealTile();
    
                System.out.println("Remaining bombs: " + remainingBombs);
                System.out.println("Unopened tiles: " + unopenedClearTiles + "\n");
                
                if (hasBomb) {
                    rectangle.setFill(Color.FIREBRICK);
                    gameOver = true;
                    checkAllTiles();
                    // Mark this bomb as the cause of defeat
                    // Set a global isDefeated boolean to true
                    // When isDefeated = true, cannot open a new tile
                } else {
                    unopenedClearTiles--;
                    if (unopenedClearTiles == 0) {
                        flagAllBombs();
                        displayWinAlert();
                        gameOver = true;
                    }
                }
                
                if (nearbyBombs == 0 && !hasBomb) {
                    getNeighbours(this).forEach(Tile::open);
                }
            } else if (!isOpen && gameOver) {
                if (hasBomb && !isFlagged) {
                    this.revealTile();
                } else if (!hasBomb && isFlagged) {
                    this.setImage("images/NoBomb@3x.png");
                }
            }
        }
        
        private void setImage(String imagePath) {
            Image image = new Image(Objects.requireNonNull(
                    getClass().getResourceAsStream(imagePath)));
            imageView.setImage(image);
        }
        
        private void removeImage() {
            setImage("images/Transparent.png");
        }
        
        private void revealTile() {
            this.isOpen = true;
            text.setVisible(true);
            rectangle.setFill(OPENED_BG);
            rectangle.setStroke(OPENED_STROKE);
            this.updateText();
        }
        
        private void checkAllTiles() {
            for (int y = 0; y < TILE_AMOUNT_Y; y++) {
                for (int x = 0; x < TILE_AMOUNT_X; x++) {
                    Tile tile = grid[x][y];
                    tile.open();
                }
            }
            // Open all remaining bombs
            // If flagged but without bomb, replace image with crossed out bomb
        }
        
        private void flag() {
            if (!isOpen && !gameOver) {
                if (isFlagged) {
                    this.isFlagged = false;
                    this.removeImage();
                    remainingBombs++;
                    updateBombAmountDisplay();
                } else {
                    this.isFlagged = true;
                    this.setImage("images/Flag@3x.png");
                    remainingBombs--;
                    updateBombAmountDisplay();
                }
            }
        }
        
        private void flagAllBombs() {
            for (int y = 0; y < TILE_AMOUNT_Y; y++) {
                for (int x = 0; x < TILE_AMOUNT_X; x++) {
                    Tile tile = grid[x][y];
                    
                    if (tile.hasBomb && !tile.isFlagged) {
                        tile.flag();
                    }
                }
            }
        }
    }
    
    /**
     * Creates the grid of the game and adds tiles.
     */
    private void createContent() {
        // TODO: Feels a bit like memory leaks here. Check for that.
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setFocusTraversable(true);
        root.setCenter(vBox);
        
        grid = new Tile[TILE_AMOUNT_X][TILE_AMOUNT_Y];
        
        gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);
        gridPane.setAlignment(Pos.CENTER);
        vBox.getChildren().add(gridPane);
        
        // Create and add tiles to the grid
        for (int y = 0; y < TILE_AMOUNT_Y; y++) {
            for (int x = 0; x < TILE_AMOUNT_X; x++) {
                Tile tile = new Tile(x, y);
                
                grid[x][y] = tile;
                gridPane.add(tile, x, y);
            }
        }
        updateBombAmountDisplay();
    }
    
    /**
     * Get all neighbours of a tile.
     *
     * @param tile The tile whose neighbours are to be found.
     * @return The neighbours of the tile.
     */
    private List<Tile> getNeighbours(Tile tile) {
        List<Tile> neighbours = new ArrayList<>();
        
        int[] points = new int[]{
                //  x   x   x
                //  x   *   x
                //  x   x   x
                
                -1, -1, // top left
                -1, 0,  // top center
                -1, 1,  // top right
                0, -1,  // middle left
                0, 1,   // middle right
                1, -1,  // bottom left
                1, 0,   // bottom center
                1, 1    // bottom right
        };
        
        for (int i = 0; i < points.length; i = i+2) {
            // Get all the points from the array
            int dx = points[i];
            int dy = points[i+1];
            
            // Set the new X og Y positions
            int newX = tile.xPos + dx;
            int newY = tile.yPos + dy;
            
            // Check if the new positions are valid
            if (newX >= 0 && newX < TILE_AMOUNT_X
                    && newY >= 0 && newY < TILE_AMOUNT_Y) {
                // Add the tile in that position
                neighbours.add(grid[newX][newY]);
            }
        }
        
        return neighbours;
    }
    
    private void placeBombs(Tile startingTile) {
        List<Tile> allTiles = getAllTiles();
    
        List<Tile> possibleBombTiles = allTiles;
        possibleBombTiles.removeAll(getNeighbours(startingTile));
        possibleBombTiles.remove(startingTile);
    
        while (remainingBombs < TOTAL_BOMBS) {
            int randomInt = random.nextInt(possibleBombTiles.size());
            Tile tile = possibleBombTiles.get(randomInt);
        
            if (!tile.hasBomb) {
                tile.hasBomb = true;
                remainingBombs++;
            }
        }
    }
    
    private List<Tile> getAllTiles() {
        List<Tile> allTiles = new ArrayList<>();
        for (int y = 0; y < TILE_AMOUNT_Y; y++) {
            for (int x = 0; x < TILE_AMOUNT_X; x++) {
                Tile tile = grid[x][y];
                allTiles.add(tile);
            }
        }
        
        return allTiles;
    }
    
    // TODO: See if performance issues can be fixed
    private void openNeighbours(Tile tile) {
        List<Tile> neighbours = getNeighbours(tile);
        if (neighbours.stream().filter(t -> t.isFlagged).count() == tile.nearbyBombs) {
            neighbours.forEach(Tile::open);
        }
    }
    
    @FXML
    public void newGame() {
        Pane center = (Pane) root.getCenter();
        root.getChildren().remove(center);
        
        gameOver = false;
        firstClick = true;
        
        remainingBombs = 0;
        unopenedClearTiles = (TILE_AMOUNT_X * TILE_AMOUNT_Y) - TOTAL_BOMBS;
        
        createContent();
    }
    
    private void displayWinAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Win");
        alert.setHeaderText("Win!");
        alert.setContentText("Congratulations, you have won the game. ");
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }
    
    /**
     * Exit the application. Display a confirmation alert.
     *
     * @param event The event that occurred.
     */
    public static void exitApplication(Event event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Close application?");
        alert.setContentText("Are you sure you want to close the application?");
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent()) {
            if (result.get() == ButtonType.OK) {
                Platform.exit();
            } else {
                event.consume();
            }
        }
    }
    
    /**
     * Show an error alert.
     *
     * @param e The exception to show an alert for.
     * @param errorMessage A message about the error.
     */
    public static void getErrorAlert(Exception e, String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("An error has occurred. ");
        alert.setContentText(errorMessage + "\nDetails: " + e.getClass().getSimpleName() + ". " + e.getLocalizedMessage());
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
        
        e.printStackTrace();
    }
    
    @FXML
    private void openOptionsMenu(Event event) {
        try {
            Stage stage = new Stage();
    
            FXMLLoader loader = new FXMLLoader(getClass().getResource("OptionsMenu.fxml"));
            Parent optionsMenu = loader.load();
            
            stage.setScene(new Scene(optionsMenu, 400, 300));
            stage.setTitle("Options");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Node) event.getSource()).getScene().getWindow());
            stage.show();
    
            stage.setMinWidth(400);
            stage.setMinHeight(300);
            stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("images/Gear@3x.png"))));
        } catch (IOException e) {
            getErrorAlert(e, "Unable to open the options.");
        }
    }
}
