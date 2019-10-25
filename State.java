public class State {
    private char [] state;

    public State(char [] state) {
        this.state = state;
    }
    public State(int size) { this.state = new char [size];}
    public State copy() {
        char [] state = new char [56];
        for(int i = 0; i < 56; i ++)
            state[i] = this.state[i];
        return new State(state);
    }
    public char [] getState() {
        return this.state;
    }
}
