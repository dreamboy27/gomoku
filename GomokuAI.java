import java.io.StreamCorruptedException;
import java.sql.SQLSyntaxErrorException;
import java.util.*;

public class GomokuAI {
    static int depth = 4;
    static int length = 15;
/**
 *      need to fix:
 *      1. hien tai chi danh gia cac line 1 cach don le,
 *      nhung thuc te thi cac duong co the ket hop voi nhau tao thanh cac nuoc nguy hiem
 *      //
 *
 *      2. sap xep lai range danh gia, chi su dung cac nuoc di co diem danh gia cao xap xi nhau
 *
 *      3. khi tim nuoc di, neu xac dinh duoc nuoc di nguy hiem
 *      thi duyet nuoc di do luon, khong can thu cac nuoc di khac
 *
 *
 */

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
        if (GameBoard.num < 10) {
            depth = 6;
        }
        else {
            depth = 8;
        }
        int score = GameBoard.evaluate();
        int[] moveValue;
        moveValue = minimax(depth, true, (int)-15e8, (int)15e8);
        if (score <= (int)-15e8) {
            System.out.println("GAME OVER! HUMAN WIN");
            Game.gameOver = true;
        }
        else {
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


//
//    static List<int[]> generateMoves(boolean isMax) {
//        // Thay vì duyệt tất cả các nước đi, chỉ duyệt các nước đi có ý nghĩa
//        int range = 2;
//        List<int[]> moves = new ArrayList<>();
//        boolean[][] visited = new boolean[GameBoard.SIZE][GameBoard.SIZE];
//        for (int row = 0; row < GameBoard.SIZE; row++) {
//            for (int col = 0; col < GameBoard.SIZE; col++) {
//                if (GameBoard.Board[row][col] == GameBoard.EMPTY) {
//                    // Xét ô xung quanh trong phạm vi range
//                    int[] dx = {-1, -1,  0, 1, 1,  1,  0, -1};
//                    int[] dy = { 0,  1,  1, 1, 0, -1, -1, -1};
//                    for (int len = 1; len <= range; len++) {
//                        if (visited[row][col]) {
//                            continue;
//                        }
//                        for (int i = 0; i < 8; i++) {
//                            if (visited[row][col]) {
//                                continue;
//                            }
//                            int nx = row + dx[i] * len;
//                            int ny = col + dy[i] * len;
//
//                            if (nx >= 0 && ny >= 0 && nx < GameBoard.SIZE && ny < GameBoard.SIZE) {
//                                if (GameBoard.Board[nx][ny] != GameBoard.EMPTY) {
//                                    if (isMax) {
//                                        GameBoard.makeMove(row, col, GameBoard.PLAYER_O);
//                                    }
//                                    else {
//                                        GameBoard.makeMove(row, col, GameBoard.PLAYER_X);
//                                    }
//                                    int score = GameBoard.score;
//                                    score -= Math.abs(dx[i] * len) * 10 + Math.abs(dy[i] * len) * 10;
//                                    moves.add(new int[]{row, col, score});
//                                    GameBoard.undoMove(row, col);
//                                    visited[row][col] = true;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        if (isMax) {
//            moves.sort((a, b) -> Integer.compare(b[2], a[2]));
//        }
//        else {
//            moves.sort((a, b) -> Integer.compare(a[2], b[2]));
//        }
//        if (depth == 4) {
//            length = 20;
//        }
//        if (depth == 6) {
//            length = 15;
//        }
//        if (depth == 7) {
//            length = 12;
//        }
//        return moves.subList(0, Math.min(moves.size(), length));
//    }



    // Hàm heuristic đánh giá điểm board với góc nhìn AI
//    static int evaluate() {
//        int score = 0;
//        for (int i = 0; i < GameBoard.SIZE; i++) {
//            score += evaluateLine(i, 0, 0, 1);  // ngang
//            score += evaluateLine(0, i, 1, 0);  // dọc
//        }
//        for (int i = 0; i < GameBoard.SIZE; i++) {
//            score += evaluateLine(i, 0, 1, 1);  // \\ cheo chinh
//            score += evaluateLine(0, i+1, 1, 1);  //
//        }
//        for (int i = 0; i < GameBoard.SIZE; i++) {
//            score += evaluateLine(0, i, 1, -1); // //
//            score += evaluateLine(i+1, GameBoard.SIZE - 1, 1, -1); // //
//        }
//        return score;
//    }

//    static int evaluateLine(int x, int y, int dx, int dy) {
//        int result = 0;
//        int count = 0;
//        int ecount = 0; // empty cell count, used for calculating broke line
//        int state = 0; // 0 = open, 1 = block, 2 = dead
//        byte player = 0;
//        while (x >= 0 && y >= 0 && x < GameBoard.SIZE && y < GameBoard.SIZE) {
//            byte nplayer = GameBoard.Board[x][y];
//            if (player == nplayer) {
//                    count++;
//            } else {
//                if (nplayer == GameBoard.EMPTY) {
//                    if (ecount >= 1) {
//                        result += getScoreByCount(count, state, player, 0);
//                        state = (player == 0) ? 0 : 1;
//                        player = nplayer;
//                        count = 1;
//                        ecount = 0;
//                    }
//                    else {
//                        ecount++;
//                    }
//                }
//                else {
//                    state += (ecount == 0) ? 1 : 0;
//                    result += getScoreByCount(count, state, player, ecount);
//                    if (ecount == 0) {
//                        state = (player == 0) ? 0 : 1;
//                    }
//                    else {
//                        state = 0;
//                    }
//                    player = nplayer;
//                    count = 1;
//                    ecount = 0;
//                }
//            }
//            x += dx;
//            y += dy;
//        }
//        result += getScoreByCount(count, state, player, ecount);
//        return result;
//    }

    // Trả điểm cho chuỗi tùy theo độ dài và số bị chặn

//    static int getScoreByCount(int count, int state, byte player, int ecount) {
//        boolean isAITurn = GameBoard.isAITurn;
//        if (isAITurn) {                 // ai turn
//            if (player == 1) {          // human
//                if (count >= 5) {
//                    if (ecount > 0)
//                        return -20000000;
//                    return -500000000;
//                }
//                else if (count == 4) {
//                    if (state == 0) return -20000000;
//                    else if (state == 1) return -20000000;
//                    else if (state == 2) return -20000;
//                }
//                else if (count == 3) {
//                    if (state == 0) return -5000000;
//                    else if (state == 1) return -100000;
//                    else if (state == 2) return -1000;
//                }
//                else if (count == 2) {
//                    if (state == 0) return -20000;
//                    else if (state == 1) return -10000;
//                    else if (state == 2) return -100;
//                }
//                else if (count == 1) return -30;
//            }
//            else if (player == 2) {
//                if (count >= 5) {
//                    if (ecount > 0)
//                        return 150000000;
//                    return 500000000;
//                }
//                else if (count == 4) {
//                    if (state == 0) return 150000000;
//                    else if (state == 1) return 150000000;
//                    else if (state == 2) return 20000;
//                }
//                else if (count == 3) {
//                    if (state == 0) return 100000000;
//                    else if (state == 1) return 200000;
//                    else if (state == 2) return 1000;
//                }
//                else if (count == 2) {
//                    if (state == 0) return 50000;
//                    else if (state == 1) return 15000;
//                    else if (state == 2) return 100;
//                }
//                else if (count == 1) return 30;
//            }
//        }
//        if (!isAITurn) {                // human turn
//            if (player == 1) {          // human
//                if (count >= 5) {
//                    if (ecount > 0)
//                        return -150000000;
//                    return -500000000;
//                }
//                else if (count == 4) {
//                    if (state == 0) return -150000000;
//                    else if (state == 1) return -150000000;
//                    else if (state == 2) return -20000;
//                }
//                else if (count == 3) {
//                    if (state == 0) return -100000000;
//                    else if (state == 1) return -200000;
//                    else if (state == 2) return -1000;
//                }
//                else if (count == 2) {
//                    if (state == 0) return -50000;
//                    else if (state == 1) return -15000;
//                    else if (state == 2) return -100;
//                }
//                else if (count == 1) return -30;
//            }
//            else if (player == 2) {
//                if (count >= 5) {
//                    if (ecount > 0)
//                        return 20000000;
//                    return 500000000;
//                }
//                else if (count == 4) {
//                    if (state == 0) return 20000000;
//                    else if (state == 1) return 20000000;
//                    else if (state == 2) return 20000;
//                }
//                else if (count == 3) {
//                    if (state == 0) return 5000000;
//                    else if (state == 1) return 100000;
//                    else if (state == 2) return 1000;
//                }
//                else if (count == 2) {
//                    if (state == 0) return 20000;
//                    else if (state == 1) return 10000;
//                    else if (state == 2) return 1000;
//                }
//                else if (count == 1) return -30;
//            }
//        }
//        return 0;
//    }


}
