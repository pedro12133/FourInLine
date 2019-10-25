public class Player {
    private String id;
    private char moveChar;
    private int turn;

    public Player(String id, char moveChar) {
        this.id = id;
        this.moveChar = moveChar;
    }
    public void setPlayerTurn(int turn) { this.turn = turn; }
    public String getId() { return this.id; }
    public char getMoveChar() { return this.moveChar; }
    public int getTurn() { return this.turn; }

}
