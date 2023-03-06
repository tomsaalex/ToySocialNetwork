package socialnetwork.toysocialnetwork.graph;

/**
 * Represents a node in a graph.
 */
public class Node {
    static final int MAX_DISTANCE = Integer.MAX_VALUE;

    private Long id;
    private Color color;
    private int distance;

    private int componentNum;

    private Node previous;
    private int discoveryTime;
    private int finishTime;

    private int value;

    public int getComponentNum() {
        return componentNum;
    }

    public void setComponentNum(int componentNum) {
        this.componentNum = componentNum;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public Node getPrevious() {
        return previous;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getDiscoveryTime() {
        return discoveryTime;
    }

    public void setDiscoveryTime(int discoveryTime) {
        this.discoveryTime = discoveryTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Node{" +
                "id=" + id + '}';
    }

    public Node(Long id) {
        this.id = id;
        this.color = Color.White;
        this.discoveryTime = 0;
        this.finishTime = 0;
        this.distance = 0;
        this.componentNum = 0;
    }
}
