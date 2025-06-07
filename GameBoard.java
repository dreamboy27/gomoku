import java.util.*;

public class GameBoard {
    static final int SIZE = 15;
    static final byte EMPTY = 0;
    static final byte PLAYER_X = 1;
    static final byte PLAYER_O = 2;
    static byte[][] Board = new byte[SIZE][SIZE];
    static boolean isAITurn = false;        // true = AI turn, false = human turn
    static int num = 0;
    static Deque<int[]> moveStack = new ArrayDeque<>();

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static byte getCell(int row, int col) {
        return Board[row][col];
    }

    public static void makeMove(int x, int y, byte player) {
        tempLineXSet = new HashSet<>();
        tempLineOSet = new HashSet<>();
        evaluateLine(x, 0, 0, 1);
        evaluateLine(0, y, 1, 0);
        evaluateLine(Math.max(x - y, 0), Math.max(0, y - x), 1, 1);
        evaluateLine(Math.max(0, x + y - SIZE + 1), Math.min(SIZE - 1, x + y), 1, -1);
        subtractLineBeforeMove();

        Board[x][y] = player;
        moveStack.push(new int[]{x, y, player});
        isAITurn ^= true;

        tempLineXSet = new HashSet<>();
        tempLineOSet = new HashSet<>();
        evaluateLine(x, 0, 0, 1);
        evaluateLine(0, y, 1, 0);
        evaluateLine(Math.max(x - y, 0), Math.max(0, y - x), 1, 1);
        evaluateLine(Math.max(0, x + y - SIZE + 1), Math.min(SIZE - 1, x + y), 1, -1);
        addLineAfterMove();

        generateMoves();
        num++;
    }

    public static void undoMove(int x, int y) {
        tempLineXSet = new HashSet<>();
        tempLineOSet = new HashSet<>();
        evaluateLine(x, 0, 0, 1);
        evaluateLine(0, y, 1, 0);
        evaluateLine(Math.max(x - y, 0), Math.max(0, y - x), 1, 1);
        evaluateLine(Math.max(0, x + y - SIZE + 1), Math.min(SIZE - 1, y + x), 1, -1);
        subtractLineBeforeMove();

        Board[x][y] = EMPTY;
        moveStack.pop();
        isAITurn ^= true;

        tempLineXSet = new HashSet<>();
        tempLineOSet = new HashSet<>();
        evaluateLine(x, 0, 0, 1);
        evaluateLine(0, y, 1, 0);
        evaluateLine(Math.max(x - y, 0), Math.max(0, y - x), 1, 1);
        evaluateLine(Math.max(0, x + y - SIZE + 1), Math.min(SIZE - 1, y + x), 1, -1);
        addLineAfterMove();

        generateMoves();
        num--;
    }

    public static void printBoard() {
        System.out.print("    ");
        for (int col = 0; col < SIZE; col++) {
            char letter = (char) ('A' + col);
            System.out.print("  " + letter + " ");
        }
        System.out.println();

        for (int row = 0; row < SIZE; row++) {
            System.out.printf("%2d  ", row + 1);
            for (int col = 0; col < SIZE; col++) {
                String cell = switch (Board[row][col]) {
                    case PLAYER_X -> "\u001B[31m" + "_X_" + "\u001B[0m"; // PLAYER_X
                    case PLAYER_O -> "\u001B[32m" + "_O_" + "\u001B[0m"; // PLAYER_O
                    default -> "___"; // EMPTY
                };
                System.out.print("|" + cell);
            }
            System.out.println("|");
        }
    }





    static Set<Line> lineXSet = new TreeSet<>();//
    static Set<Line> lineOSet = new TreeSet<>();//

    static List<Line> sortedLines = new ArrayList<>();
    static HashSet<Line> tempLineXSet = new HashSet<>();
    static HashSet<Line> tempLineOSet = new HashSet<>();


    static int evaluate() {
        int eval = 0;
        Iterator<Line> itX = lineXSet.iterator();
        Iterator<Line> itO = lineOSet.iterator();

        Line currentX = itX.hasNext() ? itX.next() : null;
        Line currentO = itO.hasNext() ? itO.next() : null;

        while (currentX != null || currentO != null) {
            if (currentX != null && (currentO == null || currentX.compareTo(currentO) < 0)) {
                // currentX tốt hơn currentO
                if (currentX.score <= 0) return (int) -2e9;
                if (currentX.score == 1) return (int) -2e8;
                if (currentX.score == 2 && currentX.state == 0) return (int) -5e7;
                if (currentX.score == 2 && currentX.state == 1) { eval -= (int)2e6; }
                if (currentX.score == 3 && currentX.state == 0) { eval -= (int)2e5; }
                if (currentX.score == 3 && currentX.state == 1) { eval -= (int)5e4; }
                if (currentX.score == 4 && currentX.state == 0) { eval -= (int)5e3; }
                if (currentX.score == 4 && currentX.state == 1) { eval -= (int)1e3; }

                currentX = itX.hasNext() ? itX.next() : null;
            } else if (currentO != null) {
                // currentO tốt hơn hoặc currentX == null
                if (currentO.score <= 0) return (int)2e9;
                if (currentO.score == 1) return (int)2e8;
                if (currentO.score == 2 && currentO.state == 0) return (int)5e7;
                if (currentO.score == 2 && currentO.state == 1) { eval += (int)2e6; }
                if (currentO.score == 3 && currentO.state == 0) { eval += (int)2e5; }
                if (currentO.score == 3 && currentO.state == 1) { eval += (int)5e4; }
                if (currentO.score == 4 && currentO.state == 0) { eval += (int)5e3; }
                if (currentO.score == 4 && currentO.state == 1) { eval += (int)1e3; }

                currentO = itO.hasNext() ? itO.next() : null;
            }
        }
        return eval;
    }

    static void addLineAfterMove() {
        lineXSet.addAll(tempLineXSet);
        lineOSet.addAll(tempLineOSet);
    }

    static void subtractLineBeforeMove() {
        lineXSet.removeAll(tempLineXSet);
        lineOSet.removeAll(tempLineOSet);
    }

    static List<int[]> moves = new ArrayList<>();
    static void generateMoves() {
        moves = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        Iterator<Line> itX = lineXSet.iterator();
        Iterator<Line> itO = lineOSet.iterator();

        Line currentX = itX.hasNext() ? itX.next() : null;
        Line currentO = itO.hasNext() ? itO.next() : null;

        while (currentX != null || currentO != null) {
            if (currentX != null && (currentO == null || currentX.compareTo(currentO) < 0)) {
                // currentX tốt hơn currentO
                for (int[] move : currentX.getMove()) {
                    String key = move[0] + "," + move[1];
                    if (seen.add(key)) {
                        moves.add(move);
                    }
                }

                currentX = itX.hasNext() ? itX.next() : null;
            } else if (currentO != null) {
                // currentO tốt hơn hoặc currentX == null
                for (int[] move : currentO.getMove()) {
                    String key = move[0] + "," + move[1];
                    if (seen.add(key)) {
                        moves.add(move);
                    }
                }

                currentO = itO.hasNext() ? itO.next() : null;
            }
        }
    }

    static List<int[]> getMoves() {
        return moves.subList(0, Math.min(moves.size(), 20));
    }

//    static void printLine() {
//        for (Line line : lineSet) {
//            System.out.println(line.toString());
//        }
//    }

//    static public int evaluate() {
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

    static int evaluateLine(int x, int y, int dx, int dy) {
        int result = 0;
        int count[] = new int[]{0, 0, 0};          // count[x] = number of cell type x
        byte player = 0;
        byte stackPlayer = 0;
        //
        List<int[]> stack = new ArrayList<>();
        int sx = x;
        int sy = y;

        while (x >= 0 && y >= 0 && x < GameBoard.SIZE && y < GameBoard.SIZE) {
            byte currentPlayer = GameBoard.Board[x][y];
            if (player == currentPlayer) {
                count[player]++;
            }
            else {
                stack.add(new int[] {count[player], sx, sy, dx, dy});
                if (stackPlayer == GameBoard.EMPTY || currentPlayer == stackPlayer || currentPlayer == GameBoard.EMPTY) {
                    sx = x;
                    sy = y;
                    count[player] = 0;
                    player = currentPlayer;
                    count[player]++;
                    if (stackPlayer == GameBoard.EMPTY) {
                        stackPlayer = currentPlayer;
                    }
                }
                else {
                    calculate(stack, stackPlayer);
                    count[stackPlayer] = 0;
                    stack = new ArrayList<>();
                    if (count[GameBoard.EMPTY] == 0) {
                        sx = x;
                        sy = y;
                    }
                    stack.add(new int[] {count[GameBoard.EMPTY], sx, sy, dx, dy});
                    sx = x;
                    sy = y;
                    stackPlayer = currentPlayer;
                    player = currentPlayer;
                    count[stackPlayer]++;
                    count[GameBoard.EMPTY] = 0;
                }
            }
            x += dx;
            y += dy;
        }

        stack.add(new int[] {count[player], sx, sy, dx, dy});
        calculate(stack, stackPlayer);

        return result;
    }


    //  mot stack voi index chan se la so empty cell, index le la so cell cua nguoi choi
    static void calculate(List<int[]> stack, byte player) {
        int maxCount = 0;
        int index = -1;
        for (int i = 0; i < stack.size(); i += 2) {
            if (stack.get(i)[0] == 1 && i > 0 && i < stack.size() - 1) {
                if (maxCount < stack.get(i + 1)[0] + stack.get(i - 1)[0] + 1) {
                    maxCount = stack.get(i + 1)[0] + stack.get(i - 1)[0] + 1;
                    index = i;
                }
            }
        }

        for (int i = 1; i < stack.size(); i+= 2) {
            int state = 0;
            if (i < index - 1 || i > index + 1) {           // no mix
                if (stack.get(i - 1)[0] > 0) {
                    if (stack.size() % 2 == 0) {
                        if (i == stack.size() - 1) {
                            state = 1;
                        }
                        else {
                            state = 0;
                        }
                    }
                    else {
                        state = 0;
                    }
                }
                else {
                    if (stack.size() % 2 == 0) {
                        if (i == stack.size() - 1) {
                            state = 2;
                        }
                        else {
                            state = 1;
                        }
                    }
                    else {
                        state = 1;
                    }
                }


                Line line = new Line(stack.get(i)[1], stack.get(i)[2], stack.get(i)[3], stack.get(i)[4],
                        state, stack.get(i)[0], player);
                if (player == 1) {
                    tempLineXSet.add(line);
                }
                if (player == 2) {
                    tempLineOSet.add(line);
                }
            }
            else {
                if (i == index - 1) {
                    if (stack.get(i - 1)[0] > 0) {
                        if (stack.size() % 2 == 0) {
                            if (i + 2 == stack.size() - 1) {
                                state = 1;
                            }
                            else {
                                state = 0;
                            }
                        }
                        else {
                            state = 0;
                        }
                    }
                    else {
                        if (stack.size() % 2 == 0) {
                            if (i + 2 == stack.size() - 1) {
                                state = 2;
                            }
                            else {
                                state = 1;
                            }
                        }
                        else {
                            state = 1;
                        }
                    }


                    Line line = new Line(stack.get(i)[1], stack.get(i)[2], stack.get(i)[3], stack.get(i)[4],
                            stack.get(index)[1], stack.get(index)[2],
                            state, stack.get(i)[0] + stack.get(i + 2)[0], player);
                    if (player == 1) {
                        tempLineXSet.add(line);
                    }
                    if (player == 2) {
                        tempLineOSet.add(line);
                    }
                }
            }
        }
    }
}
