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
import src.me.Herbert.Thomas.Sudoku.Sudoku.Sudoku;

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

		buttons.getChildren().addAll(check, reset);

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