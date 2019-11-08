import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FourInLine {

    private int numOfPlayers;
    private Player [] players;
    private Board board;
    private int maxMoveTime;
    private int connectionsToWin;
    private int moveCount;
    private int winner;

    public FourInLine() {
        this.numOfPlayers = 2;
        this.connectionsToWin = 4;
        this.players = new Player[2];
        this.moveCount = 0;
        this.winner = -1;
        setMaxTime();
        setPlayers();
        this.board = new Board(8, this.players);
    }

    private boolean validCell(String cell) {
        Pattern pattern = Pattern.compile("[A-Ha-h][1-8]");
        Matcher matcher = pattern.matcher(cell);
        boolean validCell = matcher.matches();

        if(validCell) {
            cell = cell.toLowerCase();
            int y = cell.charAt(0) - 'a';
            int x = Integer.parseInt(cell.substring(1)) - 1;
            int i = y*8 + x;
            if(board.getState().getArray()[i] == '-')
                return true;
        }
        return false;
    }

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

    public int agentTurn() {
        for(int i = 0; i < this.numOfPlayers; i ++)
            if(this.players[i].getId().equals("Agent"))
                return i;
        return -1;

    }

    public void playAgent() {
        int x = agentTurn();
        System.out.println(x);
        this.board.print();
        while(this.winner == -1) {
            for(int i = 0; i < this.numOfPlayers; i++) {
                Player currentPlayer = this.players[i];
                String move;
                if(i != x)
                    move = askForNextMove(currentPlayer);
                else
                    move = alphaBetaSearch(this.board.getState());
                this.moveCount++;
                this.board.setMove(move,currentPlayer.getCharacter(),this.moveCount);
                this.board.print();
                if(playerWon(currentPlayer)) {
                    this.winner = i;
                    break;
                }
            }
            if(this.moveCount == 63) break;
        }

        if(this.winner != -1)
            System.out.println(this.players[this.winner].getId()+" won!");
        else
            System.out.println("Tie game");
    }

    public void playOtherPlayer() {
        this.board.print();
        while(this.winner == -1) {
            for(int i = 0; i < this.numOfPlayers; i++) {
                Player currentPlayer = this.players[i];
                String move = askForNextMove(currentPlayer);
                this.moveCount++;
                this.board.setMove(move,currentPlayer.getCharacter(),this.moveCount);
                this.board.print();
                if(playerWon(currentPlayer)) {
                    this.winner = i;
                    break;
                }
            }
            if(this.moveCount == 63) break;
        }

        if(this.winner != -1)
            System.out.println(this.players[this.winner].getId()+" won!");
        else
            System.out.println("Tie game");
    }

    private boolean playerWon(Player player) {
        char playerCharacter = player.getCharacter();
        int count = 0;
        State state = this.board.getState();

        // check for horizontal win
        for(int i = 0; i < state.getArray().length; i++) {
            if(state.getArray()[i] == playerCharacter)
                count++;
            else
                count = 0;
            if(count == connectionsToWin)
                return true;
            if(i%this.board.getSize() == this.board.getSize()-1)
                count = 0;
        }

        // check for vertical win
        for(int i = 0; i < this.board.getSize(); i++) {
            for(int j = 0; j < this.board.getSize(); j++) {
                if(state.getArray()[i + j*8] == playerCharacter)
                    count++;
                else
                    count = 0;
                if(count == connectionsToWin)
                    return true;
            }
            count = 0;
        }

        return false;
    }

    public String askForNextMove(Player player) {
        int [] xy = new int [2];
        Scanner scanner = new Scanner(System.in);
        System.out.print(player.getId() + "\'s next move: ");
        String in = scanner.nextLine();
        while (!validCell(in)) {
            System.out.print("Not valid, try again: ");
            in = scanner.nextLine();
        }
        return in.toLowerCase();
    }

    private void setPlayers() {
        Player agent = new Player("Agent", 'X');
        Player opponent = new Player("Opponent", 'O');
        Scanner scanner  = new Scanner(System.in);

        System.out.println("Choose first player");
        System.out.print("(X)"+agent.getId()+" or (O)"+opponent.getId()+": ");
        String in = scanner.nextLine().replaceAll(" ", "");// take out spaces

        while(!(in.equals("X") || in.equals("O") || in.equals("x") || in.equals("o"))) {
            System.out.print("Invalid choice, try again: ");
            in = scanner.nextLine().replaceAll(" ", "");
        }

        if(in.toLowerCase().equals("x")) {
            players[0] = agent;
            players[1] = opponent;
            agent.setTurn(1);
            opponent.setTurn(2);
        }
        else {
            players[1] = agent;
            players[0] = opponent;
            agent.setTurn(2);
            opponent.setTurn(1);
        }
    }

    private void setMaxTime() {
        Scanner s = new Scanner(System.in);
        System.out.print("Enter max number of seconds to make a move: ");
        String input = s.nextLine();
        while (!isInt(input)) {
            System.out.print("Invalid number of seconds, try again: ");
            input = s.nextLine();
        }
        this.maxMoveTime = Integer.parseInt(input);
    }

    private boolean isInt(String s) {
        boolean isValid = false;
        try {
            Integer.parseInt(s);
            isValid = true;
        }
        catch (NumberFormatException e){}
        return isValid;
    }

    public static void main(String [] args) {
        FourInLine FourInLine = new FourInLine();
        FourInLine.playOtherPlayer();
    }
}
