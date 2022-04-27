package main.gui;

import main.game.Player;

public class Settings{

    public static pColor AIpColor = pColor.BLACK;
    public static boolean helpMode = true;
    public static boolean hintMode = false;
    public static boolean dragDrop = false;
    public static int AiMinPauseDurationInMs = 800;
    public static int squareSize = 80;
    public static int checkerWidth = 5*squareSize/6;
    public static int checkerHeight = 5*squareSize/6;
    public static int ghostButtonWidth = 30*squareSize/29;
    public static int ghostButtonHeight = 5*squareSize/6;

    /**
     * Gets the correct pColor (black/white) for the given player
     * @param player
     * @return
     */
    public static pColor getpColor(Player player){
        pColor result = null;
        if (player == Player.AI){
            result = Settings.AIpColor;
        }
        else if (player == Player.HUMAN){
            result = Settings.AIpColor.getOpposite();
        }
        if(result == null){
            throw new RuntimeException("Null player has no piece.");
        }
        return result;
    }
}
