package main.game;

import java.util.ArrayList;
import java.util.Stack;

public class Game{

    private Stack<BoardState> state;
    private int memory;
    private AI ai;
    private boolean humanWon;

    public Game(){
        memory = Settings.UNDO_MEMORY;
        state = new Stack<>();
        state.push(BoardState.InitialState());
        ai = new AI();
    }

    public void playerMove(BoardState newState){
        if (!isGameOver() && state.peek().getTurn() == Player.HUMAN){
            updateState(newState);
        }
    }

    public MoveFeedback playerMove(int fromPosX,int fromPosY, int dx, int dy){
        int toPosX = fromPosX+dx;
        int toPosY = fromPosY+dy;
        if (toPosX > getState().SIDE_LENGTH || toPosY > getState().SIDE_LENGTH ||toPosX < 0 || toPosY < 0){
            return MoveFeedback.NOT_ON_BOARD;
        }
        // check for forced jumped
        ArrayList<BoardState> jumpSuccessors = this.state.peek().getSuccessors(true);
        boolean jumps = jumpSuccessors.size() > 0;
        if (jumps){
            for (BoardState succ : jumpSuccessors){
                if ((succ.getFromPosX() == fromPosX && succ.getFromPosY() == fromPosY) && (succ.getToPosX() == toPosX && succ.getToPosY() == toPosY)){
                    updateState(succ);
                    return MoveFeedback.SUCCESS;
                }
            }
            return MoveFeedback.FORCED_JUMP;
        }
        // check diagonal
        if (Math.abs(dx) != Math.abs(dy)){
            return MoveFeedback.NOT_DIAGONAL;
        }
        // check for move onto piece
        if (this.getState().state[toPosY][toPosX] != null){
            return MoveFeedback.NO_FREE_SPACE;
        }
        // check for non-jump moves
        ArrayList<BoardState> nonJumpSuccessors = this.state.peek().getSuccessors(fromPosY,fromPosX, false);
        for (BoardState succ : nonJumpSuccessors){
            if ((succ.getFromPosX() == fromPosX && succ.getFromPosY() == fromPosY) && (succ.getToPosX() == toPosX && succ.getToPosY() == toPosY)){
                updateState(succ);
                return MoveFeedback.SUCCESS;
            }
        }    if (dy > 1){
            return MoveFeedback.NO_BACKWARD_MOVES_FOR_SINGLES;
        }
        if (Math.abs(dx)== 2){
            return MoveFeedback.ONLY_SINGLE_DIAGONALS;
        }
        return MoveFeedback.UNKNOWN_INVALID;
    }

    public MoveFeedback moveFeedbackClick(){
        ArrayList<BoardState> jumpSuccessors = this.state.peek().getSuccessors(true);
        if (jumpSuccessors.size() > 0){
            return MoveFeedback.FORCED_JUMP;
        }
        else{
            return MoveFeedback.PIECE_BLOCKED;
        }
    }

    public ArrayList<BoardState> getValidMoves(int posX,int posY) {
        return state.peek().getSuccessors(posY,posX);
    }

    public void aiMove(){
        // update state with AI move
        if (!isGameOver() && state.peek().getTurn() == Player.AI){
            BoardState newState = ai.move(this.state.peek(), Player.AI);
            updateState(newState);
        }
    }

    private void updateState(BoardState newState){
        state.push(newState);
        if(state.size() > memory){
            state.remove(0);
        }
    }

    public BoardState getState() {
        return state.peek();
    }


    public Player getTurn() {
        return state.peek().getTurn();
    }

    public boolean isGameOver(){
        boolean isOver = state.peek().isGameOver();
        if (isOver){
            // get win / lose status
            humanWon = state.peek().pieceCount.get(Player.AI) == 0;
        }
        return isOver;
    }

    public String getGameOverMessage(){
        String result = "Game Over. ";
        if (humanWon == true){
            result += "YOU WIN!";
        }
        else{
            result += "YOU LOSE!";
        }
        return result;
    }

    public void undo(){
        if (state.size() > 2){
            state.pop();
            while(state.peek().getTurn() == Player.AI){
                state.pop();
            }
        }
    }

}
