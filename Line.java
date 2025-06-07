import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Line implements Comparable<Line> {
    int startX, startY, dirX, dirY;     // start cell, and direction
    int breakX, breakY;             // break cell
    int state;                      // state 0 = full, 1 = half block, 2 = full block
    int num;                        // int number of cell
    int player;                    // 1 = X, 2 = O
    int score;

    public Line(int startX, int startY, int dirX, int dirY, int breakX, int breakY, int state, int num, int player) {
        this.startX = startX;
        this.startY = startY;
        this.dirX = dirX;
        this.dirY = dirY;
        this.breakX = breakX;
        this.breakY = breakY;
        this.state = state;
        this.num = num;
        this.player = player;
        this.score = evaluate();
    }

    public Line(int startX, int startY, int dirX, int dirY, int state, int num, int player) {
        this.startX = startX;
        this.startY = startY;
        this.dirX = dirX;
        this.dirY = dirY;
        this.breakX = -1;
        this.breakY = -1;
        this.state = state;
        this.num = num;
        this.player = player;
        this.score = evaluate();
    }

    public List<int[]> getMove() {
        List<int[]> moves = new ArrayList<>() {
            @Override
            public String toString() {
                String str = "";
                for (int i = 0; i < this.size(); i++) {
                    str += this.get(i)[0] + " " + this.get(i)[1] + "\n";
                }
                return str;
            }
        };
        if (num >= 5) {
            if (breakX != -1 && breakY != -1) {
                moves.add(new int[]{breakX, breakY});
                return moves;
            }
            moves.add(new int[]{0, 0});
            return moves;
        }
        if (num == 4) {
            if (breakX != -1 && breakY != -1) {
                moves.add(new int[]{breakX, breakY});
                return moves;
            }
            if (state == 0) {
                moves.add(new int[]{startX - dirX, startY - dirY});
                moves.add(new int[]{startX + dirX * num, startY + dirY * num});
                return moves;
            }
            if (state == 1) {
                int xx = startX - dirX;
                int yy = startY - dirY;
                if (xx < 0 || yy < 0 || xx >= GameBoard.SIZE || yy >= GameBoard.SIZE
                        || GameBoard.Board[xx][yy] != GameBoard.EMPTY) {
                    moves.add(new int[]{startX + dirX * num, startY + dirY * num});
                    return moves;
                }
                else {
                    moves.add(new int[]{startX - dirX, startY - dirY});
                    return moves;
                }
            }
        }
        if (num == 3) {
            if (state == 0) {
                if (breakX != -1 && breakY != -1) {
                    moves.add(new int[]{breakX, breakY});
                    return moves;
                }
                moves.add(new int[]{startX - dirX, startY - dirY});
                moves.add(new int[]{startX + dirX * num, startY + dirY * num});
                return moves;
            }
            if (state == 1) {
                int xx = startX - dirX;
                int yy = startY - dirY;
                if (xx < 0 || yy < 0 || xx >= GameBoard.SIZE || yy >= GameBoard.SIZE
                        || GameBoard.Board[xx][yy] != GameBoard.EMPTY) {
                    if (breakX != -1 && breakY != -1) {
                        moves.add(new int[]{breakX, breakY});
                        moves.add(new int[]{startX + dirX * (num + 1), startY + dirY * (num + 1)});
                    }
                    else {
                        moves.add(new int[]{startX + dirX * num, startY + dirY * num});
                    }
                    return moves;
                }
                else {
                    if (breakX != -1 && breakY != -1) {
                        moves.add(new int[]{breakX, breakY});
                    }
                    moves.add(new int[]{startX - dirX, startY - dirY});
                    return moves;
                }
            }
        }
        if (num == 2) {
            if (state == 0) {
                if (breakX != -1 && breakY != -1) {
                    moves.add(new int[]{breakX, breakY});
                    moves.add(new int[]{startX + dirX * (num + 1), startY + dirY * (num + 1)});
                }
                else {
                    moves.add(new int[]{startX + dirX * num, startY + dirY * num});
                }
                moves.add(new int[]{startX - dirX, startY - dirY});
                return moves;
            }
            if (state == 1) {
                int xx = startX - dirX;
                int yy = startY - dirY;
                if (xx < 0 || yy < 0 || xx >= GameBoard.SIZE || yy >= GameBoard.SIZE
                        || GameBoard.Board[xx][yy] != GameBoard.EMPTY) {
                    if (breakX != -1 && breakY != -1) {
                        moves.add(new int[]{breakX, breakY});
                        moves.add(new int[]{startX + dirX * (num + 1), startY + dirY * (num + 1)});
                    }
                    else {
                        moves.add(new int[]{startX + dirX * num, startY + dirY * num});
                    }
                }
                else {
                    if (breakX != -1 && breakY != -1) {
                        moves.add(new int[]{breakX, breakY});
                    }
                    moves.add(new int[]{startX - dirX, startY - dirY});
                }
                return moves;
            }
        }
        if (num == 1) {
            if (state == 0) {
                moves.add(new int[]{startX - dirX, startY - dirY});
                moves.add(new int[]{startX + dirX * num, startY + dirY * num});
                return moves;
            }
            if (state == 1) {
                int xx = startX - dirX;
                int yy = startY - dirY;
                if (xx < 0 || yy < 0 || xx >= GameBoard.SIZE || yy >= GameBoard.SIZE
                        || GameBoard.Board[xx][yy] != GameBoard.EMPTY) {
                    moves.add(new int[]{startX + dirX * num, startY + dirY * num});
                    return moves;
                }
                else {
                    moves.add(new int[]{startX - dirX, startY - dirY});
                    return moves;
                }
            }
        }
        return moves;
    }

    public int evaluate() {
        int c = this.breakX == -1 ? 0 : 1;
        if (this.state == 2) {
            if (c + this.num < 5)
                return 25 - this.num;
            return c;
        }
        return Math.max(c, 5 - this.num);
    }



    // compare(x,y) : x < y thi x duoc uu tien, con khong thi y duoc uu tien

    @Override
    public int compareTo(Line other) {
        if (this.player == other.player) {
            if (this.score != other.score) return Integer.compare(this.score, other.score);

            if (this.state != other.state) return Integer.compare(this.state, other.state);

            boolean thisBreak = (this.breakX != -1);
            boolean otherBreak = (other.breakX != -1);
            if (thisBreak != otherBreak) return thisBreak ? 1 : -1;

            int cmp;
            if ((cmp = Integer.compare(other.num, this.num)) != 0) return cmp;
            if ((cmp = Integer.compare(this.startX, other.startX)) != 0) return cmp;
            if ((cmp = Integer.compare(this.startY, other.startY)) != 0) return cmp;
            if ((cmp = Integer.compare(this.dirX, other.dirX)) != 0) return cmp;
            if ((cmp = Integer.compare(this.dirY, other.dirY)) != 0) return cmp;
            if ((cmp = Integer.compare(this.breakX, other.breakX)) != 0) return cmp;
            if ((cmp = Integer.compare(this.breakY, other.breakY)) != 0) return cmp;

            return 0;
        }
        else {
            boolean isThisTurn = (this.player == 2 && GameBoard.isAITurn)
                    || (this.player == 1 && !GameBoard.isAITurn);

            boolean isOtherTurn = (other.player == 2 && GameBoard.isAITurn)
                    || (other.player == 1 && !GameBoard.isAITurn);

            if (isThisTurn && !isOtherTurn) {
                if (this.score <= other.score) return -1;
                return 1;

            } else if (!isThisTurn && isOtherTurn) {
                if (other.score <= this.score) return 1;
                return -1;
            }
            return 0;
        }
    }



    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Line)) return false;
        Line line = (Line) obj;
        return this.startX == line.startX && this.startY == line.startY && this.dirX == line.dirX
                && this.dirY == line.dirY && this.breakX == line.breakX && this.breakY == line.breakY
                && this.state == line.state && this.num == line.num && this.player == line.player;
    }



    @Override
    public int hashCode() {
        return Objects.hash(startX, startY, dirX, dirY, breakX, breakY, state, num, player);
    }



    @Override
    public String toString() {
        return "Player " + player + " Line " + num + " State " + state
                + " BreakXY " + breakX + " " + breakY + " StartXY "
                + startX + " " + startY + " DirXY " + + dirX + " " + dirY;
    }

}
