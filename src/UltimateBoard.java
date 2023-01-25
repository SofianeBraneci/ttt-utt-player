import java.util.ArrayList;
import java.util.List;

public class UltimateBoard implements IUltimateBoard{

    private final int DIM = 3;
    // the board of board
    private final IBoard[][] boards;
    // keep track of the local wins
    private final int[][] wins;
    // keep track of the origins of each local board in the global board
    private final List<Move> origins;
    // keep track of the current local board, on which the game is played
    private  IBoard current;
    // keep track of the next board based on the previous move
    private  IBoard next;
    // keep track of the previous board that led to the current board
    private IBoard previous;
    private boolean undoHappened;
    private Move lastMove = null;
    private int lastPlayer = -1;
    private boolean useNext = true;


    public UltimateBoard() {
        // init the global board
        boards = new Board[DIM][DIM];
        wins = new int[DIM][DIM];
        origins = new ArrayList<>();
        current = null;
        next = null;
        previous = null;
        undoHappened = false;
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                // System.out.println("Position = " + i + " " + j + " Origin = " + new Move(i * 3, j * 3));
                boards[i][j] = new Board(new Move(i * 3, j * 3));
                origins.add(new Move(i * 3, j * 3));
                wins[i][j] = -1;
            }
        }

        System.out.println(origins);

    }

    public UltimateBoard(IBoard[][] boards, int[][] wins,
                         List<Move> origins, IBoard current,
                         IBoard next, IBoard previous,
                         boolean undoHappened,
                         Move lastMove, int lastPlayer) {
        this.boards = boards;
        this.wins = wins;
        this.origins = origins;
        this.current = current;
        this.next = next;
        this.previous = previous;
        this.undoHappened = undoHappened;
        this.lastPlayer = lastPlayer;
        this.lastMove = lastMove;
    }

    @Override
    public IBoard getLocalBoard(Move global) {
        Move local = toLocalMove(global);
        return boards[local.getRow()][local.getCol()];
    }

    @Override
    public void play(Move move, int player) {
        // if local board has not been determined before
        // find the origin of the local board where the move is meant to be played
        // transform the move to a local move and play it
        // the next board is determined by the coordinates of the local move
        // if local board has already been selected
        // do the similar transformations
        // determine the next board based on the move
        System.err.println("To play = " + move);
        lastMove = move;
        lastPlayer = player;


        if(current != null && current.isGameOver()){
            // find the new local board based on the origin of the move
            Move origin = findOriginOfLocalBoardByMove(move);
            int currentX = origin.getRow() / DIM;
            int currentY = origin.getCol() / DIM;
            //System.err.println("Last board is full " + move +
            //       " should be played at the local board located at = " + currentX + " " + currentY);
            current = boards[currentX][currentY];

        }
        if(current == null){
            Move origin = findOriginOfLocalBoardByMove(move);
            int currentX = origin.getRow() / DIM;
            int currentY = origin.getCol() / DIM;
           // System.err.println("Move = " + move +
                 //   " should be played at the local board located at = " + currentX + " " + currentY);
            current = boards[currentX][currentY];
        }
       //System.err.println("Current origin = " + current.getOrigin());
        Move local = new Move(move.getRow() - current.getOrigin().getRow(), move.getCol() - current.getOrigin().getCol());
        System.err.println("Current local board is located at = "
               + current.getOrigin().getRow() / DIM + " "+ current.getOrigin().getCol() / DIM + " local = " + local);
        current.play(local, player);
        if(current.hasPlayerWon(player)){
            wins[local.getRow()][local.getCol()] = player;
        }
        // update the next and the previous
        previous = current;
        current = boards[local.getRow()][local.getCol()];

        // System.err.println("Next board is located at "+ local + " origin = " + current.getOrigin());
        undoHappened = false;
        useNext = true;
    }


    @Override
    public void undoMove(Move m) {
        //System.out.println("Move " + m + " has been undone");
        undoHappened = true;
        // the move is in global coordinates
        // all we have to do is
        // transform it to a local coordinates and undo the move at the current grid
        Move local = toLocalMove(m);
        //System.out.println("Current local board is located at = "
        // + current.getOrigin().getRow() / DIM + " "+ current.getOrigin().getCol() / DIM );
        current.undoMove(local);
        current = previous;
        //System.out.println("Falling back to the previous board which located at " + current.getOrigin().getRow() / DIM + " "+ current.getOrigin().getCol() / DIM);

    }

    @Override
    public void setCurrentBoard(IBoard board) {
        current = board;
        useNext = false;
    }

    @Override
    public boolean hasPlayerWon(int player) {
        return findWinner() == player;
    }

    private int findWinner() {
        for (int i = 0; i < DIM; i++) {
            // check rows
            if (wins[i][0] > -1 && wins[i][0] == wins[i][1] && wins[i][0] == wins[i][2]) {
                return wins[i][2];
            }

            // check cols
            if (wins[0][i] > -1 && wins[0][i] == wins[1][i] && wins[0][i] == wins[2][i]) {
                return wins[2][i];
            }
        }
        // check diags
        if (wins[0][0] > -1 && wins[0][0] == wins[1][1] && wins[0][0] == wins[2][2]) {
            return wins[2][2];
        }
        if (wins[2][0] > -1 && wins[2][0] == wins[1][1] && wins[2][0] == wins[0][2]) {
            return wins[0][2];
        }
        return -1;
    }

    @Override
    public int getWinner() {
        return findWinner();
    }

    @Override
    public boolean isGameOver() {
        return isDraw() || (findWinner() != -1);
    }

    @Override
    public boolean isDraw() {
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if(!boards[i][j].isDraw()) return false;
            }
        }
        return true;
    }

    @Override
    public List<Move> availableMoves() {
        List<Move> moves = new ArrayList<>();
        for(IBoard board : getAvailableBoard()){
            if(!board.isGameOver()){
                for(Move local : board.availableMoves()){
                    moves.add(toGlobalMove(local, board.getOrigin()));
                }
            }
        }
        return moves;
    }

    @Override
    public Move getOrigin() {
        return new Move(0,0);
    }

    @Override
    public IBoard getPreviousBoard() {
        return null;
    }

    @Override
    public Move toLocalMove(Move global) {
        Move origin = findOriginOfLocalBoardByMove(global);

        return new Move(global.getRow() - origin.getRow(), global.getCol() - origin.getCol());
    }

    @Override
    public Move findOriginOfLocalBoardByMove(Move global) {
        for(Move o : origins){
            if((o.getRow() <= global.getRow() &&  global.getRow()< o.getRow() + DIM)
                    && (o.getCol() <= global.getCol() && global.getCol() < o.getCol() + DIM)){
                return o;
            }
        }
        return null;
    }

    @Override
    public Move toGlobalMove(Move local, Move origin) {

        return new Move(local.getRow() + origin.getRow(), local.getCol() + origin.getCol()) ;
    }

    @Override
    public IBoard getCurrentLocalBoard() {
        return current;
    }

    @Override
    public IBoard getNextLocalBoard() {
        return next;
    }

    @Override
    public List<IBoard> getAvailableBoard() {
        List<IBoard> bs = new ArrayList<>();
        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                if(!boards[i][j].isGameOver()) bs.add(boards[i][j]);
            }
        }
        return bs;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < DIM; i++) {
            builder.append("Line = ").append(i).append("\n");
            for (int j = 0; j < DIM; j++) {
                builder.append(boards[i][j]).append("\n----\n");
            }
        }
        return builder.toString();
    }

    @Override
    public IUltimateBoard copy() {
        IBoard[][] boards = new Board[DIM][DIM];
        int [][] wins = new int[DIM][DIM];

        for (int i = 0; i < DIM; i++) {
            for (int j = 0; j < DIM; j++) {
                boards[i][j] = this.boards[i][j].copy();
                wins[i][j] = this.wins[i][j];
            }
        }
        IBoard c, p, n;
        c = copyLocal(current);
        p = copyLocal(previous);
        n = copyLocal(next);
        List<Move> origins = new ArrayList<>();
        origins.addAll(this.origins);

        return new UltimateBoard(boards, wins, origins, c, n, p, undoHappened, lastMove, lastPlayer);
    }

    private IBoard copyLocal(IBoard board){
        if(board != null){
            return board.copy();
        }

        return null;
    }

    @Override
    public Move getLastMove() {
        return lastMove;
    }

    @Override
    public void setLastPlayer(int player) {

    }

    @Override
    public int getLastPlayer() {
        return lastPlayer;
    }
}
