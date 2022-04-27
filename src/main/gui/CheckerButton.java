package main.gui;

import main.game.Game;
import main.game.Piece;
import main.game.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Black or white checker piece (clickable button component)
 */
public class CheckerButton extends JButton{

    private int positionX;
    private int positionY;
    private Piece piece;
    private Game game;
    // drag drop
    int X;
    int Y;
    int screenX = 0;
    int screenY = 0;

    public CheckerButton(int positionX,int positionY, Piece piece, GUI gui){
        super();
        this.positionX = positionX;
        this.positionY = positionY;
        this.piece = piece;
        this.game = game;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        setIcon(piece);
    }


    public int getPositionX() {
        return positionX;
    }
    public int getPositionY() {
        return positionY;
    }

    public Piece getPiece() {
        return piece;
    }

    private void setIcon(Piece piece){
        BufferedImage buttonIcon = null;
        pColor pcolor = Settings.getpColor(piece.getPlayer());
        try {
            if (pcolor == pColor.BLACK) {
                if (piece.isKing()) {
                    buttonIcon = ImageIO.read(new File("images/blackking.png"));
                } else {
                    buttonIcon = ImageIO.read(new File("images/blackchecker.gif"));
                }
            }
            else {
                if (piece.isKing()) {
                    buttonIcon = ImageIO.read(new File("images/whiteking.png"));
                }
                else {
                    buttonIcon = ImageIO.read(new File("images/whitechecker.gif"));
                }
            }
        }
        catch(IOException e){
            System.out.println(e.toString());
        }

        if (buttonIcon != null){
            Image resized = buttonIcon.getScaledInstance(Settings.checkerWidth,Settings.checkerHeight,100);
            ImageIcon icon = new ImageIcon(resized);
            this.setIcon(icon);
        }
    }

}
