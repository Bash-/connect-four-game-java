package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import model.Board;
import model.Mark;

/**
 * Test class for the class Board.
 * @author Martijn Gemmink
 *
 */
public class BoardTest {

	// Instance variables
	Board b1 = new Board();
	Board b2 = new Board();

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testBoard() {
		Board test = new Board();
		for (int i = 0; i < Board.HEIGTH * Board.WIDTH; i++) {
			assertEquals("Check if every field on the board is empty.", Mark.EMPTY,
							test.getField(i));
		}
	}

	@Test
	public void testGetHighestField() {
		b1.forceSetField(36, Mark.RED);
		b1.forceSetField(29, Mark.YELLOW);
		b1.forceSetField(22, Mark.RED);
		b1.forceSetField(38, Mark.RED);
		b1.forceSetField(35, Mark.RED);

		// Test if highest field is correct.
		assertEquals("Highest field on column 0 = 28", 28, b1.getHighestField(0));
		assertEquals("Highest field on column 1 = 15", 15, b1.getHighestField(1));
		assertEquals("Highest field on column 3 = 31", 31, b1.getHighestField(3));

		// Reset the board.
		b1.reset();
	}

	@Test
	public void testIndexToCol() {
		assertEquals("Index = 0 gives col = 0", 0, Board.indexToCol(0));
		assertEquals("Index = 7 gives col = 0", 0, Board.indexToCol(7));
		assertEquals("Index = 10 gives col = 3", 3, Board.indexToCol(10));
		assertEquals("Index = 41 gives col = 6", 6, Board.indexToCol(41));
		assertEquals("Index = 35 gives col = 0", 0, Board.indexToCol(35));
		assertEquals("Index = 16 gives col = 2", 2, Board.indexToCol(16));
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testExceptionIndexToCol() {
		// Test if illegal argument throws an exception.
		Board.indexToCol(778);
	}

	@Test
	public void testIndexToRow() {
		assertEquals("Index = 0 gives row = 0", 0, Board.indexToRow(0));
		assertEquals("Index = 7 gives row = 1", 1, Board.indexToRow(7));
		assertEquals("Index = 10 gives row = 1", 1, Board.indexToRow(10));
		assertEquals("Index = 41 gives row = 5", 5, Board.indexToRow(41));
		assertEquals("Index = 35 gives row = 5", 5, Board.indexToRow(35));
		assertEquals("Index = 16 gives row = 2", 2, Board.indexToRow(16));
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void testExceptionIndexToRow() {
		// Test if illegal argument throws an exception.
		Board.indexToCol(-1);
	}

	@Test
	public void testDeepCopy() {
		// Setting fields on different colors.
		b1.forceSetField(1, Mark.RED);
		b1.forceSetField(3, Mark.YELLOW);
		b1.forceSetField(40, Mark.RED);
		b1.forceSetField(12, Mark.RED);
		b1.forceSetField(11, Mark.RED);

		// Make a copy of the board.
		b2 = b1.deepCopy();

		// Check if boards b1 and b2 are equal.
		for (int i = 0; i < Board.HEIGTH * Board.WIDTH; i++) {
			Mark m1 = b1.getField(i);
			Mark m2 = b2.getField(i);
			assertEquals("Checks if all marks on both boards are equal.", m1, m2);
		}
		// Reset the boards.
		b1.reset();
		b2.reset();
	}

	@Test
	public void testIndex() {
		assertEquals("(row,col) = 0,0 gives index = 0", 0, Board.index(0, 0));
		assertEquals("(row,col) = 0,4 gives index = 4", 4, Board.index(0, 4));
		assertEquals("(row,col) = 4,4 gives index = 32", 32, Board.index(4, 4));
		assertEquals("(row,col) = 5,3 gives index = 38", 38, Board.index(5, 3));
		assertEquals("(row,col) = 5,0 gives index = 35", 35, Board.index(5, 0));
	}

	@Test
	public void testIsFieldInt() {
		// Check for every field on the board if it's a field.
		for (int i = 0; i < Board.WIDTH * Board.HEIGTH; i++) {
			assertTrue(Board.isField(i));
		}

		// Check if isField(int i) returns false on non existing fields.
		assertFalse(Board.isField(-1));
		assertFalse(Board.isField(Board.WIDTH * Board.HEIGTH));
	}

	@Test
	public void testGetFieldInt() {
		b1.forceSetField(4, Mark.RED);
		b1.forceSetField(5, Mark.YELLOW);

		// Check if getField(int i) returns the right value.
		assertEquals("The mark on index 4 = Mark.RED", Mark.RED, b1.getField(4));
		assertEquals("The mark on index 5 = Mark.YELLOW", Mark.YELLOW, b1.getField(5));
		assertEquals("Any other mark is still Mark.EMPTY", Mark.EMPTY, b1.getField(2));

		// Reset the board.
		b1.reset();
	}

	@Test
	public void testIsEmptyFieldInt() {
		b1.forceSetField(4, Mark.RED);
		b1.forceSetField(5, Mark.YELLOW);

		// Check if an index is an empty field.
		assertTrue(b1.isEmptyField(0));
		assertTrue(b1.isEmptyField(1));
		assertTrue(b1.isEmptyField(18));
		assertTrue(b1.isEmptyField(41));

		// Check if index 4 and 5 return false.
		assertFalse(b1.isEmptyField(4));
		assertFalse(b1.isEmptyField(5));

		// Reset the board.
		b1.reset();
	}

	@Test
	public void testIsFull() {
		for (int i = 0; i < Board.WIDTH * Board.HEIGTH; i++) {
			b1.forceSetField(i, Mark.RED);
		}
		// Check if Board b1 is full.
		assertTrue(b1.isFull());

		// Check if Board b2 is full.
		assertFalse(b2.isFull());

		// Reset the board.
		b1.reset();
	}

	@Test
	public void testGameOver() {
		// Test if the board is empty gameOver() returns false.
		assertFalse(b1.gameOver());

		// Fill the board with Marks.
		for (int i = 0; i < Board.WIDTH * Board.HEIGTH; i++) {
			b1.forceSetField(i, Mark.RED);
		}
		assertTrue(b1.gameOver());

		// Reset the board
		b1.reset();

		// Check if gameOver() returns true if there is a winner.
		b1.forceSetField(35, Mark.RED);
		b1.forceSetField(29, Mark.RED);
		b1.forceSetField(23, Mark.RED);
		b1.forceSetField(17, Mark.RED);

		assertTrue(b1.gameOver());
	}

	@Test
	public void testHasFourHorizontal() {
		b1.forceSetField(35, Mark.RED);
		b1.forceSetField(36, Mark.RED);
		b1.forceSetField(37, Mark.RED);
		b1.forceSetField(38, Mark.RED);

		b1.forceSetField(19, Mark.YELLOW);
		b1.forceSetField(20, Mark.YELLOW);
		b1.forceSetField(14, Mark.YELLOW);
		b1.forceSetField(15, Mark.YELLOW);

		// Check if hasFourHorizontal works as expected.
		assertTrue(b1.hasFourHorizontal(Mark.RED));

		// Check if yellow has four horizontal. (false)
		assertFalse(b1.hasFourVertical(Mark.YELLOW));

		// Reset the board
		b1.reset();
	}

	@Test
	public void testHasFourVertical() {
		b1.forceSetField(36, Mark.RED);
		b1.forceSetField(29, Mark.RED);
		b1.forceSetField(22, Mark.RED);
		b1.forceSetField(15, Mark.RED);

		b1.forceSetField(26, Mark.YELLOW);
		b1.forceSetField(33, Mark.YELLOW);
		b1.forceSetField(40, Mark.YELLOW);
		b1.forceSetField(5, Mark.YELLOW);

		// Check if hasFourVertical works as expected.
		assertTrue(b1.hasFourVertical(Mark.RED));

		// Check if yellow has four vertical. (false)
		assertFalse(b1.hasFourVertical(Mark.YELLOW));

		// Reset the board
		b1.reset();
	}

	@Test
	public void testHasFourDiagonal() {
		// Left to right
		b1.forceSetField(35, Mark.RED);
		b1.forceSetField(29, Mark.RED);
		b1.forceSetField(23, Mark.RED);
		b1.forceSetField(17, Mark.RED);

		// Right to left
		b2.forceSetField(9, Mark.RED);
		b2.forceSetField(17, Mark.RED);
		b2.forceSetField(25, Mark.RED);
		b2.forceSetField(33, Mark.RED);

		// Check if hasFourDiagonal works as expected.
		b1.forceSetField(26, Mark.YELLOW);
		b1.forceSetField(20, Mark.YELLOW);
		b1.forceSetField(7, Mark.YELLOW);
		b1.forceSetField(1, Mark.YELLOW);

		// Check if hasFourDiagonal works as expected.
		assertTrue(b1.hasFourDiagonal(Mark.RED));
		assertTrue(b2.hasFourDiagonal(Mark.RED));

		// Check if yellow has four diagonal. (false)
		assertFalse(b1.hasFourVertical(Mark.YELLOW));

		// Reset the board
		b1.reset();
		b2.reset();
	}

	@Test
	public void testIsWinner() {
		// With a diagonal
		b1.forceSetField(35, Mark.RED);
		b1.forceSetField(29, Mark.RED);
		b1.forceSetField(23, Mark.RED);
		b1.forceSetField(17, Mark.RED);

		assertTrue(b1.isWinner(Mark.RED));
		assertFalse(b1.isWinner(Mark.YELLOW));

		b1.reset();

		// With four horizontal
		b1.forceSetField(35, Mark.RED);
		b1.forceSetField(36, Mark.RED);
		b1.forceSetField(37, Mark.RED);
		b1.forceSetField(38, Mark.RED);

		assertTrue(b1.isWinner(Mark.RED));
		assertFalse(b1.isWinner(Mark.YELLOW));

		b1.reset();

		// With four vertical
		b1.forceSetField(36, Mark.RED);
		b1.forceSetField(29, Mark.RED);
		b1.forceSetField(22, Mark.RED);
		b1.forceSetField(15, Mark.RED);

		assertTrue(b1.isWinner(Mark.RED));
		assertFalse(b1.isWinner(Mark.YELLOW));

		b1.reset();

	}

	@Test
	public void testHasWinner() {
		// Check if hasWinner is initiale false.
		assertFalse(b1.hasWinner());

		// With a diagonal
		b1.forceSetField(35, Mark.RED);
		b1.forceSetField(29, Mark.RED);
		b1.forceSetField(23, Mark.RED);
		b1.forceSetField(17, Mark.RED);

		assertTrue(b1.hasWinner());

		b1.reset();

		// With four horizontal
		b1.forceSetField(35, Mark.RED);
		b1.forceSetField(36, Mark.RED);
		b1.forceSetField(37, Mark.RED);
		b1.forceSetField(38, Mark.RED);

		assertTrue(b1.hasWinner());

		b1.reset();

		// With four vertical
		b1.forceSetField(36, Mark.RED);
		b1.forceSetField(29, Mark.RED);
		b1.forceSetField(22, Mark.RED);
		b1.forceSetField(15, Mark.RED);

		assertTrue(b1.hasWinner());

		b1.reset();

	}

	@Test
	public void testReset() {
		b1.forceSetField(1, Mark.RED);
		b1.forceSetField(17, Mark.YELLOW);

		// Check if the reset works
		b1.reset();
		assertTrue(b1.getField(1) == Mark.EMPTY);
		assertTrue(b1.getField(17) == Mark.EMPTY);
	}

	@Test
	public void testForceSetField() {
		// Check if field is initialy empty.
		assertTrue(b1.isEmptyField(2));

		// Force field to Mark.RED.
		b1.forceSetField(2, Mark.RED);

		// Check if field = Mark.RED.
		assertEquals("The mark on index 2 = Mark.RED", Mark.RED, b1.getField(2));

		// Reset the board
		b1.reset();
	}

	@Test
	public void testSetFieldIntMark() {
		// Check if field is initialy empty.
		assertTrue(b1.isEmptyField(2));

		// Force field to Mark.RED.
		b1.forceSetField(2, Mark.RED);

		// Check if field = Mark.RED.
		assertEquals("The mark on index 2 = Mark.RED", Mark.RED, b1.getField(2));

		// Reset the board
		b1.reset();
	}

}
