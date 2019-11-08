public class Node {
    State state;
    Node previousNode;
    Node nextNode;

    public Node(State state) { this.state = state; }

    public void setState(State state) { this.state = state; }

    public void setNextNode(Node nextNode) { this.nextNode = nextNode; }

    public void setPreviousNode(Node previousNode) { this.previousNode = previousNode; }

    public State getState() { return state; }

    public Node getNextNode() { return nextNode; }

    public Node getPreviousNode() { return previousNode; }
}

