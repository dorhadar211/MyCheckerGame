package main.game;

public enum MoveFeedback {
    FORCED_JUMP ("You're forced to take."),
    PIECE_BLOCKED ("This piece has no diagonal moves.");

    private final String name;

    MoveFeedback(String str) {
        name = str;
    }

    public String toString() {
        return this.name;
    }

}
