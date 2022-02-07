package src.me.Herbert.Thomas.Sudoku.Sudoku;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import src.me.Herbert.Thomas.Sudoku.Solver.Solver;

public class Sudoku extends GridPane implements EventHandler<KeyEvent> {

	public List<List<SudokuCell>> boxes = new ArrayList<List<SudokuCell>>();

	public List<SudokuCell> cells = new ArrayList<SudokuCell>();

	private static SudokuCell activeCell;

	private boolean autoNotes = false;

	private boolean autoFill = false;

	public Sudoku() {
		super();

		// Instantiate boxes
		for (int i = 0; i < 9; i++) {
			boxes.add(new ArrayList<SudokuCell>());
		}

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {

				int val = 0;

				// Seems weird but uses integer division to work
				int box = (i / 3) + 3 * (j / 3);

				// Creating a new cell with the sudoku containing it, it's value, it's box, it's
				// column, and it's row passed as parameters
				SudokuCell cell = new SudokuCell(this, val, box, i, j, false);

				// Adding the cell to it's box and the list of all cells in the sudoku
				boxes.get(box).add(cell);
				cells.add(cell);

				// Adding the cell to the sudoku itself and center it in it's section of the
				// GridPane used for the sudoku
				this.add(cell, i, j);
				cell.setAlignment(Pos.CENTER);

				// Determining which borders of the cell need to be thick
				calcBorders(cell, i, j);
			}
		}
		this.setGridLinesVisible(true);

		this.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				try {
					int new_val = Integer.parseInt(event.getText());
					activeCell.updateVal(new_val);
				} catch (NumberFormatException ex) {
					System.out.println(ex);
				}
			}

		});
	}

	public void calcBorders(SudokuCell cell, int col, int row) {
		boolean top = false;
		boolean right = false;
		boolean bottom = false;
		boolean left = false;
		if (row == 0 || row == 3 || row == 6) {
			top = true;
		} else if (row == 8) {
			bottom = true;
		}
		if (col == 0 || col == 3 || col == 6) {
			left = true;
		} else if (col == 8) {
			right = true;
		}
		// Setting the cell's thick borders
		if (top && left) {
			cell.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
					BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
					CornerRadii.EMPTY, new BorderWidths(2.5), Insets.EMPTY)));
		} else if (top && right) {
			cell.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
					BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
					CornerRadii.EMPTY, new BorderWidths(2.5), Insets.EMPTY)));
		} else if (bottom && left) {
			cell.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
					BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID,
					CornerRadii.EMPTY, new BorderWidths(2.5), Insets.EMPTY)));
		} else if (bottom && right) {
			cell.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
					BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
					CornerRadii.EMPTY, new BorderWidths(2.5), Insets.EMPTY)));
		} else if (top) {
			cell.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
					BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
					CornerRadii.EMPTY, new BorderWidths(2.5), Insets.EMPTY)));
		} else if (right) {
			cell.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
					BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE,
					CornerRadii.EMPTY, new BorderWidths(2.5), Insets.EMPTY)));
		} else if (bottom) {
			cell.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
					BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE,
					CornerRadii.EMPTY, new BorderWidths(2.5), Insets.EMPTY)));
		} else if (left) {
			cell.setBorder(new Border(new BorderStroke(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK,
					BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID,
					CornerRadii.EMPTY, new BorderWidths(2.5), Insets.EMPTY)));
		}
	}

	public static void setActiveCell(SudokuCell active_cell) {
		// Setting up which cell will take keyboard inputs
		if (Sudoku.activeCell != null) {
			Sudoku.activeCell.setBackground(null);
			Sudoku.activeCell.editNotes = false;
			Sudoku.activeCell.active = false;
		}
		Sudoku.activeCell = active_cell;
		active_cell.active = true;
	}

	@Override
	public void handle(KeyEvent event) {
		try {
			int newVal = Integer.parseInt(event.getText());
			// user tries to enter digits before ever clicking a cell
			if (activeCell == null) {
				return;
			}
			if (activeCell.editNotes) {
				activeCell.updateUserPoss(newVal);
			} else {
				activeCell.updateVal(newVal);
				nakedPairs();
				pointingPairs();
				boxLineReduction();
				if (this.autoFill) {
					Solver solver = new Solver(this, false);
					Pair<SudokuCell, Integer> best = solver.bestMove();
					while (best != null && best.getKey() != null) {
						best.getKey().updateVal(best.getValue());
						nakedPairs();
						pointingPairs();
						boxLineReduction();
						best = solver.bestMove();
					}
				}
			}
		} catch (NumberFormatException ex) {
			System.out.println(ex);
		}
	}

	public void nakedPairs() {
		/*
		 * if there are two cells in a box/row/column that contain the same two options
		 * then no other cells in that box/row/column can contain those values
		 */

		// make array of collections of cells
		List<List<List<SudokuCell>>> collections = new ArrayList<>();

		// add boxes
		collections.add(this.boxes);

		// add rows
		List<List<SudokuCell>> rows = new ArrayList<>();
		for (int i = 0; i < 9; i++)
			rows.add(this.getRowCellsByRowID(i));
		collections.add(rows);

		// add columns
		List<List<SudokuCell>> cols = new ArrayList<>();
		for (int i = 0; i < 9; i++)
			cols.add(this.getColCellsByColID(i));
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
			for (List<List<SudokuCell>> collection : collections) {
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
			}
		} while (changed);
	}

	public void pointingPairs() {
		/*
		 * if all the occurrences of a note are in the same row/column then no other
		 * cells in that row/column can contain that number
		 */
		int occurrences, row, col, work_with_rows;
		SudokuCell exampleCell;
		for (List<SudokuCell> box : this.boxes)
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
					for (SudokuCell c : getRowCellsByCell(exampleCell)) {
						// if it's in the same box as the original cell we don't want to touch it
						if (c.getVal() != 0 || c.getBoxID() == exampleCell.getBoxID())
							continue;

						// if it contains a note with the current digit we remove it
						if (c.getPossibleValues().contains(i))
							c.updateCompPoss(i);
					}
				else
					for (SudokuCell c : getColCellsByCell(exampleCell)) {
						if (c.getVal() != 0 || c.getBoxID() == exampleCell.getBoxID())
							continue;

						if (c.getPossibleValues().contains(i))
							c.updateCompPoss(i);
					}
			}
	}

	public void boxLineReduction() {
		/*
		 * For every row and column, if all the possible locations for a digit lie in
		 * the same box, remove other instances of that digit from the notes in that box
		 */
		for (int iter = 0; iter < 2; iter++) {

			List<List<SudokuCell>> lists = new ArrayList<>();
			for (int i = 0; i < 9; i++) {
				if (iter == 0)
					lists.add(this.getRowCellsByRowID(i));
				else
					lists.add(this.getColCellsByColID(i));
			}

			// for every row
			for (List<SudokuCell> list : lists) {
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

					List<SudokuCell> b = this.getBoxCellsByBoxID(box_of_interest);
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

	/*
	 * Fetches the list of values of cells in the same row as the cell passed as a
	 * parameter
	 */
	public List<Integer> getRowValuesByCell(SudokuCell cell) {
		List<Integer> row = new ArrayList<Integer>();
		for (int i = 0; i < 9; i++) {
			try {
				row.add(this.cells.get((9 * i) + cell.row).getVal());
			} catch (IndexOutOfBoundsException ex) {
				// System.err.println("cell doesn't exist yet");
			}
		}
		return row;
	}

	/*
	 * Fetches the list of cells in the same row as the cell passed as a parameter
	 */
	public List<SudokuCell> getRowCellsByCell(SudokuCell cell) {
		List<SudokuCell> row = new ArrayList<SudokuCell>();
		for (int i = 0; i < 9; i++) {
			try {
				row.add(this.cells.get((9 * i) + cell.row));
			} catch (IndexOutOfBoundsException ex) {
				// System.err.println("cell doesn't exist yet");
			}
		}
		return row;
	}

	/*
	 * Fetches the list of cells in the specified row
	 */
	public List<SudokuCell> getRowCellsByRowID(int rowID) {
		List<SudokuCell> row = new ArrayList<SudokuCell>();
		for (int i = 0; i < 9; i++) {
			row.add(this.cells.get((9 * i) + rowID));
		}
		return row;
	}

	/*
	 * Fetches the list of values of cells in the same column as the cell passed as
	 * a parameter
	 */
	public List<Integer> getColValuesByCell(SudokuCell cell) {
		List<Integer> col = new ArrayList<Integer>();
		for (int i = cell.col * 9; i < (cell.col * 9) + 9; i++) {
			try {
				col.add(this.cells.get(i).getVal());
			} catch (IndexOutOfBoundsException ex) {
				// System.err.println("cell doesn't exist yet");
			}
		}
		return col;
	}

	/*
	 * Fetches the list of cells in the same column as the cell passed as a
	 * parameter
	 */
	public List<SudokuCell> getColCellsByCell(SudokuCell cell) {
		List<SudokuCell> col = new ArrayList<SudokuCell>();
		for (int i = cell.col * 9; i < (cell.col * 9) + 9; i++) {
			try {
				col.add(this.cells.get(i));
			} catch (IndexOutOfBoundsException ex) {
				// System.err.println("cell doesn't exist yet");
			}
		}
		return col;
	}

	/*
	 * Fetches the list of cells in the specified column
	 */
	public List<SudokuCell> getColCellsByColID(int colID) {
		List<SudokuCell> col = new ArrayList<SudokuCell>();
		for (int i = colID * 9; i < (colID * 9) + 9; i++) {
			col.add(this.cells.get(i));
		}
		return col;
	}

	/*
	 * Fetches the list of values of cells in the same box as the cell passed as a
	 * parameter
	 */
	public List<Integer> getBoxValuesByCell(SudokuCell cell) {
		List<SudokuCell> box = boxes.get(cell.getBoxID());
		List<Integer> ret_box = new ArrayList<Integer>();
		for (SudokuCell c : box) {
			ret_box.add(c.getVal());
		}
		return ret_box;
	}

	/*
	 * Fetches the list of cells in the same box as the cell passed as a parameter
	 */
	public List<SudokuCell> getBoxCellsByCell(SudokuCell cell) {
		return boxes.get(cell.getBoxID());
	}

	/*
	 * Fetches the list of cells in the specified box
	 */
	public List<SudokuCell> getBoxCellsByBoxID(int boxID) {
		return boxes.get(boxID);
	}

	public SudokuCell getCellAt(int col, int row) {
		return cells.get((col * 9) + row);
	}

	public boolean getAutoNotes() {
		return autoNotes;
	}

	public void setAutoNotes(boolean autoNotes) {
		this.autoNotes = autoNotes;
	}

	public boolean getAutoFill() {
		return autoFill;
	}

	public void setAutoFill(boolean autoFill) {
		this.autoFill = autoFill;
	}
}
