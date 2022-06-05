package no.zrayc.games.minesweeper;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class ColourSchemeHandler {
    private static Paint UNOPENED_BG = Color.LIGHTGREY; // Click shift while hover to show colour preview. Works sometimes apparently...
    private static Paint UNOPENED_STROKE = Color.LIGHTGREY;
    private static Paint OPENED_BG = Color.SILVER;
    private static Paint OPENED_STROKE = Color.DARKGREY;
    
    public static void setLightMode() {
        UNOPENED_BG = Color.LIGHTGREY;
        UNOPENED_STROKE = Color.WHITE;
        OPENED_BG = Color.SILVER;
        OPENED_STROKE = Color.DARKGREY;
    }
    
    public static void setDarkMode() {
        UNOPENED_BG = Color.rgb(100, 100, 100);
        UNOPENED_STROKE = Color.LIGHTGREY;
        OPENED_BG = Color.SILVER;
        OPENED_STROKE = Color.DARKGREY;
    }
    
    public static Paint getUnopenedBg() {
        return UNOPENED_BG;
    }
    
    public static Paint getUnopenedStroke() {
        return UNOPENED_STROKE;
    }
    
    public static Paint getOpenedBg() {
        return OPENED_BG;
    }
    
    public static Paint getOpenedStroke() {
        return OPENED_STROKE;
    }
    
    public static void setUnopenedBg(Paint unopenedBg) {
        UNOPENED_BG = unopenedBg;
    }
    
    public static void setOpenedStroke(Paint openedStroke) {
        OPENED_STROKE = openedStroke;
    }
    
    public static void setOpenedBg(Paint openedBg) {
        OPENED_BG = openedBg;
    }
    
    public static void setUnopenedStroke(Paint unopenedStroke) {
        UNOPENED_STROKE = unopenedStroke;
    }
}
