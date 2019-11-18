public class Node {
    private State state;
    private int depth;
    private boolean isMax;
    private int moveIndex;
    private int currentSuccessorMoveIndex;

    public Node(State state, boolean isMax, int depth) {
        this.state = state;
        this.isMax = isMax;
        this.depth = depth;
        this.currentSuccessorMoveIndex = -1;
        this.moveIndex = -1;
    }

    public void restartSuccessors() { this.currentSuccessorMoveIndex = 0; }

    public Node nextSuccessor() {
        this.currentSuccessorMoveIndex++;

        if(this.currentSuccessorMoveIndex >= this.state.getSize())
            return null;

        if(this.state.getValueAt(this.currentSuccessorMoveIndex) != '-')
            return nextSuccessor();

        char playerCharacter;
        if(this.isMax)
            playerCharacter = 'X';
        else
            playerCharacter = 'O';

        State newState = new State(this.state);
        newState.setValueAt(playerCharacter,this.currentSuccessorMoveIndex);

        Node node = new Node(newState,!this.isMax, this.depth+1);
        node.setMoveIndex(this.currentSuccessorMoveIndex);
        return node;

    }

    public boolean isMax() {return isMax;}

    public void setState(State state) { this.state = state; }

    public void setDepth(int depth) { this.depth = depth; }

    public void setMoveIndex(int index) { this.moveIndex = index; }

    public State getState() { return state; }

    public int getDepth() {return depth;}

    public int getMoveIndex() { return moveIndex; }

}

