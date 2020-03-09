package model;

import java.util.Observable;

/**
 * Board class waarin het bord wordt gemodelleerd.
 * @author Martijn Gemmink en Bas Hendrikse
 * @version 1.1
 */

public class Board extends Observable {

    // Constants
    public static final int WIDTH = 7;
    public static final int HEIGTH = 6;
    private static final String[] NUMBERING = {" 0  | 1  | 2  | 3  | 4  | 5  | 6  ", 
      "---+---+---+---+---+---+---", " 7  | 8  | 9 | 10 | 11 | 12 | 13 ", "---+---+---+---+---+---+---",
      " 14 | 15 | 16 | 17 | 18 | 19 | 20 ", "---+---+---+---+---+---+---" ,
      " 21 | 22 | 23 | 24 | 25 | 26 | 27 ", "---+---+---+---+---+---+---",
      " 28 | 29 | 30 | 31 | 32 | 33 | 34 ", "---+---+---+---+---+---+---", " 35 | 36 | 37 | 38 | 39 | 40 | 41 "};
    private static final String LINE = NUMBERING[1];
    private static final String DELIM = "     ";

    // -- Instance variables -----------------------------------------

    /*@
         private invariant fields.length == HEIGTH*WIDTH;
         invariant (\forall int i; 0 <= i & i < HEIGTH*WIDTH;
             getField(i) == Mark.EMPTY || getField(i) == Mark.RED || getField(i) == Mark.YELLOW);
     */
    /**
     * De hoogte en breedte van het bord van Vier op een Rij.
     */
    private Mark[] fields;

    // -- Constructors -----------------------------------------------

    /*@
         ensures (\forall int i; 0 <= i & i < HEIGTH*WIDTH; this.getField(i) == Mark.EMPTY);
     */
    /**
     * Initialiseerd een bord met grootte (HEIGTH*WIDTH) en zet vervolgens alle velden op Mark.EMPTY.
     */
    public Board() {
        fields = new Mark[HEIGTH * WIDTH];
        for (int i = 0; i < HEIGTH * WIDTH; i++) {
            forceSetField(i, Mark.EMPTY);
        }
    }

    // -- Queries ----------------------------------------------------

    /**
     * Geeft het laagste lege veld boven van een bepaalde kolom.
     */    
    /*@requires col < WIDTH; */
    public int getHighestField(int col) {
      assert col < WIDTH;
        int result = -1;
        for (int i = col + (HEIGTH - 1) * WIDTH; i >= 0; i = i - WIDTH) {
            if (getField(i) == Mark.EMPTY) {
                result = i;
                break;
            }
        }    	
        return result;
    }

    /**
     * Gives the column of a certain index.
     * @param index
     * @return int column
     */
    /*	@requires index < WIDTH * HEIGTH && index >= 0;
       	@ensures \result == index % WIDTH;
     */
    public static int indexToCol(int index) throws IllegalArgumentException {
        if (index < 0 || index > WIDTH * HEIGTH) {
            throw new IllegalArgumentException("Invalid index " + index);
        } else {
            return index % WIDTH; 
        }
    }

    /**
     * Gives the row of a certain index.
     * @param index
     * @return int row
     */
    /*	@requires index < WIDTH * HEIGTH && index >= 0;
       	@ensures \result == index / WIDTH;
     */
    public static int indexToRow(int index) throws IllegalArgumentException {
        if (index < 0 || index > WIDTH * HEIGTH) {
            throw new IllegalArgumentException("Invalid index " + index);
        } else {
            return index / WIDTH;
        }
    }

    /**
     * Makes a copy of the board.
     * @param The board that you want to copy
     * @return Board that is an exact copy of the input
     */
    /*@
         ensures \result != this;
         ensures (\forall int i; 0 <= i & i < WIDTH * HEIGHT; \result.getField(i) == this.getField(i));
     */
    public Board deepCopy() {
        Board boardCopy = new Board();    	
        for (int i = 0; i < HEIGTH * WIDTH; i++) {
            boardCopy.fields[i] = this.fields[i];
        }
        return boardCopy;
    }

    /**
     * Calculates the index in the linear array of fields from a (row, col)
     * pair.
     * @return the index belonging to the (row,col)-field
     */
    /*@
      requires 0 <= row & row < HEIGTH;
      requires 0 <= col & col < WIDTH;
     */
    public static int index(int row, int col) {
        return (row * WIDTH) + col;
    }

    /*@
         ensures \result == (0 <= ix && ix < WIDTH * HEIGTH);
     */
    /**
     * Returns true if <code>ix</code> is a valid index of a field on the board.
     * @return <code>true</code> if <code>0 <= ix < WIDTH*HEIGTH</code>
     */
    /*@pure*/
    public static boolean isField(int ix) {
        return 0 <= ix && ix < HEIGTH * WIDTH;
    }

    /*@
         ensures \result == (0 <= row && row < HEIGTH && 0 <= col && col < WIDTH);
     */
    /**
     * Returns true of the (row,col) pair refers to a valid field on the board.
     * 
     * @return true if <code>0 <= row < HEIGTH && 0 <= col < WIDTH</code>
     */
    /*@pure*/
    public static boolean isField(int row, int col) {
        return isField(index(row, col));
    }


    /*@
         requires this.isField(i);
         ensures \result == Mark.EMPTY || \result == Mark.RED || \result == Mark.YELLOW;
     */
    /**
     * Returns the content of the field <code>i</code>.
     * @param i
     * @return the mark on the field
     */
    public Mark getField(int i) {
        return fields[i];
    }

    /*@
         requires this.isField(row,col);
         ensures \result == Mark.EMPTY || \result == Mark.RED || \result == Mark.YELLOW;
     */
    /**
     * Returns the content of the field referred to by the (row,col) pair.
     * @param row the row of the field
     * @param col the column of the field
     * @return the mark on the field
     */
    public Mark getField(int row, int col) {
        return fields[index(row, col)];
    }

    public Mark[] getFields() {
        return fields;
    }

    /*@
         requires this.isField(i);
         ensures \result == (this.getField(i) == Mark.EMPTY);
     */
    /**
     * Returns true if the field <code>i</code> is empty.
     * @param i the index of the field
     * @return true if the field is empty
     */
    public boolean isEmptyField(int i) {
        return getField(i) == Mark.EMPTY;
    }

    /*@
         requires this.isField(row, col);
         ensures \result == (this.getField(row, col) == Mark.EMPTY);
     */
    /**
     * Returns true if the field referred to by the (row,col) pair it empty.
     * 
     * @param row the row of the field
     * @param col the column of the field
     * @return true if the field is empty
     */
    /*@pure*/
    public boolean isEmptyField(int row, int col) {
        return isEmptyField(index(row, col));
    }

    /*@
         ensures \result == (\forall int i; i <= 0 & i < WIDTH * HEIGTH; this.getField(i) != Mark.EMPTY);
     */
    /**
     * Tests if there are no more empty fields on the board.
     * @return true if all fields are occupied
     */
    /*@pure*/
    public boolean isFull() {
        boolean result = true;
        for (int i = 0; i < (HEIGTH * WIDTH); i++) {
            if (isEmptyField(i)) {
                result = false;
                break;
            }
        }    	
        return result;
    }

    /*@ensures \result == this.isFull() || this.hasWinner();
     */
    /**
     * Returns true if the game is over. The game is over when there is a winner
     * or the board is full.
     * @return true if the game is over
     */
    /*@pure*/
    public boolean gameOver() {
        if (isFull() || hasWinner()) {
            setChanged();
            notifyObservers("GameOver");
        }
        return isFull() || hasWinner();
    }

    /*@requires m == Mark.RED || m == Mark.YELLOW;
     */
    /**
     * Checks if a player with Mark m has four horizontal.
     * @return true if the player has four in a row.
     */      
    public boolean hasFourHorizontal(Mark m) {
        boolean result = false;
        // Horizontal
        for (int row = 0; row < HEIGTH * WIDTH; row = row + WIDTH) {
            for (int i = row; i < row + WIDTH; i++) {
                int count = 0;
                for (int i2 = 0; i2 < 4; i2++) {
                      if (isField(i2+i) && getField(i2 + i) == m && indexToRow(i2 + i)== indexToRow(i)) {
                        ++count;
                        if (count == 4) {
                            result = true;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    /*@requires m == Mark.RED || m == Mark.YELLOW;
     */
    /**
     * Checks if a player with Mark m has four vertical.
     * @return true if the player has four in a row.
     */ 
    public boolean hasFourVertical(Mark m) {
        boolean result = false;
        // Vertical
        for (int col = 0; col < WIDTH; col++) {
            for (int i = col; i < HEIGTH * WIDTH; i = i + WIDTH) {
                int count = 0;
                for (int i2 = 0; i2 <= 3 * WIDTH; i2 = i2 + WIDTH) {
                    if (isField(i2 + i) && getField(i2 + i) == m && indexToCol(i2 + i) == indexToCol(i)) {
                        ++count;
                        if (count == 4) {
                            result = true;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    /*@requires m == Mark.RED || m == Mark.YELLOW;
     */
    /**
     * Checks if a player with Mark m has four diagonal.
     * @return true if the player has four in a row.
     */ 
    public boolean hasFourDiagonal(Mark m) {
        boolean result = false;
        for (int i = 0; i < HEIGTH * WIDTH; i++) {
          // Diagonal left top to bottom right
            int count = 0;
            int rowcolCounter = 0;
            for (int i2 = 0; i2 <= (WIDTH + 1) * 3; i2 = i2 + (WIDTH + 1)) {
                if (isField(i2 + i) && getField(i2 + i) == m
                    && indexToCol(i2 + i) == indexToCol(i) + rowcolCounter
                    && indexToRow(i2 + i) == indexToRow(i) + rowcolCounter) {
                    ++count;
                    if (count == 4) {
                        result = true;
                        break;
                    }
                }
                rowcolCounter++;
            }
            // Diagonal right top to bottom left
            count = 0;
            rowcolCounter = 0;
            for (int i2 = 0; i2 <= (WIDTH - 1) * 3; i2 = i2 + (WIDTH - 1)) {
                if (isField(i2 + i) && getField(i2 + i) == m
                    && indexToCol(i2 + i) == indexToCol(i) - rowcolCounter
                    && indexToRow(i2 + i) == indexToRow(i) + rowcolCounter) {
                    ++count;
                    if (count == 4) {
                        result = true;
                        break;
                    }
                }
                rowcolCounter++;
            }
        }
        return result;
    }

    /*@
         requires m == Mark.RED | m == Mark.YELLOW;
         ensures \result == hasFourHorizontal(m) || hasFourVertical(m) || hasFourDiagonal(m);
     */
    /**
     * Checks if the mark <code>m</code> has won. A mark wins if it controls at
     * least one row, column or diagonal.
     * 
     * @param m the mark of interest
     * @return true if the mark has won
     */
    /*@pure*/
    public boolean isWinner(Mark m) {
      return hasFourHorizontal(m) || hasFourVertical(m) || hasFourDiagonal(m);
    }

    /*@
         ensures \result == isWinner(Mark.RED) | \result == isWinner(Mark.YELLOW);
     */
    /**
     * Returns true if the game has a winner. This is the case when one of the
     * marks controls at least one row, column or diagonal.
     * 
     * @return true if the game has a winner.
     */
    /*@pure*/
    public boolean hasWinner() {
      return isWinner(Mark.RED) || isWinner(Mark.YELLOW);
    }

    /**
     * Returns a String representation of this board.
     * @return the game situation as String
     */
    public String toString() {
        String s = "Game status : \n\n";
        for (int i = 0; i < HEIGTH; i++) {
            String row = "";
            for (int j = 0; j < WIDTH; j++) {
                row = row + " " + getField(i, j).toString() + " ";
                if (j < WIDTH - 1) {
                    row = row + "|";
                }
            }
            s = s + row + DELIM + NUMBERING[i * 2];
            if (i < HEIGTH - 1) {
                s = s + "\n" + LINE + DELIM + NUMBERING[i * 2 + 1] + "\n";
            }
        }
        s = s + "\n 0  | 1  | 2  | 3  | 4  | 5  | 6 \n\n";
        return s;
    }

    // -- Commands ---------------------------------------------------

    /*@
         ensures (\forall int i; 0 <= i & i < WIDTH * HEIGTH; this.getField(i) == Mark.EMPTY);
     */
    /**
     * Empties all fields of this board.
     */
    public void reset() {
        for (int i = 0; i < HEIGTH * WIDTH; i++) {
            forceSetField(i, Mark.EMPTY);
        }
        setChanged();
        notifyObservers("reset");
    }

    //This does not notify the view to update the whole board, since this is used for the reset, unefficient
    public void forceSetField(int i, Mark m) {
        fields[i] = m;
    }


    /*@
         requires this.isField(i);
         ensures this.getField(i) == m;
     */
    /**
     * Sets the content of field <code>i</code> to the mark <code>m</code>.
     * @param i
     * @param m the mark to be placed
     */
    public void setField(int i, Mark m) {
        fields[getHighestField(indexToCol(i))] = m;
        setChanged();
        notifyObservers("setField");
    }

    /*@
         requires this.isField(row,col);
         ensures this.getField(row,col) == m;

     */
    /**
     * Sets the content of the field represented by the (row,col) pair to the
     * mark <code>m</code>.
     * @param row the field's row
     * @param col the field's column
     * @param m the mark to be placed
     */
    public void setField(int row, int col, Mark m) {
        setField(getHighestField(col), m);
        setChanged();
        notifyObservers("setField");
    }

    public void updateBoard() {
        setChanged();
        notifyObservers("setField");
    }

}
