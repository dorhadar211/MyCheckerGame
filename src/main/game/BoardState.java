package main.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class BoardState {

    // side length of the board
    public static final int SIDE_LENGTH = 8;
    public static final int NO_SQUARES = SIDE_LENGTH*SIDE_LENGTH; // 8 x 8
    // state of the board
    Piece[][] state;
    // origin and destination position of the most recent move
    private int fromPosX = -1;
    private int fromPosY = -1;
    private int toPosX = -1;
    private int toPosY = -1;
    // origin position of double jump move, used to invalidate other moves during multi-move
    private int doublejumpPosX = -1;
    private int doublejumpPosY = -1;
    // player's turn
    private Player turn;
    // track number of human/AI pieces on board
    public HashMap<Player, Integer> pieceCount;
    private HashMap<Player, Integer> kingCount;

    public BoardState(){
        state = new Piece[BoardState.SIDE_LENGTH][BoardState.SIDE_LENGTH];
    }

    /**
     * Set up initial board state.
     */
    public static BoardState InitialState(){
        BoardState bs = new BoardState();
        bs.turn = Settings.FIRSTMOVE;
        for (int i = 0; i < BoardState.SIDE_LENGTH; i++){
            for (int j =0; j<BoardState.SIDE_LENGTH;j++) {
                // place on black squares only
                if ((j+i) % 2 == 1) {
                    // AI pieces in first 3 rows
                    if (i < 3) {
                        bs.state[i][j] = new Piece(Player.AI, false);
                    }
                    // Human pieces in last 3 rows
                    else if (i > 4) {
                        bs.state[i][j] = new Piece(Player.HUMAN, false);
                    }
                }
            }
        }
        // count initial pieces (generalizable, not hard-coded)
//        int aiCount = (int) Arrays.stream(bs.state).filter(x -> x != null).filter(x -> x.getPlayer() == Player.AI).count();
//        int humanCount = (int) Arrays.stream(bs.state).filter(x -> x != null).filter(x -> x.getPlayer() == Player.HUMAN).count();
        int aiCount = 12;
        int humanCount = 12;
        bs.pieceCount = new HashMap<>();
        bs.pieceCount.put(Player.AI, aiCount);
        bs.pieceCount.put(Player.HUMAN,humanCount);
        bs.kingCount = new HashMap<>();
        bs.kingCount.put(Player.AI, 0);
        bs.kingCount.put(Player.HUMAN, 0);
        return bs;
    }

    private BoardState deepCopy(){
        BoardState newBs = new BoardState();
        for(int i=0;i<BoardState.SIDE_LENGTH;i++) {
            System.arraycopy(this.state[i], 0, newBs.state[i], 0, BoardState.SIDE_LENGTH);
        }
        return newBs;
    }

    /**
     * Compute heuristic indicating how desirable this state is to a given player.
     * @param player
     * @return
     */
    public int computeHeuristic(Player player){
        return heuristic1(player);
    }

    private int heuristic1(Player player){
        // 'infinite' value for winning
        if (this.pieceCount.get(player.getOpposite()) == 0){
            return Integer.MAX_VALUE;
        }
        // 'negative infinite' for losing
        if (this.pieceCount.get(player) == 0){
            return Integer.MIN_VALUE;
        }
        // difference between piece counts with kings counted twice
        return pieceScore(player) - pieceScore(player.getOpposite());
    }

    private int pieceScore(Player player){
        return this.pieceCount.get(player) + this.kingCount.get(player);
    }



    /**
     * Gets valid successor states for a player
     * @return
     */
    public ArrayList<BoardState> getSuccessors(){
        // compute jump successors
        ArrayList<BoardState> successors = getSuccessors(true);
            if (successors.size() > 0){
                // return only jump successors if available (forced)
                return  successors;
            }
            else{
                // return non-jump successors (since no jumps available)
                return getSuccessors(false);
            }
    }

    /**
     * Get valid jump or non-jump successor states for a player
     * @param jump
     * @return
     */
    public ArrayList<BoardState> getSuccessors(boolean jump){
        ArrayList<BoardState> result = new ArrayList<>();
        for (int i = 0; i < BoardState.SIDE_LENGTH; i++){
            for(int j =0; j < BoardState.SIDE_LENGTH; j++) {
                if (state[i][j] != null) {
                    if (state[i][j].getPlayer() == turn) {
                        result.addAll(getSuccessors(i,j, jump));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets valid successor states for a specific position on the board
     * @param position
     * @return
     */
    public ArrayList<BoardState> getSuccessors(int positionRow,int positionColumn){
            // compute jump successors GLOBALLY
            ArrayList<BoardState> jumps = getSuccessors(true);
            if (jumps.size() > 0){
                // return only jump successors if available (forced)
                return getSuccessors(positionRow,positionColumn, true);
            }
            else{
//                 return non-jump successors (since no jumps available)
                return getSuccessors(positionRow,positionColumn, false);
            }
    }

    /**
     * Get valid jump or non-jump successor states for a specific piece on the board.
     * @param position
     * @return
     */
    public ArrayList<BoardState> getSuccessors(int positionRow,int positionColumn, boolean jump){
        if (this.getPiece(positionRow,positionColumn).getPlayer() != turn){
            throw new IllegalArgumentException("No such piece at that position");
        }
        Piece piece = this.state[positionRow][positionColumn];
        if(jump){
            return jumpSuccessors(piece, positionRow,positionColumn);
        }
        else{
            return nonJumpSuccessors(piece, positionRow,positionColumn);
        }
    }

    /**
     * Gets valid non-jump moves at a given position for a given piece
     * @param piece
     * @param position
     * @return
     */
    private ArrayList<BoardState> nonJumpSuccessors(Piece piece, int positionRow,int positionColumn){
        ArrayList<BoardState> result = new ArrayList<>();
        // loop through allowed movement directions
        for (int dx : piece.getXMovements()){
            for (int dy : piece.getYMovements()){
                int newX = positionColumn + dx;
                int newY = positionRow + dy;
                // new position valid?
                if (isValid(newY, newX)) {
                    // new position available?
                    if (getPiece(newY, newX) == null) {
//                        int newpos = SIDE_LENGTH*newY + newX;
                        result.add(createNewState(positionRow, positionColumn, newY, newX, piece, false, dy,dx));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets valid jump moves at a given position for a given piece
     * @param piece
     * @param position
     * @return
     */
    private ArrayList<BoardState> jumpSuccessors(Piece piece, int positionY , int positionX){
        ArrayList<BoardState> result = new ArrayList<>();
        // no other jump moves are valid while doing double jump
        if ((doublejumpPosX > 0 ||doublejumpPosY >0) && (positionX != doublejumpPosX && positionY != doublejumpPosY)){
            return result;
        }
//        int x = position % SIDE_LENGTH;
//        int y = position / SIDE_LENGTH;
        // loop through allowed movement directions
        for (int dx : piece.getXMovements()){
            for (int dy : piece.getYMovements()){
                int newX = positionX + dx;
                int newY = positionY + dy;
                // new position valid?
                if (isValid(newY, newX)) {
                    // new position contain opposite player?
                    if (getPiece(newY,newX) != null && getPiece(newY, newX).getPlayer() == piece.getPlayer().getOpposite()){
                        newX = newX + dx; newY = newY + dy;
                        // jump position valid?
                        if (isValid(newY, newX)){
                            // jump position available?
                            if (getPiece(newY,newX) == null) {
//                                int newpos = SIDE_LENGTH*newY + newX;
                                result.add(createNewState(positionY,positionX, newY,newX, piece, true, dy, dx));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    private BoardState createNewState(int oldRowPos,int oldColPos, int newRowPos,int newColPos, Piece piece, boolean jumped, int dy, int dx){
        BoardState result = this.deepCopy();
        result.pieceCount = new HashMap<>(pieceCount);
        result.kingCount = new HashMap<>(kingCount);
        // check if king position
        boolean kingConversion = false;
        if (isKingPosition(newRowPos, piece.getPlayer())){
            piece = new Piece(piece.getPlayer(), true);
            kingConversion = true;
            // increase king count
            result.kingCount.replace(piece.getPlayer(), result.kingCount.get(piece.getPlayer()) + 1);
        }
        // move piece
        result.state[oldRowPos][oldColPos] = null;
        result.state[newRowPos][newColPos] = piece;
        // store meta data
        result.fromPosX = oldColPos;
        result.fromPosY = oldRowPos;
        result.toPosX = newColPos;
        result.toPosY = newRowPos;
        Player oppPlayer = piece.getPlayer().getOpposite();
        result.turn = oppPlayer;
        if (jumped){
            // remove captured piece
            result.state[newRowPos-dy][newColPos-dx] = null;
            result.pieceCount.replace(oppPlayer, result.pieceCount.get(oppPlayer) - 1);
            // is another jump available? (not allowed if just converted into king)
            if (result.jumpSuccessors(piece, newRowPos,newColPos).size() > 0 && kingConversion == false){
                // don't swap turns
                result.turn = piece.getPlayer();
                // remember double jump position
                result.doublejumpPosY = newRowPos;
                result.doublejumpPosX = newColPos;
            }
        }
        return result;
    }

    private boolean isKingPosition(int posRow, Player player){
        if (posRow == 0 && player == Player.HUMAN){
            return true;
        }
        else return posRow == SIDE_LENGTH - 1 && player == Player.AI;
    }

    /**
     * Gets the destination position of the most recent move.
     * @return
     */
    public int getToPosX(){
        return this.toPosX;
    }

    public int getToPosY(){
        return this.toPosY;
    }

    /**
     * Gets the destination position of the most recent move.
     * @return
     */
    public int getFromPosX(){
        return this.fromPosX;
    }

    public int getFromPosY(){
        return this.fromPosY;
    }


    /**
     * Gets the player whose turn it is
     * @return
     */
    public Player getTurn() {
        return turn;
    }

    /**
     * Is the board in a game over state?
     * @return
     */
    public boolean isGameOver(){
        return (pieceCount.get(Player.AI) == 0 || pieceCount.get(Player.HUMAN) == 0);
    }

    /**
     * Get player piece at given position.
     * @param i Position in board.
     * @return
     */
    public Piece getPiece(int i,int j){
        return state[i][j];
    }

    /**
     * Check if grid indices are valid
     * @param y
     * @param x
     * @return
     */
    private boolean isValid(int y, int x){
        return (0 <= y) && (y < SIDE_LENGTH) && (0 <= x) && (x < SIDE_LENGTH);
    }

}
