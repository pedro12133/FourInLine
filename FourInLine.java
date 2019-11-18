import java.util.Random;
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
    private int searchDepth;
    private int agentMove;

    // Constructor
    public FourInLine() {
        this.numOfPlayers = 2;
        this.connectionsToWin = 4;
        this.players = new Player[2];
        this.moveCount = 0;
        this.winner = -1;
        //setMaxTime();
        setPlayers();
        this.board = new Board(8, this.players);
    }

    // Game Modes
    public void playAgent() {
        int agentTurn = agentTurn();
        this.board.print();

        while(this.winner == -1) {
            for(int i = 0; i < this.numOfPlayers; i++) {
                System.out.println("move count: " +moveCount);
                Player currentPlayer = this.players[i];
                String move;

                if(i != agentTurn)
                    move = askForNextMove(currentPlayer);
                else {
                    move = alphaBetaSearch(this.board.getState(), 4);
                    System.out.println("Agent's move: "+move+" ");
                }

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

    /* Helper functions for playAgent and playOtherPLayer */
    private int agentTurn() {
        for(int i = 0; i < this.numOfPlayers; i ++)
            if(this.players[i].getId().equals("Agent"))
                return i;
        return -1;
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
            if(board.getState().getValueAt(i) == '-')
                return true;
        }
        return false;
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

    private boolean playerWon(Player player) {
        char playerCharacter = player.getCharacter();
        State state = this.board.getState();
        int count = 0;

        // check for horizontal win
        for(int i = 0; i < state.getSize(); i++) {
            if(state.getValueAt(i) == playerCharacter)
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
                if(state.getValueAt(i + j*8) == playerCharacter)
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

    private String askForNextMove(Player player) {
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
    /* End helper functions for playAgent and playOtherPLayer */

    private String alphaBetaSearch(State state, int depth) {
        if(this.moveCount < 2) {
            int [] bestFirstMove = {27,28,35,36};
            agentMove = bestFirstMove[new Random().nextInt(4)];
            while( !validCell( (char)(agentMove/8 + 'a')+""+((agentMove %8)+1) ) )
                agentMove = bestFirstMove[new Random().nextInt(4)];
        }
        else {
            this.searchDepth = depth;
            Node currentState = new Node(state,true,0);
            int v = maxValue(currentState,-1000000,1000000);
        }

        String row = (char)(agentMove /8 + 'a') + "";
        String column = Integer.toString((agentMove %8)+1);
        agentMove = 0;
        return row+column;
    }

    /* Helper functions for alphaBetaSearch */
    private int maxValue(Node node, int a, int b) {
        int terminalValue = terminalValue(node);
        if(terminalValue != 0)
            return terminalValue;

        if(node.getDepth() == this.searchDepth)
            return utility(node);

        int v = -10000000; // -infinity

        Node currentNode = node.nextSuccessor();
        while(currentNode != null) {
            int minValue = minValue(currentNode,a,b);
            /*if(node.getDepth() == 0) {
                System.out.print((char) (currentNode.getMoveIndex() / 8 + 'a') + "" + ((currentNode.getMoveIndex() % 8) + 1) + "-" + minValue + " ");
                if(((currentNode.getMoveIndex() % 8) + 1) == 8)
                    System.out.println();
            }*/
            if(minValue > v)
                v = minValue;
            if(v >= b)
                return v;
            if(v > a) {
                if(node.getDepth() == 0)
                    agentMove = currentNode.getMoveIndex();
                a = v;
            }
            currentNode = node.nextSuccessor();
        }

        return v;
    }

    private int minValue(Node node, int a, int b) {
        int terminalValue = terminalValue(node);
        if(terminalValue != 0)
            return terminalValue;

        if(node.getDepth() == this.searchDepth)
            return utility(node);

        int v = 10000000;

        Node currentNode = node.nextSuccessor();
        while(currentNode != null) {
            v = Math.min(v,maxValue(currentNode,a,b));
            if(v <= a)
                return v;
            b = Math.min(b,v);
            currentNode = node.nextSuccessor();
        }

        return v;
    }

    private int terminalValue(Node node) {
        State state = node.getState();
        String kill = "XXXX";
        String opponentKill = "OOOO";
        int value = 0;
        String row;
        String column;

        for(int i = 0; i < this.board.getSize(); i++) {
            row = "" + state.getValueAt((8 * i)) + state.getValueAt((8 * i) + 1) + state.getValueAt((8 * i) + 2) + state.getValueAt((8 * i) + 3);
            row += "" + state.getValueAt((8 * i) + 4) + state.getValueAt((8 * i) + 5) + state.getValueAt((8 * i) + 6) + state.getValueAt((8 * i) + 7);
            column = "" + state.getValueAt(i) + state.getValueAt(8 + i) + state.getValueAt(16 + i) + state.getValueAt(24 + i);
            column += "" + state.getValueAt(32 + i) + state.getValueAt(40 + i) + state.getValueAt(48 + i) + state.getValueAt(56 + i);

            if (row.contains(opponentKill) || column.contains(opponentKill)) {
                value += -10000;
                break;
            }
            else if (row.contains(kill) || column.contains(kill)) {
                value += 10000;
                break;
            }
        }
        return value;
    }

    private int utility(Node node) {
        int value = 0;
        value += killerMoveValue(node);
        return value;
    }

    private int killerMoveValue(Node node) {
        int value = 0;
        State state = node.getState();

        String inLine0 ="XX--";
        String inLine1 = "--XX";
        String inLine2 = "-XX-";
        String inLine7 = "X-X-";
        String inLine8 = "-X-X";

        String inLine3 = "XXX-";
        String inLine4 = "-XXX";
        String inLine5 = "X-XX";
        String inLine6 = "XX-X";

        String setUp0 = "-XX--";
        String setUp1 = "--XX-";
        String killer0 = "-XXX-";

        String opponentInLine0 = "OO--";
        String opponentInLine1 = "--OO";
        String opponentInLine2 = "-OO-";
        String opponentInLine7 = "O-O-";
        String opponentInLine8 = "-O-O";

        String opponentInLine3 = "OOO-";
        String opponentInLine4 = "-OOO";
        String opponentInLine5 = "O-OO";
        String opponentInLine6 = "OO-O";

        String opponentSetUp0 = "-OO--";
        String opponentSetUp1 = "--OO-";
        String opponentKiller0 = "-OOO-";

        String row;
        String column;

        for(int i = 0; i < this.board.getSize(); i++) {
            row = "" + state.getValueAt((8 * i)) + state.getValueAt((8 * i) + 1) + state.getValueAt((8 * i) + 2) + state.getValueAt((8 * i) + 3);
            row += "" + state.getValueAt((8 * i) + 4) + state.getValueAt((8 * i) + 5) + state.getValueAt((8 * i) + 6) + state.getValueAt((8 * i) + 7);
            column = "" + state.getValueAt(i) + state.getValueAt(8 + i) + state.getValueAt(16 + i) + state.getValueAt(24 + i);
            column += "" + state.getValueAt(32 + i) + state.getValueAt(40 + i) + state.getValueAt(48 + i) + state.getValueAt(56 + i);

            if (row.contains(opponentKiller0) || column.contains(opponentKiller0))
                value += -110;
            else if (row.contains(killer0) || column.contains(killer0))
                value += 100;
            else if (row.contains(opponentSetUp0) || column.contains(opponentSetUp0)
                    || row.contains(opponentSetUp1) || column.contains(opponentSetUp1))
                value += -11;
            else if (row.contains(setUp0) || column.contains(setUp0)
                    || row.contains(setUp1) || column.contains(setUp1))
                value += 10;
            else if (row.contains(opponentInLine6) || column.contains(opponentInLine6)
                    || row.contains(opponentInLine5) || column.contains(opponentInLine5)
                    || row.contains(opponentInLine4) || column.contains(opponentInLine4)
                    || row.contains(opponentInLine3) || column.contains(opponentInLine3))
                value += -5;
            else if (row.contains(inLine6) || column.contains(inLine6)
                    || row.contains(inLine5) || column.contains(inLine5)
                    || row.contains(inLine4) || column.contains(inLine4)
                    || row.contains(inLine3) || column.contains(inLine3))
                value += 5;
            else if (row.contains(opponentInLine2) || column.contains(opponentInLine2)
                    || row.contains(opponentInLine1) || column.contains(opponentInLine1)
                    || row.contains(opponentInLine0) || column.contains(opponentInLine0)
                    || row.contains(opponentInLine7) || column.contains(opponentInLine7)
                    || row.contains(opponentInLine8) || column.contains(opponentInLine8))
                value += -2;
            else if (row.contains(inLine0) || column.contains(inLine0)
                    || row.contains(inLine1) || column.contains(inLine1)
                    || row.contains(inLine2) || column.contains(inLine2)
                    || row.contains(inLine7) || column.contains(inLine7)
                    || row.contains(inLine8) || column.contains(inLine8))
                value += 2;
        }
        return value;
    }
    /* End of helper functions for alphaBetaSearch */

    public static void main(String [] args) {
        FourInLine FourInLine = new FourInLine();
        FourInLine.playAgent();
    }
}
