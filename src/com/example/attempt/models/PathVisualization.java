package com.example.attempt.models;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.example.attempt.qlearning.QLearning;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

/**
 * 
 * @author deepika
 *
 */

public class PathVisualization extends Application {

	private static long seed1 = 97531;
	private static long seed2 = 86420;

	Random r1 = new Random(seed1);
	Random r2 = new Random(seed2);

	Stage window;
	Scene scene;
	Button button;

	String experiment;
	
	/**
	 * This method has logic to display the User Input window to 
	 * choose the experiment.
	 * 
	 */
	

	@Override
	public void start(Stage primaryStage) throws Exception {

		PrintStream printStream = new PrintStream(new FileOutputStream("src/output_rl.txt"));
		System.setOut(printStream);

		window = primaryStage;
		window.setTitle("Q Learning and SARSA");

		CheckBox box1 = new CheckBox("Experiment 1");
		CheckBox box2 = new CheckBox("Experiment 2");
		CheckBox box3 = new CheckBox("Experiment3");
		
		box1.setSelected(true);


		button = new Button("START");
		button.setMaxSize(100, 100);
		button.setTextFill(Paint.valueOf("Green"));
		button.setAlignment(Pos.BASELINE_CENTER);
		button.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				runExperiment(box1, box2, box3);
			}
		});

		// Layout
		VBox layout = new VBox(10);
		layout.setPadding(new Insets(20, 20, 20, 20));
		layout.getChildren().addAll(box1, box2, box3, button);

		// set Stage boundaries to visible bounds of the main screen
		window.setX(100);
		window.setY(100);
		window.setWidth(500);
		window.setHeight(500);

		scene = new Scene(layout, 300, 250);
		window.setScene(scene);
		window.show();

	}

	/**
	 * 
	 * This method has logic to run a particular experiment based on the user input. 
	 * It passes the results obtained visualization function in order to display the 
	 * Q table and path visualization
	 * 
	 * @param box1
	 * @param box2
	 * @param box3
	 */
	public void runExperiment(CheckBox box1, CheckBox box2, CheckBox box3) {

		HashMap<Integer, Character> operators = new HashMap<>();

		operators.put(1, 'S');
		operators.put(2, 'N');
		operators.put(3, 'E');
		operators.put(4, 'W');
		operators.put(5, 'P');
		operators.put(6, 'D');

		if (box1.isSelected()) {
			// Experiment 1 Execution 1
			double qtableFirstExp1[][][][] = QLearning.initialize();
			Stage firstStage = new Stage();
			qtableFirstExp1 = QLearning.runExperiment1(qtableFirstExp1, r1);
			firstStage.setTitle("Experiment 1- Execution 1 Q Learning Path Visualization");
			pathVisualization(qtableFirstExp1, operators, firstStage);

			// Experiment 1 Execution 2
			double qtableFirstExp2[][][][] = QLearning.initialize();
			Stage secondStage = new Stage();
			secondStage.setTitle("Experiment 1- Execution 2 Q Learning Path Visualization");
			qtableFirstExp2 = QLearning.runExperiment1(qtableFirstExp2, r2);
			pathVisualization(qtableFirstExp2, operators, secondStage);

		} else if (box2.isSelected()) {
			// Experiment 2 Execution 1
			double qtableSecondExp1[][][][] = QLearning.initialize();
			Stage thirdStage = new Stage();
			qtableSecondExp1 = QLearning.runExperiment2(qtableSecondExp1, r1);
			thirdStage.setTitle("Experiment 2- Execution 1 Q Learning Path Visualization");
			pathVisualization(qtableSecondExp1, operators, thirdStage);

			// Experiment 2 Execution 2
			double qtableSecondExp2[][][][] = QLearning.initialize();
			Stage fourthStage = new Stage();
			fourthStage.setTitle("Experiment 2- Execution 2 Q Learning Path Visualization");
			qtableSecondExp2 = QLearning.runExperiment2(qtableSecondExp2, r2);
			pathVisualization(qtableSecondExp2, operators, fourthStage);

		} else if (box3.isSelected()) {

			// Experiment 3 Execution 1
			double qtableThirdExp1[][][][] = QLearning.initialize();
			Stage fifthStage = new Stage();
			qtableThirdExp1 = QLearning.runExperiment3(qtableThirdExp1, r1);
			fifthStage.setTitle("Experiment 3- Execution 1 Q Learning Path Visualization");
			pathVisualization(qtableThirdExp1, operators, fifthStage);

			// Experiment 3 Execution 2
			double qtableThirdExp2[][][][] = QLearning.initialize();
			Stage sixthStage = new Stage();
			sixthStage.setTitle("Experiment 3- Execution 2 Q Learning Path Visualization");
			qtableThirdExp2 = QLearning.runExperiment3(qtableThirdExp2, r2);
			pathVisualization(qtableThirdExp2, operators, sixthStage);
		}
	}

	/**
	 * 
	 * This method has the logic to display the attractive paths and Q table in 
	 * grid format. 
	 * 
	 * @param qtable
	 * @param operators
	 * @param primaryStage
	 */
	public void pathVisualization(double qtable[][][][], HashMap<Integer, Character> operators, Stage primaryStage) {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setGridLinesVisible(true);
		grid.setMaxSize(2000, 2000);
		grid.setPadding(new Insets(10, 10, 10, 10));

		// Stage stage = new Stage();

		Scene scene = new Scene(grid, 500, 500);
		primaryStage.setScene(scene);

		String upArrow = "\u2191";
		String downArrow = "\u2193";
		String leftArrow = "\u2190";
		String rightArrow = "\u2192";

		int r = 0;
		int i = 0;

		for (int x = 0; x <= 1; x++) {
			for (int l = 1; l <= 5; l++) {
				for (int m = 1; m <= 5; m++) {
					int c = i % 5;
					List<Character> paths = attractivePathDirections(qtable, l, m, x, operators);
					List<Double> stateQValues = stateQValues(qtable, l, m, x);

					Text arrows = null;
					Text qvalues = null;

					qvalues = new Text("\t" + stateQValues.get(1) + "\n" + stateQValues.get(3) + "\t\t "
							+ stateQValues.get(2) + "\n" + stateQValues.get(0));

					if (paths.size() == 1) {
						switch (paths.get(0)) {
						case 'N':
							arrows = new Text(upArrow);
							break;
						case 'E':
							arrows = new Text(rightArrow);
							break;
						case 'W':
							arrows = new Text(leftArrow);
							break;
						case 'S':
							arrows = new Text(downArrow);
							break;
						}
					} else if (paths.size() == 2) {
						if (paths.contains('N') && paths.contains('E')) {
							arrows = new Text(upArrow + "\n" + rightArrow);
						} else if (paths.contains('N') && paths.contains('W')) {
							arrows = new Text(upArrow + "\n" + leftArrow);
						} else if (paths.contains('N') && paths.contains('S')) {
							arrows = new Text(upArrow + "\n" + downArrow);
						} else if (paths.contains('S') && paths.contains('E')) {
							arrows = new Text(rightArrow + "\n" + downArrow);
						} else if (paths.contains('S') && paths.contains('W')) {
							arrows = new Text(leftArrow + "\n" + downArrow);
						} else if (paths.contains('E') && paths.contains('W')) {
							arrows = new Text(leftArrow + rightArrow);
						}
					} else if (paths.size() == 3) {
						if (paths.contains('N') && paths.contains('S') && paths.contains('E')) {
							arrows = new Text(upArrow + "\n" + "   " + rightArrow + "\n" + downArrow);
						} else if (paths.contains('N') && paths.contains('S') && paths.contains('W')) {
							arrows = new Text(upArrow + "\n" + leftArrow + "\n" + downArrow);
						} else if (paths.contains('N') && paths.contains('E') && paths.contains('W')) {
							arrows = new Text(upArrow + "\n" + leftArrow + rightArrow);
						} else if (paths.contains('S') && paths.contains('E') && paths.contains('W')) {
							arrows = new Text(leftArrow + rightArrow + "\n" + downArrow);
						}
					} else {
						arrows = new Text(" ");

					}
					// Text arrows = new Text("\u2191\n\u2190 \u2192\n\u2193");

					if (r == 0) {
						grid.add(new Text(" States"), 0, r);
						grid.add(new Text("  with "), 1, r);
						grid.add(new Text("  x = "), 2, r);
						grid.add(new Text(" 0    "), 3, r);
						grid.add(new Text("      "), 4, r);
						r++;
					}
					if (r == 6) {
						grid.add(new Text(" States"), 0, r);
						grid.add(new Text(" with "), 1, r);
						grid.add(new Text("  x = "), 2, r);
						grid.add(new Text(" 1    "), 3, r);
						grid.add(new Text("      "), 4, r);
						r++;
					}
					arrows.setFont(Font.font("Tahoma", FontWeight.NORMAL, 16));
					arrows.setTextAlignment(TextAlignment.CENTER);
					qvalues.setTextAlignment(TextAlignment.CENTER);
					// qvalues.setFont(Font.font("Calibri",FontWeight.NORMAL,16));
					grid.add(arrows, c, r);
					grid.add(qvalues, c + 5, r);

					if (c == 4)
						r++;

					if (i <= 50) {
						i++;
					}

				}
			}
		}
		primaryStage.show();
	}
	
	/**
	 * This method gets all the applicable move operators for a state.
	 * 
	 * @param i
	 * @param j
	 * @return
	 */

	public List<Integer> applicableMoveOperators(int i, int j) {
		List<Integer> appOp = new ArrayList<>();
		// MS
		if (QLearning.isInMatrix(i + 1, j)) {
			appOp.add(1);
		}
		// MN
		if (QLearning.isInMatrix(i - 1, j)) {
			appOp.add(2);
		}
		// ME
		if (QLearning.isInMatrix(i, j + 1)) {
			appOp.add(3);
		}
		// MW
		if (QLearning.isInMatrix(i, j - 1)) {
			appOp.add(4);
		}
		return appOp;
	}

	
	/**
	 * This method has logic to find the attractive paths. 
	 * 
	 * @param qtable
	 * @param l
	 * @param m
	 * @param x
	 * @param operators
	 * @return
	 */
	public List<Character> attractivePathDirections(double qtable[][][][], int l, int m, int x,
			HashMap<Integer, Character> operators) {

		List<Integer> appOp = new ArrayList<>();
		appOp = applicableMoveOperators(l, m);

		List<Character> qValuesEqual = new ArrayList<>();

		int i = 0;
		double maxqValue = qtable[l][m][x][appOp.get(i)];

		// finding maxq value for that particular state
		for (i = 1; i < appOp.size(); i++) {
			if (qtable[l][m][x][appOp.get(i)] > maxqValue) {
				maxqValue = qtable[l][m][x][appOp.get(i)];
			}
		}
		// if different actions have same maxq value adding to list
		for (int k = 0; k < appOp.size(); k++) {
			if (qtable[l][m][x][appOp.get(k)] == maxqValue) {
				qValuesEqual.add(operators.get(appOp.get(k)));
			}
		}
		return qValuesEqual;
	}

	/**
	 * 
	 * This method has logic to find the Q values for every state in order to 
	 * display them in the Q table grid. 
	 * 
	 * @param qtable
	 * @param i
	 * @param j
	 * @param x
	 * @return
	 */
	public List<Double> stateQValues(double qtable[][][][], int i, int j, int x) {
		List<Double> qValues = new ArrayList<>();
		for (int op = 1; op <= 4; op++) {
			qValues.add(Math.round(qtable[i][j][x][op] * 100.0) / 100.0);
		}
		return qValues;

	}

	public static void main(String args[]) {
		launch(args);
	}
}
