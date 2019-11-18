public class State {
    private char [] state;

    public State(int size) {
        this.state = new char [size];
        for(int i = 0; i < size; i++)
            this.state[i] = '-';
    }

    public State(State state) {
        this.state = new char[state.getSize()];
        for(int i = 0; i < state.getSize(); i++)
            this.state[i] = state.getValueAt(i);
    }

    public void setValueAt(char value, int i) {
        this.state[i] = value;
    }

    public char getValueAt(int i) {
        return this.state[i];
    }

    public int getSize() {return state.length;}




}
