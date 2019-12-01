public class Player {
    private String id;
    private char character;
    private int turn;

    public Player() {
        this.id = null;
        this.character = '\u0000';
        this.turn = -1;
    }
    public Player(String id, char playerCharacter) {
        this.id = id;
        this.character = playerCharacter;
    }

    public void setId(String id) { this.id = id; }
    public void setCharacter(char character) { this.character = character;}
    public void setTurn(int turn) { this.turn = turn; }

    public String getId() { return this.id; }
    public char getCharacter() { return this.character; }
    public int getTurn() { return this.turn; }
}
