import java.util.Scanner;

public class Game {
    static public Scanner scanner = new Scanner(System.in);
    static boolean gameOver = false;


    public static void showMenu() {
        System.out.println("Welcome to Gomoku game!");
    }

    public static void playGame() {
        showMenu();
        GameBoard.clearScreen();
        GameBoard.printBoard();
        while (!gameOver) {
            System.out.print("Người chơi hãy nhập nước đi (ví dụ H8): ");
            String move = scanner.nextLine().trim().toUpperCase();

            if (!isValidInput(move)) {
                System.out.println("❌ Định dạng không hợp lệ. Hãy nhập như A1, D10, G15.");
                continue;
            }
            int col = move.charAt(0) - 'A';
            int row;
            try {
                row = Integer.parseInt(move.substring(1)) - 1;
            } catch (NumberFormatException e) {
                System.out.println("❌ Sai số hàng.");
                continue;
            }
            if (!isValidMove(row, col)) {
                System.out.println("❌ Ô không hợp lệ hoặc đã được đánh.");
                continue;
            }

            GameBoard.makeMove(row, col, GameBoard.PLAYER_X);
            GomokuAI.makeMove();
            GameBoard.printBoard();
        }
    }

    static boolean isValidInput(String input) {
        return input.matches("^[A-Oa-o][1-9][0-5]?$");
    }

    static boolean isValidMove(int row, int col) {
        return row >= 0 && row < GameBoard.SIZE && col >= 0 && col < GameBoard.SIZE && GameBoard.getCell(row, col) == 0;
    }


}
