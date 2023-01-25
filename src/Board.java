import java.util.ArrayList;
import java.util.List;

public class Board implements IBoard{

    // representing the board
    private int[][] board;
    private final int DIM = 3;
    // keep track of the winner
    private boolean hasBeenWon = false;
    private Move lastMove = null;
    private Move origin = new Move(0, 0);
    private int lastPlayer = -1;

    public Board(){
        board = new int[DIM][DIM];

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                board[i][j] = -1;
            }
        }
    }

    private Board(int [][] b, Move lastMove, int lastPlayer, Move origin){
        this.board = b;
        this.lastMove = lastMove;
        this.lastPlayer = lastPlayer;
        this.origin = origin;
    }

    public Board(Move o){
        this();
        origin = o;
    }

    @Override
    public void setLastPlayer(int player) {
        lastPlayer = player;
    }

    @Override
    public Move getOrigin() {
        return origin;
    }

    @Override
    public void play(Move move, int player) {
        lastMove = move;
        lastPlayer = player;
        // play the move
        board[move.getRow()][move.getCol()] = player;
        // check the win
        hasBeenWon = hasPlayerWon(player);
    }

    @Override
    public int getLastPlayer() {
        return lastPlayer;
    }

    @Override
    public Move getLastMove() {
        return lastMove;
    }

    @Override
    public int getWinner() {
        return findWinner();
    }

    @Override
    public void undoMove(Move m) {
        // System.out.println("Undoing the move = "+ m);
        // undo the move
        board[m.getRow()][m.getCol()] = -1;
        // check if we still have a winner, otherwise set hasBeenWon to false
        //winner = findWinner();
        // update the state
        // hasBeenWon = winner != -1;
    }

    // return the winner or -1 if there is none
    private int findWinner(){
        for (int i = 0; i < DIM; i++) {
            // check rows
            if (board[i][0] > -1 && board[i][0] == board[i][1] && board[i][0] == board[i][2]) {
                return board[i][2];
            }

            // check cols
            if (board[0][i] > -1 && board[0][i] == board[1][i] && board[0][i] == board[2][i]) {
                return board[2][i];
            }
        }
        // check diags
        if (board[0][0] > -1 && board[0][0] == board[1][1] && board[0][0] == board[2][2]) {
            return board[2][2];
        }
        if (board[2][0] > -1 && board[2][0] == board[1][1] && board[2][0] == board[0][2]) {
            return board[0][2];
        }
        return -1;
    }

    @Override
    public boolean hasPlayerWon(int player) {
        return  player == findWinner();
    }

    @Override
    public boolean isGameOver() {
        return isDraw() || (findWinner() != -1);
    }

    @Override
    public boolean isDraw() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if(board[i][j] == -1){
                   return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<Move> availableMoves() {
        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if(board[i][j] == -1){
                    moves.add(new Move(i + origin.getRow(), j + origin.getCol()));
                }
            }
        }
        return moves;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                builder.append("|").append(board[i][j]);
            }
            builder.append("|\n");
        }
        return builder.toString();
    }

    @Override
    public IBoard copy() {
        int [][] b = new int[DIM][DIM];
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
               b [i][j] = board[i][j];
            }
        }
        return new Board(b, lastMove, lastPlayer, origin);
    }


}
