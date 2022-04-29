package main.gui;

import main.game.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
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

    public CheckerButton(int positionX,int positionY, Piece piece, GUI gui){
        super();
        this.positionX = positionX;
        this.positionY = positionY;
        this.piece = piece;
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
                    buttonIcon = ImageIO.read(new File("images/blackchecker.png"));
                }
            }
            else {
                if (piece.isKing()) {
                    buttonIcon = ImageIO.read(new File("images/whiteking.png"));
                }
                else {
                    buttonIcon = ImageIO.read(new File("images/whitechecker.png"));
                }
            }
        }
        catch(IOException e){
            System.out.println(e);
        }

        if (buttonIcon != null){
            Image resized = buttonIcon.getScaledInstance(Settings.checkerWidth,Settings.checkerHeight,100);
            ImageIcon icon = new ImageIcon(resized);
            this.setIcon(icon);
        }
    }

}
