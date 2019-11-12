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
    private int agentTurn;

    private int searchDepth;
    private int nodeCount;
    private int prunes;
    private int nextMoveIndex;

    public FourInLine() {
        this.numOfPlayers = 2;
        this.connectionsToWin = 4;
        this.players = new Player[2];
        this.moveCount = 0;
        this.winner = -1;
        setMaxTime();
        setPlayers();
        this.board = new Board(8, this.players);
        this.nodeCount = 0;
        this.prunes = 0;
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

    //        for(Node n : successors) {
//            for (char x : n.getState().getArray())
//                System.out.print(x + " ");
//            System.out.println("value = "+n.getValue()+" i = "+n.getMoveIndex());
//        }

    private int maxValue(Node node, int a, int b) {
        if(playerWon(this.players[agentTurn]))
            return 1000000;

        if(node.getDepth() == this.searchDepth)
            return utility(node);

        int v = -10000000; // -infinity
        Node [] successors = node.getSuccessors();

        for(int i = 0; i < successors.length; i ++) {
            nodeCount++;
            int minValue = minValue(successors[i],a,b);
            if(minValue > v)
                v = minValue;
            if(v >= b){
                prunes++;
                return v;
            }
            if(v > a) {
                if(node.getDepth() == 0)
                    nextMoveIndex = successors[i].getMoveIndex();
                a = v;
            }
        }

        return v;
    }

    private int minValue(Node node, int a, int b) {
        if(playerWon(this.players[agentTurn]))
            return 1000000;

        if(node.getDepth() == this.searchDepth)
            return utility(node);

        int v = 10000000;
        Node [] successors = node.getSuccessors();

        for(int i = 0; i < successors.length; i ++) {
            nodeCount++;
            v = Math.min(v,maxValue(successors[i],a,b));
            if(v <= a) {
                prunes++;
                return v;
            }
            b = Math.min(b,v);
        }

        return v;
    }

    private int utility(Node node) {
        int value = node.getValue();
        value += inLineValue(node, 'X');
        value += opponentBlockValue(node, 'O');
        return value;
    }

    private int inLineValue(Node node, char playerCharacter) {
        State state = node.getState();
        int counter = 0;
        int twoInLineCounter = 0;
        int threeInLineCounter = 0;
        int value = 0;

        // check for horizontal win
        for(int i = 0; i < state.getArray().length; i++) {
            if(state.getArray()[i] == playerCharacter)
                counter++;
            else
                counter = 0;
            if(counter == 2)
                twoInLineCounter++;
            if(counter == 3)
                threeInLineCounter++;
            if(i%this.board.getSize() == this.board.getSize()-1)
                counter = 0;
        }

        // check for vertical win
        for(int i = 0; i < this.board.getSize(); i++) {
            for(int j = 0; j < this.board.getSize(); j++) {
                if(state.getArray()[i + j*8] == playerCharacter)
                    counter++;
                else
                    counter = 0;
                if(counter == 2)
                    twoInLineCounter++;
                if(counter == 3)
                    threeInLineCounter++;
            }
            counter = 0;
        }

        value += this.searchDepth*(10*twoInLineCounter + 25*threeInLineCounter);
        return value;

    }

    private int opponentBlockValue(Node node,char playerCharacter) {
        State state = node.getState();
        int counter = 0;
        int twoInLineCounter = 0;
        int threeInLineCounter = 0;
        int twoInLineBlocks = 0;
        int threeInLineBlocks = 0;
        int value = 0;


        // check for blocking: -  -  -  O  O  -  -  -

        // check for horizontal win
        for(int i = 0; i < state.getArray().length; i++) {
            if(state.getArray()[i] == playerCharacter)
                counter++;
            else if(state.getArray()[i] == 'X' && twoInLineCounter > 0) {
                twoInLineCounter = 0;
                counter = 0;
                twoInLineBlocks++;
            }
            else if(state.getArray()[i] == 'X' && threeInLineCounter > 0) {
                threeInLineCounter = 0;
                counter = 0;
                threeInLineBlocks++;
            }
            else
                counter = 0;
            if(counter == 2)
                twoInLineCounter++;
            if(counter == 3)
                threeInLineCounter++;
            if(i%this.board.getSize() == this.board.getSize()-1)
                counter = 0;
        }

        twoInLineCounter = 0;
        threeInLineCounter = 0;

        // check for vertical win
        for(int i = 0; i < this.board.getSize(); i++) {
            for(int j = 0; j < this.board.getSize(); j++) {
                if(state.getArray()[i + j*8] == playerCharacter)
                    counter++;
                else if(state.getArray()[i] == 'X' && twoInLineCounter > 0) {
                    twoInLineCounter = 0;
                    counter = 0;
                    twoInLineBlocks++;
                }
                else if(state.getArray()[i] == 'X' && threeInLineCounter > 0) {
                    threeInLineCounter = 0;
                    counter = 0;
                    threeInLineBlocks++;
                }
                else
                    counter = 0;
                if(counter == 2)
                    twoInLineCounter++;
                if(counter == 3)
                    threeInLineCounter++;
            }
            counter = 0;
        }
         value = this.searchDepth*(1000*twoInLineBlocks + 10000*threeInLineBlocks);
        return value;
    }

    private String alphaBetaSearch(State state, int depth) {
        this.searchDepth = depth;
        Node currentState = new Node(state,true,0);
        int v = maxValue(currentState,-1000000,1000000);
        String row = (char)(nextMoveIndex/8 + 'a') + "";
        String column = Integer.toString((nextMoveIndex%8)+1);
        nodeCount = 0;
        prunes = 0;
        nextMoveIndex = 0;

        System.out.println(v);

        return row+column;
    }

    public int agentTurn() {
        for(int i = 0; i < this.numOfPlayers; i ++)
            if(this.players[i].getId().equals("Agent"))
                return i;
        return -1;
    }

    public void playAgent() {
        agentTurn = agentTurn();
        this.board.print();

        while(this.winner == -1) {
            for(int i = 0; i < this.numOfPlayers; i++) {
                Player currentPlayer = this.players[i];
                String move;
                if(i != agentTurn)
                    move = askForNextMove(currentPlayer);
                else {
                    move = alphaBetaSearch(this.board.getState(), 5);
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
        FourInLine.playAgent();
    }
}
