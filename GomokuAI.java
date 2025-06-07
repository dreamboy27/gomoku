import java.io.StreamCorruptedException;
import java.sql.SQLSyntaxErrorException;
import java.util.*;

public class GomokuAI {
    static int depth = 4;



    // minimax function: isMax = true => AI đi, false => HUMAN đi
    static int[] minimax(int depth, boolean isMax, int alpha, int beta) {
        int score = GameBoard.evaluate();
        int[] lastMove = GameBoard.moveStack.getFirst();
        int rrow = lastMove[0];
        int rcol = lastMove[1];
        // Nếu đã có người thắng hoặc hết độ sâu thì trả điểm
        if (score >= (int)15e8) {
            return new int[]{rrow, rcol, score + depth * (int) 1e7};
        }
        if (score <= (int)-15e8) {
            return new int[]{rrow, rcol, score - depth * (int)1e7};
        }
        if (depth == 0) {
            return new int[]{-1, -1, score};
        }
        int value = 0;

        List<int[]> moves = GameBoard.getMoves();
        if (isMax) {
            value = Integer.MIN_VALUE;
            for (int[] move : moves) {
                int row = move[0];
                int col = move[1];
                GameBoard.makeMove(row, col, GameBoard.PLAYER_O);
                int eval = minimax(depth - 1, false, alpha, beta)[2];
                GameBoard.undoMove(row, col);
                if (value < eval) {
                    rrow = row; rcol = col; value = eval;
                }
                alpha = Math.max(alpha, value);
                if (value >= beta) return new int[]{rrow, rcol, value};
            }
        }
        else {
            value = Integer.MAX_VALUE;
            for (int[] move : moves) {
                int row = move[0];
                int col = move[1];
                GameBoard.makeMove(row, col, GameBoard.PLAYER_X);
                int eval = minimax(depth - 1, true, alpha, beta)[2];
                GameBoard.undoMove(row, col);
                if (value > eval) {
                    rrow = row; rcol = col; value = eval;
                }
                beta = Math.min(beta, value);
                if (value <= alpha) return new int[]{rrow, rcol, value};
            }
        }
        return new int[]{rrow, rcol, value};
    }

    public static void makeMove() {
        int score = GameBoard.evaluate();
        if (GameBoard.num < 7) {
            depth = 6;
        }
        else {
            depth = 8;
        }
        int[] moveValue;
        moveValue = minimax(depth, true, (int)-15e8, (int)15e8);
        if (score <= (int)-15e8) {
            GameBoard.clearScreen();
            System.out.println("GAME OVER! HUMAN WIN");
            Game.gameOver = true;
        }
        else {
            if (GameBoard.num == 225) {
                Game.gameOver = true;
                System.out.println("GAME OVER! TIE");
                return;
            }
            GameBoard.makeMove(moveValue[0], moveValue[1], GameBoard.PLAYER_O);
            int x = moveValue[0] + 1;
            char y = (char) (moveValue[1] + 'A');
            GameBoard.clearScreen();
            System.out.println("AI moved to: (" + x + ", " + y + ")");
            if (GameBoard.evaluate() >= (int)15e8) {
                System.out.println("GAME OVER! AI WIN");
                Game.gameOver = true;
            }
        }
    }

}
