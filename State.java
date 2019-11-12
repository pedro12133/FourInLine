public class State {
    private char [] state;

    public State(int size) { this.state = new char [size];}
    public State(State state) {
        this.state = new char[state.size()];
        for(int i = 0; i < state.size(); i++)
            this.state[i] = state.getArray()[i];
    }
    public void setValue(char value, int i) {
        this.state[i] = value;
    }
    public char [] getArray() {
        return this.state;
    }
    public int size() {return state.length;}




}
