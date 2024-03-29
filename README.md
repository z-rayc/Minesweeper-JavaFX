# Minesweeper
A minesweeper game made with JavaFX.

## How to play
- Left-click on an unopened tile to open it and reveal if it has a mine or not.
    - If it has no mine, then it will show how many neighbouring tiles have mines.
    - Neighbouring tiles are in the horizontal, vertical and diagonal directions.
    - If a tile has a mine, the ___game is over___.
- Right-click on an unopened tile to flag or un-flag it.
    - A flag indicates that the tile has a mine.
    - A flagged tile cannot be opened.
- Right-click on an opened tile to open its neighbouring tiles *if* the number of flagged nearby tiles equals the amount of mines nearby.
- The number in the upper-right corner beside the flag icon shows the remaining mines to be flagged.
    - It is the number of total mines minus the number of flagged tiles.
- When every tile without a mine is opened, the game is won.
    - To start a new game, click the "new game" button.

### Using space bar:
- Hover over an unopened tile and press the space bar to flag or unflag it.
- Hover over an opened tile and press the space bar to open its neighbouring tiles *if* the number of flagged nearby tiles equals the amount of mines nearby.

### Hotkeys:
- Escape: Open the options menu
- N: Start a new game
  - Show alert to confirm
  
![Gameplay screenshot](https://github.com/z-rayc/Minesweeper-JavaFX/blob/main/Gameplay%20screenshot.png)
