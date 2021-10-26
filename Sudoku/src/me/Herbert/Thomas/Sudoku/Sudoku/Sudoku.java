package src.me.Herbert.Thomas.Sudoku.Sudoku;

import java.util.ArrayList;
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
				if (this.autoFill) {
					Solver solver = new Solver(this, false);
					Pair<SudokuCell, Integer> best = solver.bestMove();

					while (best != null && best.getKey() != null) {
						best.getKey().updateVal(best.getValue());
						best = solver.bestMove();
					}

				}
			}
		} catch (NumberFormatException ex) {
			System.out.println(ex);
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
