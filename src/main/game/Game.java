package main.game;

import java.util.ArrayList;
import java.util.Stack;

public class Game{

    private Stack<BoardState> state;
    private int memory;
    private AI ai;
    private String winner;
    private int onlyKingsMoveCounter;

    private boolean playerBlocked;

    public Game(){
        memory = Settings.UNDO_MEMORY;
        state = new Stack<>();
        state.push(BoardState.InitialState());
        ai = new AI();
        onlyKingsMoveCounter =0;
        playerBlocked = false;
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

    public void playerMove(BoardState newState){
        if (!isGameOver() && state.peek().getTurn() == Player.HUMAN){
            if(this.state.peek().pieceCount.get(Player.AI)==this.state.peek().kingCount.get(Player.AI)
                    && this.state.peek().pieceCount.get(Player.HUMAN)==this.state.peek().kingCount.get(Player.HUMAN) ){
                if(this.state.peek().kingCount.get(Player.HUMAN) == newState.kingCount.get(Player.HUMAN)){
                    this.onlyKingsMoveCounter++;
                }
                else {
                    this.onlyKingsMoveCounter =0;
                }
            }
            updateState(newState);
            ArrayList<BoardState> successors = newState.getSuccessors();
            if(successors.size()==0){
                this.playerBlocked =true;
            }
        }
    }

    public void aiMove(){
        // update state with AI move
            if (!isGameOver() && state.peek().getTurn() == Player.AI){
                BoardState newState = ai.move(this.state.peek());
                if(this.state.peek().pieceCount.get(Player.AI)==this.state.peek().kingCount.get(Player.AI)
                        && this.state.peek().pieceCount.get(Player.HUMAN)==this.state.peek().kingCount.get(Player.HUMAN) ){
                    if(this.state.peek().kingCount.get(Player.AI) == newState.kingCount.get(Player.AI)){
                        this.onlyKingsMoveCounter++;
                    }
                    else {
                        this.onlyKingsMoveCounter =0;
                    }
                }
                updateState(newState);
                ArrayList<BoardState> successors = newState.getSuccessors();
                if(successors.size()==0){
                    this.playerBlocked =true;
                }
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
        if (state.peek().isGameOver()){
            // get win / lose status
            if(state.peek().pieceCount.get(Player.HUMAN) > state.peek().pieceCount.get(Player.AI)){
                this.winner = "HUMAN";
            }
            else {
                this.winner = "AI";
            }
            return true;
        }
        else if (this.onlyKingsMoveCounter>=20) {
            this.winner = "TIE";
            return true;
        }
        else if (playerBlocked == true) {
            if(state.peek().getTurn() == Player.HUMAN){
                this.winner ="AI";
            }
            else{
                this.winner ="HUMAN";
            }
            return true;
        }
        return false;
    }

    public String getGameOverMessage(){
        String result = "Game Over. ";
        if (winner == "HUMAN"){
            result += "YOU WIN!";
        }
        else if (winner == "AI"){
            result += "YOU LOSE!";
        }
        else {
            result += "TIE!";
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
