package src.me.Herbert.Thomas.Sudoku.NoteReducer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import src.me.Herbert.Thomas.Sudoku.Sudoku.Sudoku;
import src.me.Herbert.Thomas.Sudoku.Sudoku.SudokuCell;

public class NoteReducer {

	Sudoku sudoku;

	public NoteReducer(Sudoku sudoku) {
		this.sudoku = sudoku;
	}

	public void reduceNotes() {
		nakedPairs();
		pointingPairs();
		boxLineReduction();
		hiddenPairs();
	}

	// Generate all the possible pairs of digits given a list of available digits
	private void generatePairs(List<int[]> combinations, int data[], List<Integer> avail, int start, int end,
			int index) {
		if (index == data.length)
			combinations.add(data.clone());
		else if (start <= end) {
			data[index] = avail.get(start);
			generatePairs(combinations, data, avail, start + 1, end, index + 1);
			generatePairs(combinations, data, avail, start + 1, end, index);
		}
	}

	private void hiddenPairs() {
		/*
		 * If in a given collection there are precisely two cells that can hold a pair
		 * of digits, then all the other notes from those cells can be removed
		 */

		// make array of collections of cells
		List<List<List<SudokuCell>>> collections = new ArrayList<>();

		// add boxes
		collections.add(this.sudoku.boxes);

		// add rows
		List<List<SudokuCell>> rows = new ArrayList<>();
		for (int i = 0; i < 9; i++)
			rows.add(this.sudoku.getRowCellsByRowID(i));
		collections.add(rows);

		// add columns
		List<List<SudokuCell>> cols = new ArrayList<>();
		for (int i = 0; i < 9; i++)
			cols.add(this.sudoku.getColCellsByColID(i));
		collections.add(cols);

		for (List<List<SudokuCell>> collection : collections) {
			for (List<SudokuCell> list : collection) {
				// hold counts of digits
				List<Integer> counts = new ArrayList<Integer>(Collections.nCopies(10, 0));
				// number of digits with two possible positions
				int twos = 0;
				for (SudokuCell cell : list) {
					// cell already solved
					if (cell.getVal() != 0)
						continue;

					for (int poss : cell.getPossibleValues()) {
						// increment count
						counts.set(poss, counts.get(poss) + 1);
						if (counts.get(poss) == 2)
							twos++; // digit has two possible positions so far
						else if (counts.get(poss) == 3)
							twos--; // digit no longer has two possible positions
					}
				}

				// can't make pairs with less than two numbers
				if (twos < 2)
					continue;

				// generate list of digits that occur twice in the notes in this collection
				List<Integer> available = new ArrayList<>();
				for (int i = 0; i < counts.size(); i++)
					if (counts.get(i) == 2)
						available.add(i);

				// get all possible pairs of available digits
				List<int[]> pairs = new ArrayList<>();
				generatePairs(pairs, new int[2], available, 0, available.size() - 1, 0);
				// no possible pairs somehow? seems like a pointless check
				if (pairs.size() == 0)
					continue;

				// contains the cells both digits of a pair occur in
				List<SudokuCell> common_cells;
				for (int[] pair : pairs) {
					common_cells = new ArrayList<>();
					for (SudokuCell cell : list)
						if (cell.getVal() == 0 && cell.getPossibleValues().contains(pair[0])
								&& cell.getPossibleValues().contains(pair[1]) && cell.getPossibleValues().size() > 2)
							// cell can't be solved, must contain both digits of the pair, and must have
							// more than two possible values
							common_cells.add(cell);

					// the digits need to occur in the same cell exactly twice to be a hidden pair
					if (common_cells.size() != 2)
						continue;

					for (SudokuCell cell : common_cells) {
						if (cell.getVal() != 0)
							continue;

						for (int i = 1; i <= 9; i++) {
							if (i == pair[0] || i == pair[1])
								continue;
							if (cell.getPossibleValues().contains(i))
								cell.updateCompPoss(i);
						}
					}
				}
			}
		}
	}

	private void nakedPairs() {
		/*
		 * if there are two cells in a box/row/column that contain the same two options
		 * then no other cells in that box/row/column can contain those values
		 */

		// make array of collections of cells
		List<List<List<SudokuCell>>> collections = new ArrayList<>();

		// add boxes
		collections.add(this.sudoku.boxes);

		// add rows
		List<List<SudokuCell>> rows = new ArrayList<>();
		for (int i = 0; i < 9; i++)
			rows.add(this.sudoku.getRowCellsByRowID(i));
		collections.add(rows);

		// add columns
		List<List<SudokuCell>> cols = new ArrayList<>();
		for (int i = 0; i < 9; i++)
			cols.add(this.sudoku.getColCellsByColID(i));
		collections.add(cols);

		/*
		 * if we find a naked pair we may uncover a new naked pair in a collection we've
		 * already iterated over (e.g., if we find a naked pair in the columns, there
		 * could be relevant changes in the rows or boxes) which wouldn't otherwise be
		 * investigated
		 */
		boolean changed;
		do {
			changed = false;
			for (List<List<SudokuCell>> collection : collections)
				for (List<SudokuCell> l : collection) {
					HashSet<List<Integer>> two_options = new HashSet<>();
					for (SudokuCell c : l) {
						// don't care about solved cells
						if (c.getVal() != 0)
							continue;

						/*
						 * can't apply nakedPairs to a cell if it doesn't have exactly two possible
						 * values
						 */
						if (c.getPossibleValues().size() != 2)
							continue;

						// already found a cell with these exact possible values; can apply naked pairs
						if (two_options.contains(c.getPossibleValues())) {
							/*
							 * remove all instances of these values from the notes of other cells in the
							 * list
							 */
							for (SudokuCell rem : l) {
								// solved cell
								if (rem.getVal() != 0)
									continue;

								// same cell
								if (rem.equals(c))
									continue;

								// for each of the possible values of c
								for (int ind = 0; ind < 2; ind++)
									// if rem's list of possible values contains the `ind`th possible value of c and
									// rem's possible values aren't the same as c's
									if (rem.getPossibleValues().contains(c.getPossibleValues().get(ind))
											&& !rem.getPossibleValues().equals(c.getPossibleValues())) {
										// remove this possible value from rem
										rem.updateCompPoss(c.getPossibleValues().get(ind));
										changed = true;
									}
							}
						} else
							// first occurrence of cell with these possible values; add to set
							two_options.add(c.getPossibleValues());
					}
				}
		} while (changed);
	}

	private void pointingPairs() {
		/*
		 * if all the occurrences of a note are in the same row/column then no other
		 * cells in that row/column can contain that number
		 */
		int occurrences, row, col, work_with_rows;
		SudokuCell exampleCell;
		for (List<SudokuCell> box : this.sudoku.boxes)
			for (int i = 1; i <= 9; i++) {
				exampleCell = null;
				occurrences = 0;
				row = -1;
				col = -1;
				work_with_rows = -1;
				for (SudokuCell cell : box) {
					// if the cell has already been solved or it doesn't contain the value we're
					// currently interested in we can't do anything with it
					if (cell.getVal() != 0 || !cell.getPossibleValues().contains(i))
						continue;

					// if we haven't found an occurrence yet
					if (row == -1 && col == -1) {
						exampleCell = cell;
						occurrences++;
						row = cell.row;
						col = cell.col;
					} else if (cell.row == row && work_with_rows != 0) {
						// work_with_rows being different to 0 means it's either 1 (so we're working
						// with rows) or it's -1 (so we haven't decided yet, and we decide to work with
						// rows)
						work_with_rows = 1;
						occurrences++;
					} else if (cell.col == col && work_with_rows != 1) {
						// if this cell shares the same row/column of the previous occurrences
						work_with_rows = 0;
						occurrences++;
					} else {
						// if this cell doesn't share a row/column with the previous occurrences we
						// can't do anything with this digit, reset variables to signal this to the
						// outside
						row = -1;
						col = -1;
						break;
					}
				}
				if (row == -1 || occurrences < 2 || occurrences > 3)
					continue;

				// need to remove any occurences of `i` in the notes of other cells in the
				// row/column
				if (work_with_rows == 1)
					// for each cell in the row of this cell
					for (SudokuCell c : this.sudoku.getRowCellsByCell(exampleCell)) {
						// if it's in the same box as the original cell we don't want to touch it
						if (c.getVal() != 0 || c.getBoxID() == exampleCell.getBoxID())
							continue;

						// if it contains a note with the current digit we remove it
						if (c.getPossibleValues().contains(i))
							c.updateCompPoss(i);
					}
				else
					for (SudokuCell c : this.sudoku.getColCellsByCell(exampleCell)) {
						if (c.getVal() != 0 || c.getBoxID() == exampleCell.getBoxID())
							continue;

						if (c.getPossibleValues().contains(i))
							c.updateCompPoss(i);
					}
			}
	}

	private void boxLineReduction() {
		/*
		 * For every row and column, if all the possible locations for a digit lie in
		 * the same box, remove other instances of that digit from the notes in that box
		 */
		for (int iter = 0; iter < 2; iter++) {

			List<List<SudokuCell>> lists = new ArrayList<>();
			for (int i = 0; i < 9; i++) {
				if (iter == 0)
					lists.add(this.sudoku.getRowCellsByRowID(i));
				else
					lists.add(this.sudoku.getColCellsByColID(i));
			}

			// for every row
			for (List<SudokuCell> list : lists)
				// for each number from 1 to 9
				for (int i = 1; i <= 9; i++) {
					int box_of_interest = -1;
					// scan each cell of current row
					for (SudokuCell c : list) {
						// if already solved skip
						if (c.getVal() != 0)
							continue;
						else if (c.getVal() == i) {
							box_of_interest = -1;
							break;
						}
						if (c.getPossibleValues().contains(i)) {
							if (box_of_interest == -1)
								box_of_interest = c.getBoxID();
							else if (box_of_interest != c.getBoxID()) {
								/*
								 * there are instances of this digit in the notes of cells in different boxes in
								 * the row
								 */
								box_of_interest = -1;
								break;
							}
						}
					}
					// if we didn't find any interesting boxes we move onto the next digit
					if (box_of_interest == -1)
						continue;

					List<SudokuCell> b = this.sudoku.getBoxCellsByBoxID(box_of_interest);
					for (SudokuCell c : b) {
						/*
						 * if c is in the row, solved, or doesn't contain a note with the digit we're
						 * interested in, then we don't care about it and we move onto the next cell
						 */
						if (iter == 0) {
							if (c.row == list.get(0).row || c.getVal() != 0 || !c.getPossibleValues().contains(i))
								continue;
						} else if (iter == 1) {
							if (c.col == list.get(0).col || c.getVal() != 0 || !c.getPossibleValues().contains(i))
								continue;
						}
						c.updateCompPoss(i);
					}
				}
		}
	}
}
