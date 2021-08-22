package src.me.Herbert.Thomas.Sudoku.Main;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.me.Herbert.Thomas.Sudoku.Checker.Checker;
import src.me.Herbert.Thomas.Sudoku.Solver.Solver;
import src.me.Herbert.Thomas.Sudoku.Sudoku.Sudoku;
import src.me.Herbert.Thomas.Sudoku.Sudoku.SudokuCell;

public class Main extends Application {

	Sudoku sudoku = new Sudoku();

	public void start(Stage primaryStage) {

		primaryStage.setTitle("Sudoku");

		HBox root = new HBox();

		VBox buttons = new VBox();

		Button check = new Button();
		check.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				System.out.println("Checking the grid");
				Checker checker = new Checker(sudoku);
				if (checker.isValid()) {
					// Pop-up message to tell the user the grid is correct
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("SUCCESS!");
					alert.setHeaderText("Well Done!");
					alert.setContentText("The sudoku is correct! Congratulations!");
					alert.show();
					System.out.println("The sudoku is valid!");
				} else {
					// Pop-up message to tell the user the grid is incorrect
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("ERROR!");
					alert.setHeaderText("Oh no!");
					alert.setContentText("The sudoku is wrong :(");
					alert.show();
					System.out.println("The sudoku is NOT valid :(");
				}
			}

		});
		check.setText("Check grid");

		Button solve = new Button();
		solve.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				// Solving the grid
				long start = System.nanoTime();
				Solver solver = new Solver(sudoku, true);
				long end = System.nanoTime();
				System.out.println(solver.rows);
				System.out.println(solver.cols);
				System.out.println(solver.boxes);
				System.out.println(String.format("That took %d nanoseconds!", end - start));
				System.out.println("That's " + (end - start) / 1000000000.0 + " seconds");
			}

		});
		solve.setText("Solve grid");

		Button reset = new Button();
		reset.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				System.out.println("Resetting the grid");
				root.getChildren().remove(sudoku);
				sudoku = new Sudoku();
				root.getChildren().add(sudoku);
				root.getChildren().remove(buttons);
				root.getChildren().add(buttons);
			}

		});
		reset.setText("Reset grid");

		Button autoNotes = new Button();
		autoNotes.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				System.out.println("Toggling auto-notes");
				sudoku.autoNotes = !sudoku.autoNotes;
				for (SudokuCell c : sudoku.cells) {
					c.showNotes = sudoku.autoNotes;
					if (c.showNotes) {
						c.drawPoss();
					} else {
						c.setText("");
					}
				}
				if (sudoku.autoNotes) {
					autoNotes.setText("Auto-notes: Enabled");
				} else {
					autoNotes.setText("Auto-notes: Disabled");
				}
			}

		});
		autoNotes.setText("Auto-notes: Disabled");

<<<<<<< HEAD
		buttons.getChildren().addAll(check, solve, reset, autoNotes);
=======
		Button nextMove = new Button();
		nextMove.setOnMouseClicked(new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
				System.out.println("Finding next move");
				Solver solver = new Solver(sudoku, false);
				SudokuCell nextCell = solver.bestMove();
				nextCell.setBackground(
						new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(5.0), new Insets(-5.0))));
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Here's a hint!");
				alert.setHeaderText("Hint:");
				if (nextCell.getPossibleValues().size() == 1) {
					alert.setContentText(String.format("%d is the only option for the yellow cell!",
							nextCell.getPossibleValues().get(0)));
				} else {
					alert.setContentText(String.format("There are only %d possible values for the yellow cell!",
							nextCell.getPossibleValues().size()));
				}
				alert.show();
			}

		});
		nextMove.setText("What next?");

		buttons.getChildren().addAll(check, solve, reset, nextMove, autoNotes);
>>>>>>> parent of 22d5507... Removed redundant code, improved formatting, added auto-fill feature to aid in 'manual' solving of the sudoku

		root.getChildren().addAll(sudoku, buttons);

		Scene scene = new Scene(root, 1030, 900);
		scene.setOnKeyPressed(sudoku);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}