import java.util.List;

public interface IBoard {

    void play(Move move, int player);
    void undoMove(Move m);
    boolean hasPlayerWon(int player);
    int getWinner();
    boolean isGameOver();
    boolean isDraw();
    List<Move> availableMoves();
    Move getOrigin();
    IBoard copy();
    Move getLastMove();
    int getLastPlayer();
    void setLastPlayer(int player);
}
