package main.gui;

public enum pColor {
    WHITE,
    BLACK;

    public pColor getOpposite(){
        pColor result = null;
        if (this == WHITE){
            result = BLACK;
        }
        else if (this == BLACK){
            result = WHITE;
        }
        if(result == null){
            throw new RuntimeException("Null piece has no opposite");
        }
        return result;
    }
}

