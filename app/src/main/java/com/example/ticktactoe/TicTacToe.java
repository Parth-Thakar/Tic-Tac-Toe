package com.example.ticktactoe;

import static android.provider.Settings.System.getString;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.util.Pair;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;

import java.lang.annotation.Retention;
import java.util.Random;

public class TicTacToe {
    //Logic class for the TicTacToe game
    // IN JAVA

    static final int BOARD_ROW = 3;
    static final int BOARD_COLUMN = 3;
    private int board[][];

    // BOARD PLAYER FOR FIRST TIME IT WILL BE X
    private BoardPlayer playerToMove = BoardPlayer.PLAYER_X; // stores whose turn it is

    @Nullable
    private TicTacToeListener ticTacToeListener;
    private int numberOfMoves = 0;
    // INITIALIZING THE GAME UNDER THE CONSTRUCTOR
    public TicTacToe() {
        initGame();
    }
    // SETTING-UP THE LISTNER
    public void setTicTacToeListener(@Nullable TicTacToeListener ticTacToeListener) {
        this.ticTacToeListener = ticTacToeListener;
    }
    // CHECKING THE VALID MOVE OR NOT
    public boolean isValidMove(int x, int y) {
        return board[x][y] == BoardState.SPACE;
    }
    // MOVE AT PARTICULAR INDEX ROW AND COL (ROW SHOULD BE 0-2) && (COL SHOULD ALSO BE IN 0-2)
    public boolean moveAt(@IntRange(from = 0, to = 2) int x, @IntRange(from = 0, to = 2) int y) {
        if (x < 0 || x > BOARD_ROW - 1 || y < 0 || y > BOARD_COLUMN - 1) {
            throw new IllegalArgumentException(String.format(getString(R.string.exception_string), x, y);
        }
        if (!isValidMove(x, y)) {
            return false;
        }
        numberOfMoves++;
        if (ticTacToeListener != null) {
            ticTacToeListener.movedAt(x, y, playerToMove.move);
        }
        board[x][y] = playerToMove.move;
        Pair<Boolean, SquareCoordinates[]> won = hasWon(x, y, playerToMove);
        if (won.first && ticTacToeListener != null) {
            ticTacToeListener.gameWonBy(playerToMove, won.second);
        } else if (numberOfMoves == BOARD_COLUMN * BOARD_ROW && ticTacToeListener != null) {
            ticTacToeListener.gameEndsWithATie();
        }
        // CHANGING THE NEXT PLAYER TO 0 EVERYTIME ONE MOVE IS DONE BY X
        changeTurnToNextPlayer();
        return true;
    }

    

    // LOGIC TO CHECK HAS WON OR NOT THAT WILL CHECK ROW WISE COLUMN WISE AND DIAGONAL WISE SAME VALUES
    private Pair<Boolean, SquareCoordinates[]> hasWon(int x, int y, BoardPlayer playerToMove) {
        SquareCoordinates[] winCoordinates = new SquareCoordinates[3];
        boolean hasWon = checkRow(x, y, playerToMove.move, winCoordinates)
                || checkColumn(x, y, playerToMove.move, winCoordinates)
                || checkDiagonals(x, y, playerToMove.move, winCoordinates);
        return Pair.create(hasWon, winCoordinates);
    }
    // LOGIC TO CHECK THE DIAGONALS IN BOARD FOR THE SAME VALUE
    private boolean checkDiagonals(int x, int y, int move, SquareCoordinates[] winCoordinates) {
        if ((board[0][0] == move && board[1][1] == move && board[2][2] == move)) {
            winCoordinates[0] = new SquareCoordinates(0, 0);
            winCoordinates[1] = new SquareCoordinates(1, 1);
            winCoordinates[2] = new SquareCoordinates(2, 2);
            return true;
        } else if ((board[0][2] == move && board[1][1] == move && board[2][0] == move)) {
            winCoordinates[0] = new SquareCoordinates(0, 2);
            winCoordinates[1] = new SquareCoordinates(1, 1);
            winCoordinates[2] = new SquareCoordinates(2, 0);
            return true;
        }
        return false;
    }
    // CHECKING THE COLUMN FOR THE SAME VALUE
    private boolean checkColumn(int x, int y, int movetoCheck, SquareCoordinates[] winCoordinates) {
        for (int i = 0; i < BOARD_ROW; i++) {
            if (board[i][y] != movetoCheck) {
                return false;
            }
        }
        for (int i = 0; i < winCoordinates.length; i++) {
            winCoordinates[i] = new SquareCoordinates(i, y);
        }
        return true;
    }
    // CHECKING THE ROW FOR THE SAME VALUE
    private boolean checkRow(int x, int y, int movetoCheck, SquareCoordinates[] winCoordinates) {
        for (int i = 0; i < BOARD_ROW; i++) {
            if (board[x][i] != movetoCheck) {
                return false;
            }
        }
        for (int i = 0; i < winCoordinates.length; i++) {
            winCoordinates[i] = new SquareCoordinates(x, i);
        }
        return true;
    }
    // CHANGING TO NEXT VALUE IF THE NEXT PLAYER IS "O" THEN COUMPUTERPLAY() METHOD WILL CALL
    private void changeTurnToNextPlayer() {
        if (playerToMove.equals(BoardPlayer.PLAYER_X)) {
            playerToMove = BoardPlayer.PLAYER_O;
            computerPlay();
        } else {
            playerToMove = BoardPlayer.PLAYER_X;
        }
    }
    // COMPUTERPLAY METHOD FOR MOVING THE RANDOM MOVE ON BOARD
    private void computerPlay() {
        int remainingBlocks = 9;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i][j] != '\u0000') {
                    remainingBlocks--;
                }
            }
        }
        if (remainingBlocks > 0) {
            Random random = new Random();
            int row, col;
            do {
                row = random.nextInt(3);
                col = random.nextInt(3);
            } while (board[row][col] != '\u0000');


            moveAt(row, col);

        }
    }

    public BoardPlayer getPlayerToMove() {
        return playerToMove;
    }
    // INITGAME TO INTIALIZE THE BOARD WITH BOARD ROW AND COLUMN
    private void initGame() {
        board = new int[BOARD_ROW][BOARD_COLUMN];
        playerToMove = BoardPlayer.PLAYER_X;
        numberOfMoves = 0;
    }
    // ON RESET CALIING THE INITGAME() METHOD AGAIN TO RESET ALL THE PLAYED MOVES
    public void resetGame() {
        initGame();
    }

    @BoardState
    public int getMoveAt(int x, int y) {
        if (board[x][y] == BoardState.SPACE) {
            return BoardState.SPACE;
        } else if (board[x][y] == BoardState.MOVE_O) {
            return BoardState.MOVE_O;
        } else {
            return BoardState.MOVE_X;
        }
    }

    @Retention(SOURCE)
    @IntDef({BoardState.SPACE, BoardState.MOVE_X, BoardState.MOVE_O})
    public @interface BoardState {
        int SPACE = 0;
        int MOVE_X = 1;
        int MOVE_O = 2;
    }
    // BOARDPLAYER ENUM FOR THE PLAYER X AND PLAYER O
    public enum BoardPlayer {
        PLAYER_X(BoardState.MOVE_X), PLAYER_O(BoardState.MOVE_O);
        public int move = BoardState.SPACE;

        BoardPlayer(int move) {
            this.move = move;
        }
    }
    // INTERFACE FOR THE TicTacToeListener
    public interface TicTacToeListener {
        void gameWonBy(BoardPlayer boardPlayer, SquareCoordinates winPoints[]);

        void gameEndsWithATie();

        void movedAt(int x, int y, int move);
    }
    // SQUARE CORDINATES FOR THE CUSTOM VIEWS CORDINATION
    // todo use this for passing coordinates
    public static final class SquareCoordinates {
        public final int i; // holds the row index of a Square on Board
        public final int j; // holds the column index of a Square on Board

        public SquareCoordinates(int i, int j) {
            this.i = i;
            this.j = j;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SquareCoordinates that = (SquareCoordinates) o;

            if (i != that.i) {
                return false;
            }
            return j == that.j;
        }

        @Override
        public int hashCode() {
            int result = i;
            result = 31 * result + j;
            return result;
        }
    }


}
