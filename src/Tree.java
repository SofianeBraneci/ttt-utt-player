public class Tree<T extends IBoard>{

    private Node<T> root;

    public Tree(Node<T> root) {
        this.root = root;
    }

    public Node<T> getRoot() {
        return root;
    }

    public void setRoot(Node<T> root) {
        this.root = root;
    }

    public void addChild(Node<T> parent, Node<T> child) {
        parent.getNodes().add(child);
    }
}
