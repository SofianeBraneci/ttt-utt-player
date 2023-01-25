import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Strategies {
    private static final int MAX_DEPTH = 6;
    // 1 : AI
    // 0 : human

    public static Move findBestMove(IBoard board){
        int bestScore = Integer.MIN_VALUE;
        Move bestMove = null;
        List<Move> moves = board.availableMoves();
        for(Move move : moves){
            board.play(move, 1);
            // run min max
            int score = minMax(board,false, MAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE);
            //System.out.println("Score = " + score + " move = "+ move);
            if(score > bestScore){
                bestMove = move;
                bestScore = score;
            }
            // undo
            board.undoMove(move);
        }
        return bestMove;
    }
    public static int minMax(IBoard board, boolean isMax){
        // check if we are in a leaf
        if(board.isGameOver()){
            return eval(board);
        }
        // else, apply the min max
        List<Move> moves = board.availableMoves();
        if (isMax){
            int highest = Integer.MIN_VALUE;
            for(Move move : moves){
                board.play(move, 1);
                highest = Math.max(highest, minMax(board, false));
                board.undoMove(move);
            }
            return highest;
        }else{
            int lowest = Integer.MAX_VALUE;
            for(Move move : moves){
                board.play(move, 0);
                lowest = Math.min(lowest, minMax(board, true));
                board.undoMove(move);
            }
            return lowest;
        }
    }
    public static int minMax(IBoard board, boolean isMax, int depth){
        // check if we are in a leaf
        if(board.isGameOver()){
            return eval(board, depth);
        }
        // else, apply the min max
        List<Move> moves = board.availableMoves();
        if (isMax){
            int highest = Integer.MIN_VALUE;
            for(Move move : moves){
                board.play(move, 1);
                highest = Math.max(highest, minMax(board, false, depth - 1));
                board.undoMove(move);
            }
            return highest;
        }else{
            int lowest = Integer.MAX_VALUE;
            for(Move move : moves){
                board.play(move, 0);
                lowest = Math.min(lowest, minMax(board, true, depth - 1));
                board.undoMove(move);
            }
            return lowest;
        }
    }
    public static int minMax(IBoard board, boolean isMax, int depth, int alpha, int beta){
        // check if we are in a leaf
        if(board.isGameOver()){
            return eval(board, depth);
        }
        // else, apply the min max
        List<Move> moves = board.availableMoves();
        if (isMax){
            int highest = Integer.MIN_VALUE;
            for(Move move : moves){
                board.play(move, 1);
                highest = Math.max(highest, minMax(board, false, depth - 1, alpha, beta));
                board.undoMove(move);
                alpha = Math.max(highest, alpha);
                // no need to continue
                if(alpha >= beta) return highest;

            }
            return highest;
        }else{
            int lowest = Integer.MAX_VALUE;
            for(Move move : moves){
                board.play(move, 0);
                lowest = Math.min(lowest, minMax(board, true, depth - 1, alpha, beta));
                board.undoMove(move);
                beta = Math.min(lowest, beta);
                // no need to continue
                if(alpha >= beta) return lowest;
            }
            return lowest;
        }
    }
    private static int eval(IBoard board, int depth) {
        int winner = board.getWinner();
        if (winner == 1) {
            return 10 + depth;
        } else if (winner == 0) {
            return -10 - depth;
        }
        return 0;
    }
    private static int eval(IBoard board) {
        int winner = board.getWinner();
        if (winner == 1) {
            return 10;
        } else if (winner == 0) {
            return -10;
        }
        return 0;
    }

    public static IBoard bestBoard(List<IBoard> boards){

        IBoard best = null;
        int bestScore = Integer.MIN_VALUE;

        for(IBoard board : boards){
            int e = eval(board);
            if(e > bestScore){
                best = board;
                bestScore = e;
            }
        }
        return best;

    }
    public static <T extends IBoard> Move findBestMoveMCTS(T board){
        long start = System.currentTimeMillis();
        Node<T> root = new Node<>(board, null, board.getLastMove(), board.getLastPlayer(), new ArrayList<>());
        while(System.currentTimeMillis() - start < 70){

            // selection
            Node<T> promisingNode = selectPromisingNode(root);
            // expansion
            if(!promisingNode.getBoard().isGameOver()){
                expandNode(promisingNode);
            }
            // simulation
            Node<T> toExplore = promisingNode;
            if(toExplore.getNodes().size() > 0){
                toExplore = toExplore.getRandomChildNode();
            }
            int result = randomPlayOut(toExplore);
            backpropagation(toExplore, result);
            // update
        }
        Node<T> best = root.getChildWithMaxWinScore();

        return best.getMove();
    }

    public static Move findBestMoveMCTSUltimate(IUltimateBoard board){
        long start = System.currentTimeMillis();
        boolean isFirst = false;
        Node<IUltimateBoard> root = new Node<>(board, null, board.getLastMove(), board.getLastPlayer(), new ArrayList<>());
        while(System.currentTimeMillis() - start < 1000){

            // selection
            Node<IUltimateBoard> promisingNode = selectPromisingNode(root);
            // expansion
            if(!promisingNode.getBoard().isGameOver()){
                expandNodeUltimate(promisingNode, isFirst);
                isFirst = false;
            }
            // simulation
            Node<IUltimateBoard> toExplore = promisingNode;
            if(toExplore.getNodes().size() > 0){
                toExplore = toExplore.getRandomChildNode();
            }
            int result = randomPlayOutUltimate(toExplore);
            System.err.println("Winner = " + result);
            backpropagation(toExplore, result);
            // update
        }
        Node<IUltimateBoard> best = root.getChildWithMaxWinScore();

        return best.getMove();
    }

    public static  IBoard findBestBoard(IUltimateBoard board){
        long start = System.currentTimeMillis();
        Node<IBoard> root = new Node<>(board.getCurrentLocalBoard(), null, board.getLastMove(), board.getLastPlayer(), new ArrayList<>());
        while(System.currentTimeMillis() - start < 70){

            // selection
            Node<IBoard> promisingNode = selectPromisingNode(root);
            // expansion
            if(!promisingNode.getBoard().isGameOver()){
                expandNode(promisingNode);
            }
            // simulation
            Node<IBoard> toExplore = promisingNode;
            if(toExplore.getNodes().size() > 0){
                toExplore = toExplore.getRandomChildNode();
            }
            int result = randomPlayOut(toExplore);
            backpropagation(toExplore, result);
            // update
        }
        Node<IBoard> best = root.getChildWithMaxWinScore();

        return best.getBoard();
    }

    private static <T extends IBoard>  void backpropagation(Node<T> toExplore, int result) {
            Node<T> node = toExplore;
            while (node != null){
                node.incrementVisit();
                if(node.getPlayer() == result){
                        node.setWinScore(node.getWinScore() + 10);
                }
                node = node.getParent();
            }
    }

    private static <T extends IBoard> int randomPlayOut(Node<T> toExplore) {
        // System.err.println("In playout");
        Node<T> temp = toExplore.copy();
        IBoard current = temp.getBoard();
        int winner = current.getWinner();
        int opponent = current.getLastPlayer() == 1 ? 0: 1;

        Random gen = new Random();
        int player = opponent == 1 ? 0 : 1;
        List<Move> moves = current.availableMoves();
        Move m;
        int tries = 6;
        while (!current.isGameOver() && !moves.isEmpty() && tries > 0){
            m = moves.get(gen.nextInt(moves.size()));
            current.play(m, player);
            moves = current.availableMoves();
            winner = current.getWinner();
            player = player == 1 ? 0: 1;
            tries --;
        }
        // System.err.println("Done play");
        return winner;
    }

    private static  int randomPlayOutUltimate(Node<IUltimateBoard> toExplore) {
        // System.err.println("In playout");

        Node<IUltimateBoard> temp = toExplore.copy();

        IBoard current = temp.getBoard().getCurrentLocalBoard();

        int winner = temp.getBoard().getWinner();

        int opponent = temp.getBoard().getLastPlayer() == 1 ? 0: 1;

        if(winner == opponent){
            temp.getParent().setWinScore(Integer.MIN_VALUE);
            return winner;
        }
        Random gen = new Random();
        int player = opponent == 1 ? 0 : 1;

        List<Move> moves = current.availableMoves();

        // System.err.println(moves);

        // System.out.println(current.getOrigin());

        Move m;
        int tries = 6;
        while (!temp.getBoard().isGameOver() && !moves.isEmpty() && tries > 0){
            m = moves.get(gen.nextInt(moves.size()));
            Move global = temp.getBoard().toGlobalMove(m, temp.getBoard().getCurrentLocalBoard().getOrigin());
            temp.getBoard().play(global, player);
            moves = temp.getBoard().getCurrentLocalBoard().availableMoves();
            winner = temp.getBoard().getWinner();
            player = player == 1 ? 0: 1;
            tries --;
        }
        // System.err.println("Done play");
        return winner;
    }

    private static <T extends IBoard>  void expandNode(Node<T> promisingNode) {
        // System.err.println("In expand");
        int opponent = promisingNode.getBoard().getLastPlayer() == 1 ? 0:1;
        List<T> states = new ArrayList<>();
        T current = promisingNode.getBoard();

        for(Move m : current.availableMoves()){
            T state = (T) current.copy();
            state.play(m, opponent);
            states.add(state);
        }

        // System.err.println("states size = " + states.size());

        for(T state : states){
            Node<T> node = new Node<>(state, promisingNode, state.getLastMove(), state.getLastPlayer(), new ArrayList<>());
            promisingNode.getNodes().add(node);
        }
        // System.err.println("Done expand");

    }

    private static  void expandNodeUltimate(Node<IUltimateBoard> promisingNode, boolean isFirst) {
        // System.err.println("In expand");
        int opponent = promisingNode.getBoard().getLastPlayer() == 1 ? 0:1;

        List<IUltimateBoard> states = new ArrayList<>();
        IUltimateBoard current = promisingNode.getBoard();


        for(Move m : current.getCurrentLocalBoard().availableMoves()){
                IUltimateBoard state = (IUltimateBoard) current.copy();
                state.play(current.toGlobalMove(m, current.getCurrentLocalBoard().getOrigin()), opponent);
                states.add(state);
        }

        // System.err.println("states size = " + states.size());

        for(IUltimateBoard state : states){
            Node<IUltimateBoard> node = new Node<>(state, promisingNode, state.getLastMove(), state.getLastPlayer(), new ArrayList<>());
            promisingNode.getNodes().add(node);
        }
        // System.err.println("Done expand");

    }

    private static <T extends IBoard>  Node<T> selectPromisingNode(Node<T> root) {
        Node<T> node = root;
        while (node.getNodes().size() != 0){
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }
}
