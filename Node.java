import java.util.Arrays;
import java.util.Comparator;

public class Node {
    private State state;
    private Node parent;
    private Node [] successors;
    private int value;
    private int depth;
    private boolean isMax;
    int moveIndex;

    private int calculateNumberOfSuccessors() {
        int count = 0;
        for(char x : state.getArray())
            if(x == '-')
                count++;
        return count;
    }

    private void setSuccessors() {
        // value of each cell
        int [] cellValues = {
                1,2,3,4,4,3,2,1,
                2,3,4,5,5,4,3,2,
                3,4,5,6,6,5,4,3,
                4,5,6,7,7,6,5,4,
                4,5,6,7,7,6,5,4,
                3,4,5,6,6,5,4,3,
                2,3,4,5,5,4,3,2,
                1,2,3,4,4,3,2,1
        };
        int numberOfSuccessors = calculateNumberOfSuccessors();
        successors = new Node [numberOfSuccessors];
        char [] stateArray = state.getArray();
        int size = state.size();
        char playerCharacter;
        State newState;
        Comparator<Node> comparator;

        if(this.isMax) {
            comparator = new SortInAscendingValue();
            playerCharacter = 'X';
        }
        else {
            comparator = new SortInDescendingValue();
            playerCharacter = 'O';
        }

        int count = 0;
        for(int i = 0; i < size; i++) {
            if(stateArray[i] == '-') {
                newState = new State(this.state);
                newState.setValue(playerCharacter,i);
                this.successors[count] = new Node(newState,!this.isMax, this.depth+1);
                this.successors[count].setValue(cellValues[i] + this.value);
                this.successors[count].setMoveIndex(i);
                count++;
            }
        }
        Arrays.sort(successors, comparator);
    }

    public Node(State state, boolean isMax, int depth) {
        this.state = state;
        this.parent = null;
        this.successors = null;
        this.value = 0;
        this.isMax = isMax;
        this.depth = depth;
    }

    public void setState(State state) { this.state = state; }

    public void setParent(Node parent) { this.parent = parent; }

    public void setValue(int value) { this.value = value; }
    public void setDepth(int depth) { this.depth = depth; }
    public void setMoveIndex(int index) { this.moveIndex = index; }

    public State getState() { return state; }

    public Node [] getSuccessors() {
        setSuccessors();
        return successors;
    }

    public Node getParent() { return parent; }

    public int getValue() { return value; }

    public int getDepth() {return depth;}

    public int getMoveIndex() { return moveIndex; }

    public boolean isMax() {return isMax;}

    class SortInAscendingValue implements Comparator<Node>
    {
        public int compare(Node n1, Node n2) {
            return n2.getValue() - n1.getValue();
        }
    }
    class SortInDescendingValue implements Comparator<Node>
    {
        public int compare(Node n1, Node n2) {
            return n1.getValue() - n2.getValue();
        }
    }


}

