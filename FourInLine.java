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
    private int agentTurn;

    private int searchDepth;
    private int nodeCount;
    private int prunes;
    private int agentMove;

    public FourInLine() {
        this.numOfPlayers = 2;
        this.connectionsToWin = 4;
        this.players = new Player[2];
        this.moveCount = 0;
        this.winner = -1;
        //setMaxTime();
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

    private int maxValue(Node node, int a, int b) {
        if(playerWon(this.players[agentTurn]))
            return 1000000;

        if(node.getDepth() == this.searchDepth)
            return utility(node);

        int v = -10000000; // -infinity

        Node currentNode = node.nextSuccessor();
        while(currentNode != null) {

            nodeCount++;
            int minValue = minValue(currentNode,a,b);
            if(node.getDepth() == 0) {
                System.out.print((char) (currentNode.getMoveIndex() / 8 + 'a') + "" + ((currentNode.getMoveIndex() % 8) + 1) + "-" + minValue + " ");
                if(((currentNode.getMoveIndex() % 8) + 1) == 8)
                    System.out.println();
            }
            if(minValue > v)
                v = minValue;
            if(v >= b){
                prunes++;
                return v;
            }
            if(v > a) {
                if(node.getDepth() == 0) {
                    agentMove = currentNode.getMoveIndex();
                }
                a = v;
            }
            currentNode = node.nextSuccessor();
        }

        return v;
    }

    private int minValue(Node node, int a, int b) {
        if(playerWon(this.players[agentTurn]))
            return 1000000;

        if(node.getDepth() == this.searchDepth)
            return utility(node);

        int v = 10000000;

        Node currentNode = node.nextSuccessor();
        while(currentNode != null) {

            nodeCount++;
            v = Math.min(v,maxValue(currentNode,a,b));
            if(v <= a) {
                prunes++;
                return v;
            }
            b = Math.min(b,v);
            currentNode = node.nextSuccessor();
        }
        return v;
    }

    private int utility(Node node) {
        int value = 0;
        value += cellValue(node);
        value += killerMoveValue(node);
        return value;
    }

    private int killerMoveValue(Node node) {
        State state = node.getState();
        String setUp0 = "-XX--";
        String setUp1 = "--XX-";
        String killer0 = "-XXX-";
        String kill = "XXXX";
        String row;
        String column;

        for(int i = 0; i < 8; i++) {
            row = ""+state.getValueAt((8*i))+state.getValueAt((8*i)+1)+state.getValueAt((8*i)+2)+state.getValueAt((8*i)+3);
            row += ""+state.getValueAt((8*i)+4)+state.getValueAt((8*i)+5)+state.getValueAt((8*i)+6)+state.getValueAt((8*i)+7);
            column = ""+state.getValueAt(i)+state.getValueAt(8+i)+state.getValueAt(16+i)+state.getValueAt(24+i);
            column += ""+state.getValueAt(32+i)+state.getValueAt(40+i)+state.getValueAt(48+i)+state.getValueAt(56+i);

            if(row.contains(kill) || column.contains(kill))
                return this.searchDepth*1000;
            if(row.contains(killer0) || column.contains(killer0))
                return this.searchDepth*100;
            if(row.contains(setUp0) || column.contains(setUp0))
                return this.searchDepth*10;
            if(row.contains(setUp1) || column.contains(setUp1))
                return this.searchDepth*10;

        }
        return 0;
    }

    // Heuristics
    private int cellValue(Node node) {
        int [] cellValues = {
                1,2,3,4,4,3,2,1,
                2,3,4,5,5,4,3,2,
                3,4,5,6,6,5,4,3,
                4,5,6,7,7,6,5,4,
                4,5,6,7,7,6,5,4,
                3,4,5,6,6,5,4,3,
                2,3,4,5,5,4,3,2,
                1,2,3,4,4,3,2,1
        };
        int value = 0;

        for(int i = 0; i < node.getState().size(); i++)
            if(node.getState().getValueAt(i) == 'X')
                value += cellValues[i];
        return value;
    }

    private String alphaBetaSearch(State state, int depth) {
        if(this.moveCount < 2) {
            int [] bestFirstMove = {27,28,35,36};
            agentMove = bestFirstMove[new Random().nextInt(4)];
            while(!validCell((char)(agentMove /8 + 'a') + ""+((agentMove %8)+1)))
                agentMove = bestFirstMove[new Random().nextInt(4)];
            System.out.println(agentMove);
        }
        else {
            this.searchDepth = depth;
            Node currentState = new Node(state,true,0);
            int v = maxValue(currentState,-1000000,1000000);
            System.out.println("\nBest: "+v+" nodes: "+nodeCount+" prunes: "+prunes);
        }

        String row = (char)(agentMove /8 + 'a') + "";
        String column = Integer.toString((agentMove %8)+1);

        nodeCount = 0;
        prunes = 0;
        agentMove = 0;

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
                System.out.println("move count: " +moveCount);
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
        State state = this.board.getState();
        int count = 0;

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



        /*State state = new State(64);
        for(int i = 0; i < state.size(); i++)
            state.setValueAt('-',i);

        state.setValueAt('X',1);
        state.setValueAt('X',2);
        state.setValueAt('X',10);
        state.setValueAt('X',11);
        state.setValueAt('X',19);
        state.setValueAt('X',20);
        state.setValueAt('X',28);
        state.setValueAt('X',29);
        state.setValueAt('X',37);
        state.setValueAt('X',38);*/


    }
}
