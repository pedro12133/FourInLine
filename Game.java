import java.io.IOException;

public class Game {
    public static void main(String [] args) throws IOException {
        Player agent = new Player("Agent", 'X');
        Player opponent = new Player("Opponent", 'O');
        int boardSize = 8;
        Board board = new Board(boardSize);

        board.setPlayers(agent,opponent);
        System.out.println();
        board.printBoard();

        while(board.getWinner() == null && board.getMoveCount() < 32) {
            System.out.println();
            board.askForNextMove();
        }

        if(board.getWinner() != null)
            System.out.println(board.getWinner().getId()+ " wins.");
        else
            System.out.println("Tie.");
    }
}
