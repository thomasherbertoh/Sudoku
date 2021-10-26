package src.me.Herbert.Thomas.Sudoku.Checker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javafx.util.Pair;
import src.me.Herbert.Thomas.Sudoku.Sudoku.Sudoku;
import src.me.Herbert.Thomas.Sudoku.Sudoku.SudokuCell;

public class Checker {

	private Sudoku sudoku;
	private boolean valid;

	public Checker(Sudoku sudoku) {
		this.sudoku = sudoku;
		this.valid = check();
	}

	public boolean check() {
		/*
		To avoid checking too many cells multiple times, I only check the rows,
		columns, and boxes of cells in the positions marked with an 'x'
		+===+===+===+===+===+===+===+===+===+
		# x |   |   #   |   |   #   |   |   #
		+---+---+---+---+---+---+---+---+---+
		#   |   |   #   | x |   #   |   |   #
		+---+---+---+---+---+---+---+---+---+
		#   |   |   #   |   |   #   |   | x #
		+===+===+===+===+===+===+===+===+===+
		#   | x |   #   |   |   #   |   |   #
		+---+---+---+---+---+---+---+---+---+
		#   |   |   #   |   | x #   |   |   #
		+---+---+---+---+---+---+---+---+---+
		#   |   |   #   |   |   # x |   |   #
		+===+===+===+===+===+===+===+===+===+
		#   |   | x #   |   |   #   |   |   #
		+---+---+---+---+---+---+---+---+---+
		#   |   |   # x |   |   #   |   |   #
		+---+---+---+---+---+---+---+---+---+
		#   |   |   #   |   |   #   | x |   #
		+===+===+===+===+===+===+===+===+===+
		*/
		// List contains column, row pairs
		List<Pair<Integer, Integer>> checkPositions = new ArrayList<>() {
			{
				add(new Pair<Integer, Integer>(0, 0));
				add(new Pair<Integer, Integer>(4, 1));
				add(new Pair<Integer, Integer>(8, 2));
				add(new Pair<Integer, Integer>(1, 3));
				add(new Pair<Integer, Integer>(5, 4));
				add(new Pair<Integer, Integer>(6, 5));
				add(new Pair<Integer, Integer>(2, 6));
				add(new Pair<Integer, Integer>(3, 7));
				add(new Pair<Integer, Integer>(7, 8));
			}
		};

		// Create arrays to hold rows/columns/boxes
		List<Integer> row = new ArrayList<Integer>();
		List<Integer> column = new ArrayList<Integer>();
		List<Integer> box = new ArrayList<Integer>();

		// Contains boolean values saying if things are valid or not
		List<Boolean> valid = new ArrayList<Boolean>();

		// The cell found at each position we check
		SudokuCell cell;

		for (Pair<Integer, Integer> pos : checkPositions) {
			// Get the cell referenced by the current checkPosition
			cell = sudoku.getCellAt(pos.getKey(), pos.getValue());

			// Get the row, column, and box of the current cell
			row = sudoku.getRowValuesByCell(cell);
			column = sudoku.getColValuesByCell(cell);
			box = sudoku.getBoxValuesByCell(cell);

			// If the row, column, and box are all valid, true will be added.
			// Otherwise, false will be added.
			valid.add(checkList(row) && checkList(column) && checkList(box));
		}

		// Remove duplicates from the boolean list 'valid'
		Set<Boolean> set_valid = new HashSet<Boolean>(valid);

		// If the size of the set is 1 (contains one value) and it contains
		// 'true', then there must be no incorrect rows, columns, or boxes.
		return set_valid.size() == 1 && set_valid.contains(true);
	}

	/*
	 * Checks if all values in a list are unique and returns the boolean result
	 */
	public boolean checkList(List<Integer> list) {
		int[] nums = new int[9];
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) < 1) {
				return false;
			}
			nums[list.get(i) - 1]++;
		}
		for (int i = 0; i < nums.length; i++) {
			if (nums[i] != 1) {
				return false;
			}
		}
		return true;
	}

	public boolean isValid() {
		return this.valid;
	}
}
