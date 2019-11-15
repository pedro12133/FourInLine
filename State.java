public class State {
    private char [] state;

    public State(int size) { this.state = new char [size];}
    public State(State state) {
        this.state = new char[state.size()];
        for(int i = 0; i < state.size(); i++)
            this.state[i] = state.getArray()[i];
    }
    public void setValueAt(char value, int i) {
        this.state[i] = value;
    }
    public char getValueAt(int i) {
        return this.state[i];
    }
    public char [] getArray() {
        return this.state;
    }
    public int size() {return state.length;}




}
