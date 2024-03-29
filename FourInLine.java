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
    private long startTime;

    // Constructor
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

    // Game Modes
    public void playAgent(int depth) {
        this.searchDepth = depth;
        int agentTurn = agentTurn();
        this.board.print();
        while(this.winner == -1) {

            for(int i = 0; i < this.numOfPlayers; i++) {

                if((moveCount == board.getState().getSize() - searchDepth))
                    this.searchDepth--;

                Player currentPlayer = this.players[i];
                String move;

                if(i != agentTurn)
                    move = askForNextMove(currentPlayer);
                else {
                    move = alphaBetaSearch(this.board.getState());
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
            if(this.moveCount >= 64) break;
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

    private String alphaBetaSearch(State state) {

        if(this.moveCount < 2) {
            int [] bestFirstMove = {27,28,35,36};
            this.agentMove = bestFirstMove[new Random().nextInt(4)];
            while( !validCell( (char)(this.agentMove/8 + 'a')+""+((this.agentMove %8)+1) ) )
                this.agentMove = bestFirstMove[new Random().nextInt(4)];
        }
        else {
            try {
                this.startTime = System.currentTimeMillis();
                Node currentState = new Node(state,true,0);
                if(this.searchDepth == 0) {
                    for (int i = 0; i < this.board.getState().getSize(); i++) {
                        if (this.board.getState().getValueAt(i) == '-') {
                            this.agentMove = i;
                        }
                    }
                }
                else
                    maxValue(currentState,-1000000,1000000);
            }
            catch (OutOfTimeException e) {
                System.out.println(e);
            }

        }

        String row = (char)(this.agentMove /8 + 'a') + "";
        String column = Integer.toString((this.agentMove %8)+1);
        this.agentMove = 0;
        return row+column;
    }
    /* Helper functions for alphaBetaSearch */
    private int maxValue(Node node, int a, int b) throws OutOfTimeException {

        if(OutOfTime())
            throw new OutOfTimeException();

        int terminalValue = terminalValue(node);
        if(terminalValue != 0)
            return terminalValue;

        if(node.getDepth() == this.searchDepth)
            return utility(node);

        int v = -10000000; // -infinity
        Node currentNode = node.nextSuccessor();
        while(currentNode != null) {
            int minValue = minValue(currentNode,a,b);
            v = Math.max(minValue,v);
            if(v >= b)
                return v;
            if(v > a) {
                if(node.getDepth() == 0)
                    this.agentMove = currentNode.getMoveIndex();
                a = v;
            }
            currentNode = node.nextSuccessor();
        }

        return v;
    }
    private int minValue(Node node, int a, int b) throws OutOfTimeException {

        if(OutOfTime())
            throw new OutOfTimeException();

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
        int c = -1;
        int x = -1;

        for(int i = 0; i < this.board.getSize(); i++) {

            x = 8*i;
            row = "" + state.getValueAt(x) + state.getValueAt(x + 1);
            row += "" + state.getValueAt(x + 2) + state.getValueAt(x + 3);
            row += "" + state.getValueAt(x + 4) + state.getValueAt(x + 5);
            row += "" + state.getValueAt(x + 6) + state.getValueAt(x + 7);

            x = i;
            column = "" + state.getValueAt(x) + state.getValueAt(8 + x);
            column += "" + state.getValueAt(16 + x) + state.getValueAt(24 + x);
            column += "" + state.getValueAt(32 + x) + state.getValueAt(40 + x);
            column += "" + state.getValueAt(48 + x) + state.getValueAt(56 + x);

            if (row.contains(opponentKill) || column.contains(opponentKill)) {
                value += -10000;
                break;
            }
            else if (row.contains(kill) || column.contains(kill)) {
                value += 10000 - node.getDepth();
                break;
            }
        }
        return value;
    }
    private int utility(Node node) {
        int value = 0;
        State state = node.getState();

        int setupCount = 0;
        int threeInLineCount = 0;

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
        String setUp2 = "-X-X-";
        String killer0 = "-XXX-";

        int opponentSetupCount = 0;
        int opponentThreeInLineCount = 0;

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
        String opponentSetUp2 = "-O-O-";
        String opponentKiller0 = "-OOO-";

        String row;
        String column;

        for(int i = 0; i < this.board.getSize(); i++) {
            int x = 8*i;
            row = "" + state.getValueAt(x) + state.getValueAt(x + 1);
            row += "" + state.getValueAt(x + 2) + state.getValueAt(x + 3);
            row += "" + state.getValueAt(x + 4) + state.getValueAt(x + 5);
            row += "" + state.getValueAt(x + 6) + state.getValueAt(x + 7);

            x = i;
            column = "" + state.getValueAt(x) + state.getValueAt(8 + x);
            column += "" + state.getValueAt(16 + x) + state.getValueAt(24 + x);
            column += "" + state.getValueAt(32 + x) + state.getValueAt(40 + x);
            column += "" + state.getValueAt(48 + x) + state.getValueAt(56 + x);

            //row check
            if (row.contains(opponentKiller0))
                return -500;
            else if (row.contains(killer0))
                return 500;
            else if (row.contains(opponentSetUp0) || row.contains(opponentSetUp1) || row.contains(opponentSetUp2)) {
                value += -15;
                opponentSetupCount++;
            }
            else if(row.contains(setUp0) || row.contains(setUp1) || row.contains(setUp2)) {
                value += 10;
                setupCount++;
            }
            else if (row.contains(opponentInLine6) || row.contains(opponentInLine5)
                    || row.contains(opponentInLine4) || row.contains(opponentInLine3)) {
                value += -7;
                opponentThreeInLineCount++;
            }
            else if (row.contains(inLine6) || row.contains(inLine5)
                    || row.contains(inLine4) || row.contains(inLine3)) {
                value += 5;
                threeInLineCount++;
            }
            else if (row.contains(opponentInLine2) || row.contains(opponentInLine1)
                    || row.contains(opponentInLine0) || row.contains(opponentInLine7)
                    || row.contains(opponentInLine8)) {
                value += -3;
            }
            else if (row.contains(inLine0) || row.contains(inLine1)
                    || row.contains(inLine2) || row.contains(inLine7)
                    || row.contains(inLine8))
                value += 2;

            // column check
            if(column.contains(opponentKiller0))
                return -500;
            else if(column.contains(killer0))
                return 500;
            else if (column.contains(opponentSetUp0) || column.contains(opponentSetUp1) || column.contains(opponentSetUp2)) {
                value += -15;
                opponentSetupCount++;
            }
            else if(column.contains(setUp0) || column.contains(setUp1) || column.contains(setUp2)) {
                value += 10;
                setupCount++;
            }
            else if (column.contains(opponentInLine6) || column.contains(opponentInLine5)
                    || column.contains(opponentInLine4) || column.contains(opponentInLine3)){
                value += -7;
                opponentThreeInLineCount++;
            }
            else if (column.contains(inLine6) || column.contains(inLine5)
                    || column.contains(inLine4) || column.contains(inLine3)) {
                value += 5;
                threeInLineCount++;
            }
            else if (column.contains(opponentInLine2) || column.contains(opponentInLine1)
                    ||  column.contains(opponentInLine0) || column.contains(opponentInLine7)
                    || column.contains(opponentInLine8))
                value += -3;
            else if (column.contains(inLine0) || column.contains(inLine1)
                    || column.contains(inLine2) || column.contains(inLine7)
                    || column.contains(inLine8))
                value += 2;
        }

        if(opponentSetupCount > 0 && opponentThreeInLineCount > 0)
            return  -500;
        if(opponentThreeInLineCount > 1)
            return -500;
        if(opponentSetupCount > 1)
            return -500;
        if(setupCount > 0 && threeInLineCount > 0)
            return 500;
        if(setupCount > 1)
            return 500;
        if(threeInLineCount > 1)
            return 500;
        return value;
    }
    public boolean OutOfTime() {
        long endTime = System.currentTimeMillis();
        long runtime = endTime - this.startTime;
        if(runtime >= this.maxMoveTime*1000)
            return true;
        return false;
    }
    /* End of helper functions for alphaBetaSearch */

    public static void main(String [] args) {
        FourInLine FourInLine = new FourInLine();
        FourInLine.playAgent(4);
    }
}
