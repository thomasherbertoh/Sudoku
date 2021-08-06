package src.me.Herbert.Thomas.Sudoku.Checker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		// Create arrays to hold rows/columns/boxes
		List<Integer> row = new ArrayList<Integer>();
		List<Integer> column = new ArrayList<Integer>();
		List<Integer> box = new ArrayList<Integer>();
		
		// Contains boolean values saying if things are valid or not
		List<Boolean> valid = new ArrayList<Boolean>();

		for (SudokuCell cell : this.sudoku.cells) {
			// Get the row, column, and box of the current cell
			row = sudoku.getRowValues(cell);
			column = sudoku.getColValues(cell);
			box = sudoku.getBoxValues(cell);

			// If the row, column, and box are all valid, true will be added. Otherwise, false will be added.
			valid.add(checkList(row) && checkList(column) && checkList(box));
		}
		// Remove duplicates from the boolean list 'valid'
		Set<Boolean> set_valid = new HashSet<Boolean>(valid);
		// If the size of the set is 1 (contains one value) and it contains 'true', then there must be no incorrect rows, columns, or boxes.
		return set_valid.size() == 1 && set_valid.contains(true);
	}

	/*
	 * Checks if all values in a list are unique and returns the true or false result
	 */
	public boolean checkList(List<Integer> list) {
		Set<Integer> set_list = new HashSet<Integer>(list);
		return set_list.size() == list.size();
	}

	public boolean isValid() {
		return this.valid;
	}
	
}
