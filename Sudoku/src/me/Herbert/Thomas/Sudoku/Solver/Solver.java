package src.me.Herbert.Thomas.Sudoku.Solver;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;
import src.me.Herbert.Thomas.Sudoku.Checker.Checker;
import src.me.Herbert.Thomas.Sudoku.Sudoku.Sudoku;
import src.me.Herbert.Thomas.Sudoku.Sudoku.SudokuCell;

public class Solver {

	public Sudoku sudoku;
	public boolean solved = false;

	public List<List<SudokuCell>> rows = new ArrayList<List<SudokuCell>>();
	public List<List<SudokuCell>> cols = new ArrayList<List<SudokuCell>>();
	public List<List<SudokuCell>> boxes = new ArrayList<List<SudokuCell>>();

	public Solver(Sudoku sudoku, Boolean solve) {
		this.sudoku = sudoku;

		this.rows = getRows();
		this.cols = getCols();
		this.boxes = this.sudoku.boxes;

		if (solve) {
			this.solved = solve(this.sudoku);
		}
	}

	public boolean solve(Sudoku sudoku) {
		// Set to true if the sudoku has been solved
		boolean ret = false;
		// Preliminary check to see if the sudoku is solved whilst also fetching the
		// best row
		Pair<Integer, List<SudokuCell>> bestRow = bestChoice(rows);
		if (bestRow.getKey() == null) {
			// bestChoice(lists) returns a pair containing `null` only if none of the lists
			// passed to it contain missing digits, meaning we must have solved the grid and
			// we exit as soon as possible
			ret = true;
		}

		// if we haven't solved the sudoku
		if (!ret) {
			Pair<SudokuCell, Integer> best = bestMoveBiforcation();
			if (best == null) {
				Checker checker = new Checker(this.sudoku);
				if (checker.isValid()) {
					return true;
				} else {
					return false;
				}
			}
			SudokuCell bestCell = best.getKey();

			SudokuCell updated = null;

			if (bestCell != null) {
				if (bestCell.getPossibleValues().size() == 1) {
					// Update the value of the best choice to its only possible value
					bestCell.updateVal(best.getValue());

					// Save the updated cell
					updated = bestCell;

					// Recursive call trying to solve the sudoku with this updated value
					ret = solve(sudoku);

					// ret will be `false` if we were unable to solve the sudoku, meaning that this
					// selection was wrong in this situation. We have to revert the change and
					// continue (in this case we will return from the function)
					if (!ret && updated != null) {
						updated.updateVal(0);
					}
				} else if (bestCell.getPossibleValues().size() != 0) {
					// If the best cell is a cell with multiple possible valid values, we have to
					// try them all until we find one that solves the sudoku or we run out of
					// options
					for (int i = 0; i < bestCell.getPossibleValues().size() && !ret; i++) {
						// Update the value of the best choice to the current option
						bestCell.updateVal(bestCell.getPossibleValues().get(i));

						// Save the updated cell
						updated = bestCell;

						// Recursive call trying to solve the sudoku with this updated value
						ret = solve(sudoku);

						// ret will be `false` if we were unable to solve the sudoku, meaning that this
						// selection was wrong in this situation. We have to revert the change and
						// continue (in this case we'll try the next possible value if there is one,
						// otherwise we'll end up returning from the function)
						if (!ret && updated != null) {
							updated.updateVal(0);
						}
					}

				}
			} else {
				Checker checker = new Checker(this.sudoku);
				if (checker.isValid()) {
					ret = true;
				}
			}
		}
		return ret;
	}

	/*
	 * Fetches the rows of the sudoku
	 */
	private List<List<SudokuCell>> getRows() {
		List<List<SudokuCell>> rows = new ArrayList<List<SudokuCell>>();
		for (int i = 0; i < 9; i++) {
			rows.add(this.sudoku.getRowCellsByRowID(i));
		}
		return rows;
	}

	/*
	 * Fetches the columns of the sudoku
	 */
	private List<List<SudokuCell>> getCols() {
		List<List<SudokuCell>> cols = new ArrayList<List<SudokuCell>>();
		for (int i = 0; i < 9; i++) {
			cols.add(this.sudoku.getColCellsByColID(i));
		}
		return cols;
	}

	/*
	 * Returns a pair containing the list with the least missing digits and the
	 * number of missing digits it contains (but the other way around)
	 */
	public Pair<Integer, List<SudokuCell>> bestChoice(List<List<SudokuCell>> lists) {
		// List to be returned
		List<SudokuCell> retList = new ArrayList<SudokuCell>();

		int minZeros = 9;
		int maxZeros = 0;
		int count;

		for (List<SudokuCell> list : lists) {
			count = 0;
			// Count number of missing digits in the list
			for (SudokuCell c : list) {
				if (c.getVal() == 0) {
					count++;
				}
			}
			// New best list
			if (count < minZeros && count > 0) {
				minZeros = count;
				retList = list;
			}

			if (count > maxZeros) {
				maxZeros = count;
			}
		}

		Pair<Integer, List<SudokuCell>> retPair;
		if (maxZeros != 0) {
			retPair = new Pair<>(minZeros, retList);
		} else {
			// maxZeros == 0, therefore there is not a list that has a missing digit and the
			// sudoku must be solved
			this.solved = true;
			retPair = new Pair<>(-1, null);
		}
		return retPair;
	}

	/*
	 * Returns the cell with the least possible values in the given list
	 */
	public SudokuCell chooseLeast(List<SudokuCell> list) {
		SudokuCell bestCell = null;
		int least = 10;
		for (SudokuCell c : list) {
			// If this cell hasn't already been filled in and it's better than the best one
			// found so far
			if (c.getVal() == 0 && c.getPossibleValues().size() < least) {
				// Save new best cell
				bestCell = c;
				least = c.getPossibleValues().size();
			}
		}
		return bestCell;
	}

	/*
	 * Searches for cells with just one possible value
	 */
	public Pair<SudokuCell, Integer> findNakedSingles() {
		// Find the best row, column, and box of the sudoku
		Pair<Integer, List<SudokuCell>> bestRow = bestChoice(rows);
		Pair<Integer, List<SudokuCell>> bestCol = bestChoice(cols);
		Pair<Integer, List<SudokuCell>> bestBox = bestChoice(boxes);

		// Find the best cell for the best row, best column, and best box
		SudokuCell bestChoiceRow = null;
		SudokuCell bestChoiceCol = null;
		SudokuCell bestChoiceBox = null;
		if (bestRow.getKey() != -1) {
			bestChoiceRow = chooseLeast(bestRow.getValue());
		}
		if (bestCol.getKey() != -1) {
			bestChoiceCol = chooseLeast(bestCol.getValue());
		}
		if (bestBox.getKey() != -1) {
			bestChoiceBox = chooseLeast(bestBox.getValue());
		}

		// Preliminary selection
		SudokuCell best = bestChoiceRow;
		// This should never be necessary...currently here as a sanity check
		if (best == null) {
			best = bestCell(10);
		}
		// One of the calls to bestChoice() could have marked the sudoku as solved
		if (!this.solved) {
			// Choosing the best cell out of the three possibilities
			if (bestChoiceCol != null && bestChoiceCol.getPossibleValues().size() < best.getPossibleValues().size()) {
				best = bestChoiceCol;
			}
			if (bestChoiceBox != null && bestChoiceBox.getPossibleValues().size() < best.getPossibleValues().size()) {
				best = bestChoiceBox;
			}
		}

		// Possible that the best cell to check isn't in the row/col/box with the least
		// missing digits
		if (best == null) {
			best = bestCell(10);
		} else if (best.getPossibleValues().size() > 1) {
			best = bestCell(best.getPossibleValues().size());
		}
		if (best != null && best.getPossibleValues().size() >= 1) {
			return new Pair<SudokuCell, Integer>(best, best.getPossibleValues().get(0));
		} else {
			return null;
		}
	}

	/*
	 * Searches for a cell that's the only possible location for a digit in the
	 * given list
	 */
	public Pair<SudokuCell, Integer> findHiddenSingleInList(List<SudokuCell> list) {
		/*
		 * Iterate over the given list searching for cells that contain the only note
		 * for a certain digit
		 */
		int[] possCount;
		possCount = new int[10];
		// for every cell in this row
		for (SudokuCell c : list) {
			// if the cell is already filled in we don't care
			if (c.getVal() == 0) {
				// for each of the cell's possible values, increment it's count
				for (int i : c.getPossibleValues()) {
					possCount[i - 1]++;
				}
			}
		}
		// for each possible value
		for (int i = 0; i < possCount.length; i++) {
			// if it appears precisely once
			if (possCount[i] == 1) {
				// there's a hidden single that should be set to this value
				for (SudokuCell c : list) {
					if (c.getVal() == 0 && c.getPossibleValues().contains(i + 1)) {
						return new Pair<SudokuCell, Integer>(c, i + 1);
					}
				}
			}
		}
		return null;
	}

	/*
	 * Searches for a cell that are the only possible location for a digit in a row,
	 * column, or box
	 */
	public Pair<SudokuCell, Integer> findHiddenSingles() {
		// The reason for these specific positions can be found in the Checker.java file
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

		Pair<SudokuCell, Integer> candidate = null;

		SudokuCell cell;

		for (Pair<Integer, Integer> pos : checkPositions) {
			// Get the cell at pos
			cell = sudoku.getCellAt(pos.getKey(), pos.getValue());
			// row
			candidate = findHiddenSingleInList(sudoku.getRowCellsByCell(cell));
			if (candidate != null) {
				return candidate;
			}
			// col
			candidate = findHiddenSingleInList(sudoku.getColCellsByCell(cell));
			if (candidate != null) {
				return candidate;
			}
			// col
			candidate = findHiddenSingleInList(sudoku.getBoxCellsByCell(cell));
			if (candidate != null) {
				return candidate;
			}
		}
		return candidate;
	}

	/*
	 * Returns a cell that has a certain value. Null if no such cell exists
	 */
	public Pair<SudokuCell, Integer> bestMove() {
		Pair<SudokuCell, Integer> nakedSingle = findNakedSingles();
		if (nakedSingle != null && nakedSingle.getKey().getPossibleValues().size() == 1) {
			return nakedSingle;
		}
		System.out.println("Searching for hidden singles");
		Pair<SudokuCell, Integer> hiddenSingle = findHiddenSingles();
		if (hiddenSingle != null) {
			System.out.println("Found a hidden single:");
			System.out.println(hiddenSingle);
			return hiddenSingle;
		}
		return null;
	}

	/*
	 * Returns the cell currently believed to be the best possible move. The best
	 * cell may have multiple possibilities
	 */
	public Pair<SudokuCell, Integer> bestMoveBiforcation() {
		// Search for the cell with the least possible values
		Pair<SudokuCell, Integer> bestOption = findNakedSingles();
		return bestOption;
	}

	/*
	 * Brute-force search of the grid for the cell with the least possible values
	 */
	public SudokuCell bestCell(int maxVal) {
		SudokuCell best = null;
		for (SudokuCell c : sudoku.cells) {
			if (c.getVal() == 0 && c.getPossibleValues().size() <= maxVal) {
				best = c;
				maxVal = c.getPossibleValues().size();
			}
		}
		return best;
	}

}
