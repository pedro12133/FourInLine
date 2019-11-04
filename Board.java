
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Board {
    private ArrayList<String> board;
    private State state;
    private Player [] players;
    private int moveCount;
    private int size;
    private Player winner;
    private String [] winStrs;


    private double maxValue(State state, double a, double b) {
        if(terminal(state))
            return utility(state);
        double v = Double.NEGATIVE_INFINITY;
        //for each action act and successor state s
        State s = new State();
        for(int i = 0; i < 1000; i ++) {
            v = Math.max(v,minValue(s,a,b));
            if(v >= b)
                return v;
            a = Math.max(a,v);
        }
        return v;
    }

    private double minValue(State state, double a, double b) {
        return 0;
    }

    private double utility(State state) {
        return 0;
    }

    private boolean terminal(State state) {
        return false;
    }
    private String alphaBetaSearch(State state) {
        double v = maxValue(state,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
        // return the action in SUCCESSORS(state) w/ value v
        return "";
    }

    public Board(int size) {
        //make by size
        this.board = new ArrayList<String>();
        this.board.add("   1  2  3  4  5  6  7  8          ");
        this.board.add("A  -  -  -  -  -  -  -  -          ")      ;  /*m1m2*/
        this.board.add("B  -  -  -  -  -  -  -  -          ");
        this.board.add("C  -  -  -  -  -  -  -  -          ");
        this.board.add("D  -  -  -  -  -  -  -  -          ");
        this.board.add("E  -  -  -  -  -  -  -  -          ");
        this.board.add("F  -  -  -  -  -  -  -  -          ");
        this.board.add("G  -  -  -  -  -  -  -  -          ");
        this.board.add("H  -  -  -  -  -  -  -  -          ");
        this.state = new State(new char[64]); // all elements null
        this.moveCount = 0;
        this.size = size;
        this.winner = null;
        this.players = null;
    }

    public void setPlayers(Player agent, Player opponent) {
        Scanner scanner  = new Scanner(System.in);
        System.out.print("Choose first player\n(1)"+agent.getId()+" or (2)"+opponent.getId()+": ");
        String in = scanner.nextLine().replaceAll(" ", "");// take out spaces
        while(in.length() != 1 || !(in.equals("1") || in.equals("2"))) {
            System.out.print("Invalid, try again: ");
            in = scanner.nextLine().replaceAll(" ", "");
        }

        this.players = new Player [2];

        if(in.equals("1")) {
            this.players[0] = agent;
            this.players[1] = opponent;
            agent.setPlayerTurn(1);
            opponent.setPlayerTurn(2);
        }
        else {
            this.players[1] = agent;
            this.players[0] = opponent;
            agent.setPlayerTurn(2);
            opponent.setPlayerTurn(1);
        }

        this.winStrs = new String[this.players.length];
        for(Player player: this.players) {
            this.winStrs[player.getTurn() - 1] = "" + player.getMoveChar() + player.getMoveChar()
                    + player.getMoveChar() + player.getMoveChar();
        }
        System.out.println(this.players[0].getId()+" is player 1.");
        this.board.add(0, this.board.get(0)+this.players[0].getId()+"("+this.players[0].getMoveChar()+")"
                +" vs. "+this.players[1].getId()+"("+this.players[1].getMoveChar()+")");
        this.board.remove(1);

    }

    public void printBoard() {
        for(int i = 0; i < this.board.size(); i++)
            System.out.println(this.board.get(i));
    }

    public void askForNextMove() {
        this.moveCount++;
        for(Player player: this.players) {
            if(this.winningState())
                return;

            String id = player.getId();
            Scanner scanner = new Scanner(System.in);
            System.out.print(id + "\'s next move: ");
            String in = scanner.nextLine()/*br.readLine()*/;
            if(in == null) return;

            while (!validCell(in)) {
                System.out.print("Not valid, try again: ");
                in = scanner.nextLine()/*br.readLine()*/;
            }

            int letter;
            if (in.charAt(0) < 73)
                letter = in.charAt(0) - 64;
            else {
                letter = in.charAt(0) - 96;
                in = in.substring(1);
                in = (char) (letter + 64) + in;
            }
            int number = in.charAt(1) - 48;
            updateBoard(letter, number, in, player);
            this.state.getState()[(number-1)+(8*(letter-1))] = player.getMoveChar();
            this.printBoard();
        }
    }

    public Player getWinner() { return this.winner; }

    public int getMoveCount() { return moveCount; }

    private void updateBoard(int letter, int number, String in, Player player) {
        // update board
        char character = player.getMoveChar();
        String currentRow = this.board.get(letter);
        String newRow = currentRow.substring(0,(3*number));
        newRow += character;
        newRow += currentRow.substring(3*number + 1);
        this.board.add(letter,newRow);
        this.board.remove(letter+1);

        // update state
        if(this.moveCount < this.size + 1) {
            currentRow = this.board.get(this.moveCount);
            if (player.getTurn() == 1)
                newRow = currentRow + this.moveCount + ".   " + in;
            else
                newRow = currentRow + "      " + in;
            this.board.add(this.moveCount, newRow);
            this.board.remove(this.moveCount + 1);
        }
        else {
            currentRow = "                                   ";
            if (player.getTurn() == 1) {
                newRow = currentRow + this.moveCount + ((this.moveCount < 10) ? ".   " : ".  ") + in;
                this.board.add(this.moveCount, newRow);
            }
            else {
                currentRow = this.board.get(this.moveCount);
                newRow = currentRow + "      " + in;
                this.board.add(this.moveCount, newRow);
                this.board.remove(this.moveCount + 1);

            }

        }
    }

    private boolean validCell(String cell) {
        Pattern pattern1 = Pattern.compile("[A-H][1-8]");
        Matcher matcher1 = pattern1.matcher(cell);
        Pattern pattern2 = Pattern.compile("[a-h][1-8]");
        Matcher matcher2 = pattern2.matcher(cell);
        boolean validCell = matcher1.matches() || matcher2.matches();

        if(validCell) {
            int letter;
            if (cell.charAt(0) < 73)
                letter = cell.charAt(0) - 64;
            else {
                letter = cell.charAt(0) - 96;
                cell = cell.substring(1);
                cell = (char)(letter + 64) + cell;
            }
            int number = cell.charAt(1) - 48;
            if((int)this.state.getState()[(number-1)+(8*(letter-1))] == 0)
                return true;
        }

        return false;
    }

    private boolean winningState() {
        // check for horizontal win
        for(int i = 1; i < size+1; i++) {
            for (int x = 0; x < this.players.length; x++) {
                if (this.board.get(i).replaceAll(" ","").contains(winStrs[x])) {
                    this.winner = this.players[x];
                    return true;
                }
            }
        }

        // check for horizontal win
        String [] columns = new String [this.size];
        for(int i = 0; i < 64; i ++) {
            char cell = this.state.getState()[i];
            if(cell != 0)
                cell = '-';
            if(columns[i%8] == null)
                columns[i%8] = "-";
            columns[i%8] += this.state.getState()[i];
        }
        for(String col : columns)
            for (int x = 0; x < this.winStrs.length; x++)
                if(col != null)
                    if (col.contains(this.winStrs[x])) {
                        this.winner = this.players[x];
                        return true;
                    }

        // maybe check for diagonal win

        return false;
    }

}
