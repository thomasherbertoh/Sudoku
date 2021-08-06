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

public class Sudoku extends GridPane implements EventHandler<KeyEvent> {

	private List<List<SudokuCell>> boxes = new ArrayList<List<SudokuCell>>();
	
	public List<SudokuCell> cells = new ArrayList<SudokuCell>();

	private static SudokuCell activeCell;

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

				// Creating a new cell with the sudoku containing it, it's value, it's box, it's column, and it's row passed as parameters
				SudokuCell cell = new SudokuCell(this, val, box, i, j, false);

				// Adding the cell to it's box and the list of all cells in the sudoku
				boxes.get(box).add(cell);
				cells.add(cell);

				// Adding the cell to the sudoku itself and center it in it's section of the GridPane used for the sudoku
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
			Sudoku.activeCell.edit_notes = false;
			Sudoku.activeCell.active = false;
		}
		Sudoku.activeCell = active_cell;
		active_cell.active = true;
	}

	@Override
	public void handle(KeyEvent event) {
		try {
			int new_val = Integer.parseInt(event.getText());
			activeCell.updateVal(new_val);
		} catch (NumberFormatException ex) {
			System.out.println(ex);
		}
	}

	/*
	 * Fetches the list of values of cells in the same row as the cell passed as a parameter
	 */
	public List<Integer> getRowValues(SudokuCell cell) {
		List<Integer> row = new ArrayList<Integer>();
		for (SudokuCell temp_cell : this.cells) {
			if (temp_cell.row == cell.row) {
				row.add(temp_cell.getVal());
			}
		}
		return row;
	}

	/*
	 * Fetches the list of values of cells in the same column as the cell passed as a parameter
	 */
	public List<Integer> getColValues(SudokuCell cell) {
		List<Integer> col = new ArrayList<Integer>();
		for (SudokuCell temp_cell : this.cells) {
			if (temp_cell.col == cell.col) {
				col.add(temp_cell.getVal());
			}
		}
		return col;
	}

	/*
	 * Fetches the list of values of cells in the same box as the cell passed as a parameter
	 */
	public List<Integer> getBoxValues(SudokuCell cell) {
		List<SudokuCell> box = boxes.get(cell.getBoxID());
		List<Integer> ret_box = new ArrayList<Integer>();
		for (SudokuCell c : box) {
			ret_box.add(c.getVal());
		}
		return ret_box;
	}
}
