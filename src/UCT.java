import java.util.Collections;
import java.util.Comparator;

public class UCT {

    public static double uct(int totalVisit, double nodeWinScore, int nodeVisit){
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return (nodeWinScore / (double) nodeVisit) + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    public static <T extends IBoard> Node findBestNodeWithUCT(Node<T> node){
        int parentVisits = node.getVisitCount();
        return Collections.max(node.getNodes(), new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                return Double.compare(uct(parentVisits, o1.getWinScore(), o1.getVisitCount()),
                        uct(parentVisits, o2.getWinScore(), o2.getVisitCount()));
            }
        });

    }
}
