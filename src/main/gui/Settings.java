package main.gui;

import main.game.Player;

public final class Settings{

    public static pColor AIpColor = pColor.BLACK;
    public static int AiMinPauseDurationInMs = 800;
    public static int squareSize = 80;
    public static int checkerWidth = 5*squareSize/6;
    public static int checkerHeight = 5*squareSize/6;
    public static int HelpButtonWidth = 30*squareSize/29;
    public static int HelpButtonHeight = 5*squareSize/6;

    private Settings(){}
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
