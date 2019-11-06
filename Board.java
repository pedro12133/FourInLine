
import java.util.ArrayList;
import java.util.List;

public class Board {
    private State state;
    private int size;
    private List<String> board;

    private double maxValue(State state, double a, double b) {
        if(terminalTest(state))
            return utility(state);
        double v = Double.NEGATIVE_INFINITY;
        //for each action act and successor state s
        State s = new State(64);
        for(int i = 0; i < 1000; i ++) {
            v = Math.max(v,minValue(s,a,b));
            if(v >= b)
                return v;
            a = Math.max(a,v);
        }
        return v;
    }

    private double minValue(State state, double a, double b) {
        if(terminalTest(state))
            return utility(state);
        double v = Double.POSITIVE_INFINITY;
        //for each action act and successor state s
        State s = new State(64);
        for(int i = 0; i < 1000; i ++) {
            v = Math.min(v,minValue(s,a,b));
            if(v <= a)
                return v;
            a = Math.min(a,v);
        }
        return v;
    }

    private double utility(State state) {
        return 0;
    }

    private boolean terminalTest(State state) {
        return false;
    }

    private String alphaBetaSearch(State state) {
        double v = maxValue(state,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
        // return the action in SUCCESSORS(state) w/ value v
        return "";
    }



    public Board(int size, Player [] players) {
        this.size = size;
        this.state = new State(this.size*this.size);
        for(int i = 0; i < this.size*this.size; i++)
            this.state.getArray()[i] = '-';
        initializeBoard(players);
    }

    private void initializeBoard(Player [] players) {
        this.board = new ArrayList<String>();
        String xLabels = "   1  2  3  4  5  6  7  8";
        char [] yLabels = {'A','B','C','D','E','F','G','H'};
        String row = "";

        this.board.add(xLabels+"    "+players[0].getId()+" vs "+players[1].getId());
        for(int i = 0; i < this.size*this.size; i++) {
            if(i%this.size == 0 && i != 0)
                this.board.add(row);
            if(i%this.size == 0)
                row = yLabels[i/this.size]+"  ";
            row = row+this.state.getArray()[i]+"  ";
        }
        this.board.add(row);
    }

    public void print() {
        for(String row: this.board)
            System.out.println(row);
    }

    public void setMove(String move, char playerCharacter, int moveCount) {
        int row = move.charAt(0) - 'a';
        int column = Integer.parseInt(move.substring(1)) - 1;
        int i = row*8 + column;
        this.state.getArray()[i] = playerCharacter;
        updateBoard(row, column, playerCharacter, moveCount, move);
    }

    private void updateBoard(int row, int column, char playerCharacter, int moveCount, String move) {

        // update state
        String updatedRow = board.get(row+1).substring(0,3*(column+1));
        updatedRow += playerCharacter;
        updatedRow += board.get(row+1).substring(3*(column+1)+1);
        board.set(row+1, updatedRow);

        // update moves
        if(moveCount < (this.size*2)+1) {
            if(moveCount % 2 == 1) {
                updatedRow = board.get(moveCount/2 + 1);
                updatedRow += "     "+(moveCount / 2 + 1)+". "+move.charAt(0)+(column+1);
                board.set(moveCount/2 + 1, updatedRow);
            }
            else {
                updatedRow = board.get(moveCount/2);
                updatedRow += " "+move.charAt(0)+(column+1);
                board.set(moveCount/2, updatedRow);
            }
        }
        else {
            updatedRow = "                               "+(moveCount < 19 ? " ": "");
            if(moveCount % 2 == 1) {
                updatedRow += (moveCount / 2 + 1) + ". " + move.charAt(0) + (column + 1);
                board.add(updatedRow);
            }
            else {
                updatedRow = board.get(moveCount/2);
                updatedRow += " "+move.charAt(0)+(column+1);
                board.set(moveCount/2, updatedRow);
            }
        }
    }

    public State getState() {return this.state;}

    public int getSize() {return this.size;}
}
