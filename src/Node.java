import java.util.*;

public class Node<T extends IBoard> {
    private Node parent;
    private List<Node<T>> nodes;
    private Move move;
    private int player;
    private int visitCount;
    private double winScore;
    private T board;
    private final Random gen = new Random();
    public Node(T board ,Node parent, Move move, int player, List<Node<T>> nodes) {
        this.board = board;
        this.visitCount = 0;
        this.winScore = 0.0;
        this.player = player;
        this.parent = parent;
        this.move = move;
        this.nodes = nodes;
    }

    public Node(T board, Node parent) {
        this(board, parent,null, -1, new ArrayList<>());
    }

    public Node(T board) {
        this(board,null);
    }


    public Node<T> getParent() {
        return parent;
    }

    public List<Node<T>> getNodes() {
        return nodes;
    }

    public Move getMove() {
        return move;
    }

    public int getPlayer() {
        return board.getLastPlayer();
    }

    public int getVisitCount() {
        return visitCount;
    }

    public double getWinScore() {
        return winScore;
    }

    public T getBoard() {
        return board;
    }

    public Node<T> getRandomChildNode(){
        return nodes.get(gen.nextInt(nodes.size()));
    }

    public Node<T> getChildWithMaxWinScore(){
        return Collections.max(nodes, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return Double.compare(o1.winScore, o2.winScore);
            }
        });
    }

    public Node<T> copy(){
        List<Node<T>> nodes = new ArrayList<>();
        for(Node<T> node : this.nodes){
            nodes.add(new Node<T>((T) node.board.copy(), parent == null ? null : parent.copy(), move, player, new ArrayList<>(node.getNodes()) );
        }
        return new Node<T>((T) board.copy(), parent == null ? null : parent.copy(), move, player, nodes);
    }



    public void setParent(Node parent) {
        this.parent = parent;
    }

    public void setNodes(List<Node<T>> nodes) {
        this.nodes = nodes;
    }

    public void setMove(Move move) {
        this.move = move;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public void setWinScore(double winScore) {
        this.winScore = winScore;
    }

    public void setBoard(T board) {
        this.board = board;
    }

    public void incrementVisit() {
        visitCount++;
    }
}
