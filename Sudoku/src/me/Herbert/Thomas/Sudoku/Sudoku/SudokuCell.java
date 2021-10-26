package src.me.Herbert.Thomas.Sudoku.Sudoku;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	private List<Integer> compPossibleValues;
	private List<Integer> userPossibleValues;

	public int col;
	public int row;
	public boolean showNotes;
	public boolean editNotes = false;
	public boolean active = false;

	public SudokuCell(Sudoku sudoku, int val, int box, int column, int row, boolean show_poss) {
		super();

		this.sudoku = sudoku;
		this.val = val;
		this.box = box;
		this.col = column;
		this.row = row;
		this.showNotes = show_poss;
		if (this.val == 0) {
			this.compPossibleValues = updatePossibleValues();
		}
		userPossibleValues = new ArrayList<Integer>();

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
				cell.setBackground(
						new Background(new BackgroundFill(Color.GREY, new CornerRadii(5.0), new Insets(-5.0))));
				Sudoku.setActiveCell(cell);
				if (event.isShiftDown()) {
					editNotes = true;
					showNotes = true;
					cell.setBackground(
							new Background(new BackgroundFill(Color.DARKGREY, new CornerRadii(5.0), new Insets(-5.0))));
					cell.drawPoss();
				}
			}

		});
	}

	public void updateVal(int val) {
		cell.setText("");
		cell.setFont(new Font("calibri", 70));
		cell.val = val;
		cell.showNotes = false;

		if (cell.val != 0) {
			cell.setText(Integer.toString(cell.val));
		} else {
			cell.setText("");
		}

		for (SudokuCell c : this.sudoku.getRowCellsByCell(this)) {
			c.compPossibleValues = c.updatePossibleValues();
			if (c.showNotes && c.val == 0) {
				c.drawPoss();
			}
		}
		for (SudokuCell c : this.sudoku.getColCellsByCell(this)) {
			c.compPossibleValues = c.updatePossibleValues();
			if (c.showNotes && c.val == 0) {
				c.drawPoss();
			}
		}
		for (SudokuCell c : this.sudoku.getBoxCellsByCell(this)) {
			c.compPossibleValues = c.updatePossibleValues();
			if (c.showNotes && c.val == 0) {
				c.drawPoss();
			}
		}

		if (cell.showNotes && cell.val == 0) {
			drawPoss();
		}

		this.setBackground(null);
	}

	public int getVal() {
		return this.val;
	}

	public int getBoxID() {
		return this.box;
	}

	public List<Integer> updatePossibleValues() {
		List<Integer> row = this.sudoku.getRowValuesByCell(this);
		List<Integer> col = this.sudoku.getColValuesByCell(this);
		List<Integer> box = this.sudoku.getBoxValuesByCell(this);

		List<Integer> out = new ArrayList<Integer>();
		for (int i = 1; i < 10; i++) {
			out.add(i);
		}

		out = removeImpossibleValues(out, row);
		out = removeImpossibleValues(out, col);
		out = removeImpossibleValues(out, box);
		return out;
	}

	public List<Integer> removeImpossibleValues(List<Integer> toEmpty, List<Integer> master) {
		Iterator<Integer> iter = toEmpty.iterator();
		int next;
		while (iter.hasNext()) {
			next = iter.next();
			if (master.contains(next)) {
				iter.remove();
			}
		}
		return toEmpty;
	}

	public void drawPoss() {
		this.setFont(new Font("calibri", 15));
		String poss = "";

		// Iterate over possible values of this cell, avoid adding the final element of
		// the array for better formatting
		if (!sudoku.getAutoNotes()) {
			for (int i = 0; i < this.userPossibleValues.size() - 1; i++) {
				poss += Integer.toString(this.userPossibleValues.get(i));
				poss += ", ";
			}

			// Add the final element of the array
			if (this.userPossibleValues.size() != 0) {
				poss += Integer.toString(this.userPossibleValues.get(this.userPossibleValues.size() - 1));
			}
		} else {
			for (int i = 0; i < this.compPossibleValues.size() - 1; i++) {
				poss += Integer.toString(this.compPossibleValues.get(i));
				poss += ", ";
			}

			// Add the final element of the array
			if (this.compPossibleValues.size() != 0) {
				poss += Integer.toString(this.compPossibleValues.get(this.compPossibleValues.size() - 1));
			}
		}

		this.setText(poss);
	}

	public void updateCompPoss(int val) {
		if (this.compPossibleValues.contains(val)) {
			this.compPossibleValues.remove((Object) val);
		} else {
			this.compPossibleValues.add(val);
		}
	}

	public void updateUserPoss(int val) {
		if (this.userPossibleValues.contains(val)) {
			this.userPossibleValues.remove((Object) val);
		} else {
			this.userPossibleValues.add(val);
		}
		drawPoss();
	}

	public List<Integer> getPossibleValues() {
		return this.compPossibleValues;
	}

}
