package src.me.Herbert.Thomas.Sudoku.Sudoku;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class SudokuCell extends Label {

	private Sudoku sudoku;
	private SudokuCell cell = this;
	private int val;
	private int box;
	private boolean show_notes;

	public int col;
	public int row;
	public boolean edit_notes = false;
	public boolean active = false;

	
	public SudokuCell(Sudoku sudoku, int val, int box, int column, int row, boolean show_poss) {
		super();
		
		this.sudoku = sudoku;
		this.val = val;
		this.box = box;
		this.col = column;
		this.row = row;
		this.show_notes = show_poss;

		this.setText(String.format("%d", this.val));

		this.setPrefHeight(100);
		this.setPrefWidth(100);
		this.setTextAlignment(TextAlignment.CENTER);
		Font font = new Font("calibri", 70);
		this.setFont(font);

		updateVal(val);

		this.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				// When this cell gets clicked, set it to be the active cell of it's sudoku
				cell.setBackground(new Background(new BackgroundFill(Color.GREY, new CornerRadii(5.0), new Insets(-5.0))));
				Sudoku.setActiveCell(cell);
				if (event.isShiftDown()) {
					edit_notes = true;
					show_notes = true;
					cell.setBackground(new Background(new BackgroundFill(Color.DARKGREY, new CornerRadii(5.0), new Insets(-5.0))));
					// cell.drawPoss();
				}
			}

		});
	}

	public void updateVal(int val) {
		cell.setText("");
		cell.setFont(new Font("calibri", 70));
		cell.val = val;
		cell.show_notes = false;
		if (cell.val != 0) {
			cell.setText(Integer.toString(cell.val));
		} else {
			cell.setText("");
		}
		this.setBackground(null);
	}

	public int getVal() {
		return this.val;
	}

	public int getBoxID() {
		return this.box;
	}

}
