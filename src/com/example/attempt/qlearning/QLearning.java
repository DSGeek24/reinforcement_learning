package com.example.attempt.qlearning;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

/**
 * 
 * @author deepika
 *
 */
public class QLearning {

	static double learning_rate = 0.3;
	static double discount_factor = 0.5;
	static int reward = 0;

	static int i = 0;
	static int j = 0;
	static int x = 0;
	static int op = 0;

	// bank account of agent
	static int totalRewardPRandom = 0;
	static int totalRewardPGreedy = 0;
	static int totalRewardPExploit = 0;

	static List<HashMap<Integer, Integer>> totalBankAccountPRandom = new ArrayList<>();
	static List<HashMap<Integer, Integer>> totalBankAccountPGreedy = new ArrayList<>();
	static List<HashMap<Integer, Integer>> totalBankAccountPExploit = new ArrayList<>();

	// delivered blocks
	static List<HashMap<Integer, Integer>> totalDeliveredBlocksPRandom = new ArrayList<>();
	static List<HashMap<Integer, Integer>> totalDeliveredBlocksPGreedy = new ArrayList<>();
	static List<HashMap<Integer, Integer>> totalDeliveredBlocksPExploit = new ArrayList<>();

	// steps to reach terminal state
	static List<Integer> terminalStateReachedNumber = new ArrayList<>();

	static int pickupBlocks[][] = new int[6][6];
	static int dropOffBlocks[][] = new int[6][6];
	static HashMap<Integer, Character> operators = new HashMap<>();

	static Boolean dropOffFilled = true;

	static StringBuilder sb = new StringBuilder();

	/**
	 * This method is used to initialize the PD World before every experiment
	 * 
	 * @return
	 * 
	 */
	public static double[][][][] initialize() {

		// initialize q table with all zeros
		double qtable[][][][] = new double[6][6][2][7];

		// create Hashmap consisting of operators assigned to numbers
		operators.put(1, 'S');
		operators.put(2, 'N');
		operators.put(3, 'E');
		operators.put(4, 'W');
		operators.put(5, 'P');
		operators.put(6, 'D');

		// initialize pickup and drop blocks in states

		for (int i = 0; i <= 6; i++) {
			for (j = 0; j <= 6; j++) {
				if ((i == 3 & j == 3) || (i == 1 & j == 1) || (i == 5 & j == 5) || (i == 4 & j == 1)) {
					pickupBlocks[i][j] = 4;
				} else if ((i == 4 & j == 4) || (i == 5 & j == 1)) {
					dropOffBlocks[i][j] = 0;
				}
			}
		}

		totalRewardPRandom = 0;
		totalRewardPGreedy = 0;
		totalRewardPExploit = 0;

		totalBankAccountPRandom = new ArrayList<>();
		totalBankAccountPGreedy = new ArrayList<>();
		totalBankAccountPExploit = new ArrayList<>();

		// delivered blocks
		totalDeliveredBlocksPRandom = new ArrayList<>();
		totalDeliveredBlocksPGreedy = new ArrayList<>();
		totalDeliveredBlocksPExploit = new ArrayList<>();

		// steps to reach terminal state
		terminalStateReachedNumber = new ArrayList<>();

		return qtable;
	}

	/**
	 * This function is used to run the Experiment 1 which runs PRandom for first
	 * 3000 steps and PGreedy for another 3000 steps. It returns q table after
	 * PRandom and PGreedy and also prints the performance measures of bank account
	 * and number of operators needed to reach terminal state.
	 * 
	 * @param qtable
	 * @param r
	 * @return
	 */

	public static double[][][][] runExperiment1(double qtable[][][][], Random r) {
		// initial values
		i = 1;
		j = 5;
		x = 0;
		op = 1;

		System.out.println("STARTED EXPERIMENT 1");
		System.out.println("\n");
		HashMap<double[][][][], List<Integer>> qTableWithStateInfo = new HashMap<>();
		List<Integer> stateInfo = new ArrayList<>();

		System.out.println("Following PRANDOM policy for 3000 steps");

		qTableWithStateInfo = pRandom(qtable, i, j, x, op, 0, 3000, "qLearning", r, "Experiment1");

		for (Entry<double[][][][], List<Integer>> output : qTableWithStateInfo.entrySet()) {
			qtable = output.getKey();
			stateInfo = output.getValue();
		}
		i = stateInfo.get(0);
		j = stateInfo.get(1);
		x = stateInfo.get(2);
		op = stateInfo.get(3);

		System.out.println("Q Table for Experiment 1 after PRANDOM");
		prettyPrint(qtable, "PRANDOM", 3000);

		System.out.println("Pickup blocks left after PRANDOM for Experiment1 "
				+ (pickupBlocks[3][3] + pickupBlocks[1][1] + pickupBlocks[5][5] + pickupBlocks[4][1]));
		System.out.println("Drop blocks after PRANDOM " + (dropOffBlocks[4][4] + dropOffBlocks[5][1]));
		System.out.println("Agent carrying " + x);

		// printing the performance measures for experiment 1
		printPerformanceMeasures(totalBankAccountPRandom, terminalStateReachedNumber, "Experiment1", "PRandom");

		System.out.println("Following PGREEDY policy for 3000 steps");

		qTableWithStateInfo = pGreedy(qtable, i, j, x, op, 3000, 6000, "qLearning", r);
		for (Entry<double[][][][], List<Integer>> output : qTableWithStateInfo.entrySet()) {
			qtable = output.getKey();
			stateInfo = output.getValue();
		}
		i = stateInfo.get(0);
		j = stateInfo.get(1);
		x = stateInfo.get(2);
		op = stateInfo.get(3);

		System.out.println("Q Table for Experiment 1 after PGREEDY");
		prettyPrint(qtable, "PGREEDY", 3000);

		System.out.println("Pickup blocks left after PGREEDY for Experiment1 "
				+ (pickupBlocks[3][3] + pickupBlocks[1][1] + pickupBlocks[5][5] + pickupBlocks[4][1]));
		System.out.println("Drop blocks after PGREEDY for Experiment1 " + (dropOffBlocks[4][4] + dropOffBlocks[5][1]));
		System.out.println("Agent carrying " + x);

		// printing the performance measures for experiment 1
		printPerformanceMeasures(totalBankAccountPGreedy, terminalStateReachedNumber, "Experiment1", "PGreedy");

		return qtable;

	}

	/**
	 * This function is used to run the Experiment 2 which runs PRandom for first
	 * 200 steps and PExploit for another 5800 steps. It returns q table after first
	 * drop off location is filled, terminal state is reached and end of the
	 * experiment.
	 * 
	 * It also prints the performance measures of bank account and number of
	 * operators needed to reach terminal state.
	 * 
	 * @param qtable
	 * @param r
	 * @return
	 */

	public static double[][][][] runExperiment2(double qtable[][][][], Random r) {

		System.out.println("STARTED EXPERIMENT 2");
		// initial values
		i = 1;
		j = 5;
		x = 0;
		op = 1;

		HashMap<double[][][][], List<Integer>> qTableWithStateInfo = new HashMap<>();
		List<Integer> stateInfo = new ArrayList<>();

		System.out.println("Following PRANDOM policy for 200 steps");

		qTableWithStateInfo = pRandom(qtable, i, j, x, op, 0, 200, "qLearning", r, "Experiment2");

		for (Entry<double[][][][], List<Integer>> output : qTableWithStateInfo.entrySet()) {
			qtable = output.getKey();
			stateInfo = output.getValue();
		}

		i = stateInfo.get(0);
		j = stateInfo.get(1);
		x = stateInfo.get(2);
		op = stateInfo.get(3);

		System.out.println("Q Table for Experiment 2 after PRANDOM");
		prettyPrint(qtable, "PRANDOM", 200);

		System.out.println("Pickup blocks left after PRANDOM for Experiment 2 "
				+ (pickupBlocks[3][3] + pickupBlocks[1][1] + pickupBlocks[5][5] + pickupBlocks[4][1]));
		System.out.println(
				"Drop blocks left after after PRANDOM for Experiment 2 " + (dropOffBlocks[4][4] + dropOffBlocks[5][1]));
		System.out.println("x is " + x);

		printPerformanceMeasures(totalBankAccountPRandom, terminalStateReachedNumber, "Experiment2", "PRandom");

		System.out.println("Following PEXPLOIT policy for 5800 steps");

		qTableWithStateInfo = pExploit(qtable, i, j, x, op, 200, 6000, "qLearning", r, "Experiment2");
		for (Entry<double[][][][], List<Integer>> output : qTableWithStateInfo.entrySet()) {
			qtable = output.getKey();
			stateInfo = output.getValue();
		}
		i = stateInfo.get(0);
		j = stateInfo.get(1);
		x = stateInfo.get(2);
		op = stateInfo.get(3);

		System.out.println("Q Table for Experiment 2 after PEXPLOIT");
		prettyPrint(qtable, "PEXPLOIT", 5800);

		System.out.println("Pickup blocks left after pExploit for Experiment 2 "
				+ (pickupBlocks[3][3] + pickupBlocks[1][1] + pickupBlocks[5][5] + pickupBlocks[4][1]));
		System.out
				.println("Drop blocks after pExploit for Experiment 2 " + (dropOffBlocks[4][4] + dropOffBlocks[5][1]));
		System.out.println("x is " + x);

		printPerformanceMeasures(totalBankAccountPExploit, terminalStateReachedNumber, "Experiment2", "PExploit");
		return qtable;
	}

	/**
	 * This function is used to run the Experiment 3 which runs PRandom for first
	 * 200 steps and PExploit for another 5800 steps. It returns Q table after
	 * PRandom and after PExploit.
	 * 
	 * @param qtable
	 * @param r
	 * @return
	 */
	public static double[][][][] runExperiment3(double qtable[][][][], Random r) {
		// initial values
		i = 1;
		j = 5;
		x = 0;
		op = 1;

		System.out.println("STARTED EXPERIMENT 3");

		HashMap<double[][][][], List<Integer>> qTableWithStateInfo = new HashMap<>();
		List<Integer> stateInfo = new ArrayList<>();

		System.out.println("Following PRANDOM policy for 200 steps");

		qTableWithStateInfo = pRandom(qtable, i, j, x, op, 0, 200, "sarsa", r, "Experiment3");

		for (Entry<double[][][][], List<Integer>> output : qTableWithStateInfo.entrySet()) {
			qtable = output.getKey();
			stateInfo = output.getValue();
		}

		i = stateInfo.get(0);
		j = stateInfo.get(1);
		x = stateInfo.get(2);
		op = stateInfo.get(3);

		System.out.println("Q Table for Experiment3 after PRANDOM");
		prettyPrint(qtable, "PRANDOM", 200);

		System.out.println("Pickup blocks left after PRANDOM for Experiment 3 "
				+ (pickupBlocks[3][3] + pickupBlocks[1][1] + pickupBlocks[5][5] + pickupBlocks[4][1]));
		System.out.println("Drop blocks after PRANDOM for Experiment 3 " + (dropOffBlocks[4][4] + dropOffBlocks[5][1]));
		System.out.println("x is " + x);

		printPerformanceMeasures(totalBankAccountPRandom, terminalStateReachedNumber, "Experiment3", "PRandom");

		System.out.println("Following PEXPLOIT policy for 5800 steps");

		qTableWithStateInfo = pExploit(qtable, i, j, x, op, 200, 6000, "sarsa", r, "Experiment3");
		for (Entry<double[][][][], List<Integer>> output : qTableWithStateInfo.entrySet()) {
			qtable = output.getKey();
			stateInfo = output.getValue();
		}

		i = stateInfo.get(0);
		j = stateInfo.get(1);
		x = stateInfo.get(2);
		op = stateInfo.get(3);

		System.out.println("Q Table for Experiment 3 after PEXPLOIT");
		prettyPrint(qtable, "PEXPLOIT", 5800);

		System.out.println("Pickup blocks left after pExploit for Experiment 3 "
				+ (pickupBlocks[3][3] + pickupBlocks[1][1] + pickupBlocks[5][5] + pickupBlocks[4][1]));
		System.out.println("Drop blocks after pExploit for Experiment 3 " + (dropOffBlocks[4][4] + dropOffBlocks[5][1]));
		System.out.println("x is " + x);

		printPerformanceMeasures(totalBankAccountPExploit, terminalStateReachedNumber, "Experiment3", "PExploit");
		return qtable;
	}

	/**
	 * 
	 * This method has functionality to determine next action to be taken by the
	 * agent as defined by PRANDOM policy.
	 * 
	 * 
	 * @return QTable along with present state of the agent
	 */
	public static HashMap<double[][][][], List<Integer>> pRandom(double qtable[][][][], int i, int j, int x, int op,
			int currentSteps, int totalSteps, String method, Random r, String experiment) {
		// initial steps
		HashMap<double[][][][], List<Integer>> qTableWithStateInfo = new HashMap<>();
		int steps = currentSteps;
		List<Integer> stateInfo = new ArrayList<>();
		int presentSteps = steps;
		while (steps < totalSteps) {

			// for Experiment 2 reporting q table for first drop off location filled
			if (dropOffFilled) {
				if (experiment.equalsIgnoreCase("Experiment2")) {
					if ((dropOffBlocks[4][4] == 8) | (dropOffBlocks[5][1] == 8)) {
						System.out.println("Qtable after first dropoff location is filled");
						prettyPrint(qtable, "PRandom", steps);
						dropOffFilled = false;
					}
				}
			}

			int increasedSteps = steps;
			List<Integer> appOp = new ArrayList<>();

			// checking if terminal state is reached
			if (terminalStateReached(dropOffBlocks[4][4] + dropOffBlocks[5][1])) {
				// totalReward of the agent is reset to zero if terminal state is reached
				totalRewardPRandom = 0;

				Integer terminalStateReached = steps;
				terminalStateReachedNumber.add(terminalStateReached);

				// for experiment 2 reporting q table when terminal state is reached
				if (experiment.equalsIgnoreCase("Experiment2")) {
					System.out.println("Experiment 2");
					System.out.println("Qtable after terminal state is reached");
					prettyPrint(qtable, "PRandom", steps);
				}

				List<Integer> initialState = new ArrayList<>();
				initialState = resetPDWorld(i, j, x, op);
				i = initialState.get(0);
				j = initialState.get(1);
				x = initialState.get(2);
				op = initialState.get(3);
			}

			// performance measure-bank account
			if ((increasedSteps - presentSteps) == 100) {
				bankAccount(steps, totalRewardPRandom, totalBankAccountPRandom);
				presentSteps = increasedSteps;
				HashMap<Integer, Integer> deliveredBlocks = new HashMap<>();
				deliveredBlocks.put(steps, dropOffBlocks[4][4] + dropOffBlocks[5][1]);
				totalDeliveredBlocksPRandom.add(deliveredBlocks);
			}

			// getting all the applicable operators for the present state
			appOp = applicableOperators(i, j, x, pickupBlocks, dropOffBlocks);

			// pickup action
			if (appOp.size() >= 1) {

				if ((appOp.size() == 1) && (appOp.get(0) == 5)) {
					stateInfo = pickupAction(qtable, i, j, x, steps, method, "PRANDOM", r);

					// changing the state and corresponding values
					i = stateInfo.get(0);
					j = stateInfo.get(1);
					x = stateInfo.get(2);
					op = stateInfo.get(3);
					steps = stateInfo.get(4);
					totalRewardPRandom = stateInfo.get(5) + totalRewardPRandom;
				}

				// drop action
				else if ((appOp.size() == 1) && (appOp.get(0) == 6)) {
					stateInfo = dropOffAction(qtable, i, j, x, steps, method, "PRANDOM", r);
					// changing the state and corresponding values
					i = stateInfo.get(0);
					j = stateInfo.get(1);
					x = stateInfo.get(2);
					op = stateInfo.get(3);
					steps = stateInfo.get(4);
					totalRewardPRandom = stateInfo.get(5) + totalRewardPRandom;

				}

				// move action
				else {
					int rand = r.nextInt(appOp.size());
					int nextAction = appOp.get(rand);
					stateInfo = moveAction(nextAction, qtable, i, j, x, steps, method, "PRANDOM", r);

					// changing the state and corresponding values
					i = stateInfo.get(0);
					j = stateInfo.get(1);
					x = stateInfo.get(2);
					op = stateInfo.get(3);
					steps = stateInfo.get(4);
					totalRewardPRandom = stateInfo.get(5) + totalRewardPRandom;

				}
			}
		}
		qTableWithStateInfo.put(qtable, stateInfo);
		return qTableWithStateInfo;
	}

	/**
	 * 
	 * This method has functionality to determine next action to be taken by the
	 * agent as defined by PGREEDY policy.
	 * 
	 * 
	 * @param qtable
	 * @param i
	 * @param j
	 * @param x
	 * @param op
	 * @param r
	 * @param experiment
	 * @return
	 */
	public static HashMap<double[][][][], List<Integer>> pGreedy(double qtable[][][][], int i, int j, int x, int op,
			int currentSteps, int totalSteps, String method, Random r) {

		HashMap<double[][][][], List<Integer>> qTableWithStateInfo = new HashMap<>();
		int steps = currentSteps;
		List<Integer> stateInfo = new ArrayList<>();
		int presentSteps = steps;

		totalRewardPGreedy = totalRewardPRandom;

		while (steps < totalSteps) {
			
			int increasedSteps = steps;
			List<Integer> appOp = new ArrayList<>();

			// checking if terminal state is reached
			if (terminalStateReached(dropOffBlocks[4][4] + dropOffBlocks[5][1])) {
				// totalReward of the agent is reset to zero if terminal state is reached
				totalRewardPGreedy = 0;

				Integer terminalStateReached = steps;
				
				terminalStateReachedNumber.add(terminalStateReached);

				List<Integer> initialState = new ArrayList<>();
				initialState = resetPDWorld(i, j, x, op);
				i = initialState.get(0);
				j = initialState.get(1);
				x = initialState.get(2);
				op = initialState.get(3);
			}

			// performance measure-bank account
			if ((increasedSteps - presentSteps) == 100) {
				bankAccount(steps, totalRewardPGreedy, totalBankAccountPGreedy);
				presentSteps = increasedSteps;
				HashMap<Integer, Integer> deliveredBlocks = new HashMap<>();
				deliveredBlocks.put(steps, dropOffBlocks[4][4] + dropOffBlocks[5][1]);
				totalDeliveredBlocksPGreedy.add(deliveredBlocks);
			}

			// getting all the applicable operators for the present state
			appOp = applicableOperators(i, j, x, pickupBlocks, dropOffBlocks);

			// pickup action
			if (appOp.size() >= 1) {

				if ((appOp.size() == 1) && (appOp.get(0) == 5)) {
					stateInfo = pickupAction(qtable, i, j, x, steps, method, "PGREEDY", r);

					// changing the state and corresponding values
					i = stateInfo.get(0);
					j = stateInfo.get(1);
					x = stateInfo.get(2);
					op = stateInfo.get(3);
					steps = stateInfo.get(4);
					totalRewardPGreedy = stateInfo.get(5) + totalRewardPGreedy;
				}

				// drop action
				else if ((appOp.size() == 1) && (appOp.get(0) == 6)) {
					stateInfo = dropOffAction(qtable, i, j, x, steps, method, "PGREEDY", r);
					// changing the state and corresponding values
					i = stateInfo.get(0);
					j = stateInfo.get(1);
					x = stateInfo.get(2);
					op = stateInfo.get(3);
					steps = stateInfo.get(4);
					totalRewardPGreedy = stateInfo.get(5) + totalRewardPGreedy;

				}

				// action with highest q value
				else {
					// find highest q value
					HashMap<Double, Integer> maxQWithAction = new HashMap<>();
					maxQWithAction = findMaxQValueMoveAction(qtable, i, j, x, appOp, r);

					double qvalue;
					int maxQAction = 0;

					for (Entry<Double, Integer> entry : maxQWithAction.entrySet()) {
						qvalue = entry.getKey();
						maxQAction = entry.getValue();
					}

					stateInfo = moveAction(maxQAction, qtable, i, j, x, steps, method, "PGREEDY", r);

					// changing the state and corresponding values
					i = stateInfo.get(0);
					j = stateInfo.get(1);
					x = stateInfo.get(2);
					op = stateInfo.get(3);
					steps = stateInfo.get(4);
					totalRewardPGreedy = stateInfo.get(5) + totalRewardPGreedy;

				}
			}
		}
		qTableWithStateInfo.put(qtable, stateInfo);
		return qTableWithStateInfo;
	}

	/**
	 * 
	 * This method has functionality to determine next action to be taken by the
	 * agent as defined by PEXPLOIT policy.
	 * 
	 * @return
	 */
	public static HashMap<double[][][][], List<Integer>> pExploit(double qtable[][][][], int i, int j, int x, int op,
			int currentSteps, int totalSteps, String method, Random r, String experiment) {

		HashMap<double[][][][], List<Integer>> qTableWithStateInfo = new HashMap<>();
		int steps = currentSteps;
		int action = 0;

		int tempAction = 0;
		int presentSteps = steps;

		totalRewardPExploit = totalRewardPRandom;

		List<Integer> stateInfo = new ArrayList<>();
		while (steps < totalSteps) {

			int increasedSteps = steps;
			int prob = r.nextInt(100);
			List<Integer> appOp = new ArrayList<>();

			// for Experiment 2 reporting q table for first drop off location filled
			if (dropOffFilled) {
				if (experiment.equalsIgnoreCase("Experiment2")) {
					if ((dropOffBlocks[4][4] == 8) | (dropOffBlocks[5][1] == 8)) {
						System.out.println("Experiment 2");
						System.out.println("Qtable after first dropoff location is filled");
						prettyPrint(qtable, "PExploit", steps);
						// once printed no need to print again hence
						dropOffFilled = false;
					}
				}
			}
			
			// checking if terminal state is reached
			if (terminalStateReached(dropOffBlocks[4][4] + dropOffBlocks[5][1])) {

				// totalReward of the agent is reset to zero if terminal state is reached
				totalRewardPExploit = 0;

				Integer terminalStateReached = steps;
			
				terminalStateReachedNumber.add(terminalStateReached);

				// for experiment 2 reporting q table when terminal state is reached
				if (experiment.equalsIgnoreCase("Experiment2")) {
					System.out.println("Experiment 2");
					System.out.println("Qtable after terminal state is reached");
					prettyPrint(qtable, "PExploit", steps);
				}

				List<Integer> initialState = new ArrayList<>();
				initialState = resetPDWorld(i, j, x, op);
				i = initialState.get(0);
				j = initialState.get(1);
				x = initialState.get(2);
				op = initialState.get(3);
			}

			// performance measure-bank account
			if ((increasedSteps - presentSteps) == 100) {
				bankAccount(steps, totalRewardPExploit, totalBankAccountPExploit);
				presentSteps = increasedSteps;
				HashMap<Integer, Integer> deliveredBlocks = new HashMap<>();
				deliveredBlocks.put(steps, dropOffBlocks[4][4] + dropOffBlocks[5][1]);
				totalDeliveredBlocksPExploit.add(deliveredBlocks);
			}

			// getting all the applicable operators for the present state
			appOp = applicableOperators(i, j, x, pickupBlocks, dropOffBlocks);

			// pickup action
			if (appOp.size() >= 1) {

				if ((appOp.size() == 1) && (appOp.get(0) == 5)) {
					stateInfo = pickupAction(qtable, i, j, x, steps, method, "PEXPLOIT", r);

					// changing the state and corresponding values
					i = stateInfo.get(0);
					j = stateInfo.get(1);
					x = stateInfo.get(2);
					op = stateInfo.get(3);
					steps = stateInfo.get(4);
					totalRewardPExploit = stateInfo.get(5) + totalRewardPExploit;
				}

				// drop action
				else if ((appOp.size() == 1) && (appOp.get(0) == 6)) {
					stateInfo = dropOffAction(qtable, i, j, x, steps, method, "PEXPLOIT", r);
					// changing the state and corresponding values
					i = stateInfo.get(0);
					j = stateInfo.get(1);
					x = stateInfo.get(2);
					op = stateInfo.get(3);
					steps = stateInfo.get(4);
					totalRewardPExploit = stateInfo.get(5) + totalRewardPExploit;
				} else {
					// finding highest Q value action
					HashMap<Double, Integer> maxQWithAction = new HashMap<>();
					maxQWithAction = findMaxQValueMoveAction(qtable, i, j, x, appOp, r);
					double qvalue = 0;
					int maxQAction = 0;

					for (Entry<Double, Integer> entry : maxQWithAction.entrySet()) {
						qvalue = entry.getKey();
						maxQAction = entry.getValue();
					}
					tempAction = maxQAction;
					// With prob of 0.85 selecting max q action

					if (prob <= 84 && prob >= 0) {
						action = maxQAction;
					} else {
						// now selecting one among remaining applicable actions excluding max action
						Boolean maxActionNotExcluded = true;
						while (maxActionNotExcluded) {
							int index = r.nextInt(appOp.size());
							action = appOp.get(index);
							if (tempAction != action) {
								maxActionNotExcluded = false;
							}
						}

					}
					stateInfo = moveAction(action, qtable, i, j, x, steps, method, "PEXPLOIT", r);

					// changing the state and corresponding values
					i = stateInfo.get(0);
					j = stateInfo.get(1);
					x = stateInfo.get(2);
					op = stateInfo.get(3);
					steps = stateInfo.get(4);
					totalRewardPExploit = stateInfo.get(5) + totalRewardPExploit;
				}
			}
		}
		qTableWithStateInfo.put(qtable, stateInfo);
		return qTableWithStateInfo;
	}

	/**
	 * 
	 * This method finds the max Q value of all the applicable operators for move
	 * action for a particular state
	 * 
	 * @param qtable
	 * @param l
	 * @param m
	 * @param x
	 * @param appOp
	 * @param r
	 * @return
	 */
	public static HashMap<Double, Integer> findMaxQValueMoveAction(double qtable[][][][], int l, int m, int x,
			List<Integer> appOp, Random r) {

		HashMap<Double, Integer> maxQWithAction = new HashMap<>();
		ArrayList<Integer> qValuesEqual = new ArrayList<>();

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
				qValuesEqual.add(appOp.get(k));
			}
		}
		
		// rolling dice
		if (qValuesEqual.size() > 1) {
			int rand = r.nextInt(qValuesEqual.size());
			op = qValuesEqual.get(rand);

		} else {
			op = qValuesEqual.get(0);
		}

		maxQWithAction.put(maxqValue, op);
		return maxQWithAction;
	}

	/**
	 *
	 * This method is used to update the Q value for every state using Q Learning
	 * formula
	 * 
	 *
	 */
	public static void calculateNewQvalue(double oldQvalue, double qtable[][][][], int i, int j, int x, int nexti,
			int nextj, int nextx, int nextop, int op, int reward, List<Integer> appOp) {
		double maxQValue = findMaxQValueAllApplicableActions(qtable, nexti, nextj, nextx, appOp);
		double newQvalue = oldQvalue + learning_rate * (reward + discount_factor * maxQValue - oldQvalue);
		qtable[i][j][x][op] = newQvalue;
	}

	/**
	 *
	 * This method is used to update the Q value for every state using SARSA
	 * 
	 *
	 */
	public static void calculateNewQvalueSarsa(double oldQvalue, double qtable[][][][], int i, int j, int x, int nexti,
			int nextj, int nextx, int nextop, int op, int reward, String policy, Random r) {
		int nextAction = actionsBasedOnPolicy(qtable, nexti, nextj, nextx, pickupBlocks, dropOffBlocks, policy, r);
		double nextQValue = qtable[nexti][nextj][nextx][nextAction];
		double newQvalue = oldQvalue + learning_rate * (reward + discount_factor * nextQValue - oldQvalue);
		qtable[i][j][x][op] = newQvalue;
	}

	/**
	 * This method checks if a particular move is applicable in a state or not as
	 * leaving the grid is not allowed
	 * 
	 * @return
	 */
	public static boolean isInMatrix(int x, int y) {
		if ((x >= 1 & x <= 5) && (y >= 1 & y <= 5)) {
			return true;
		}
		return false;
	}

	/**
	 * This method checks if terminal state is reached i.e, all drop off blocks have
	 * been delivered
	 * 
	 * @param blocks
	 * @return
	 */
	public static Boolean terminalStateReached(int blocks) {
		if (blocks == 16) {
			return true;
		}
		return false;
	}

	/**
	 *
	 * This method resets the PD World to initial state with agent in (1,5,0) state
	 * and pickup blocks and drop off blocks back to initial positions and numbers.
	 *
	 * @return
	 */

	public static List<Integer> resetPDWorld(int i, int j, int x, int op) {
		//System.out.println("World RESET to Initial State");

		List<Integer> changedStateValues = new ArrayList<>();
		// sending agent back to 1,5,0
		i = 1;
		j = 5;
		x = 0;
		op = 0;

		// setting pick up blocks and drop blocks to initial state
		for (int l = 0; l <= 6; l++) {
			for (int m = 0; m <= 6; m++) {
				if ((l == 3 & m == 3) || (l == 1 & m == 1) || (l == 5 & m == 5) || (l == 4 & m == 1)) {
					pickupBlocks[l][m] = 4;
				} else if ((l == 4 & m == 4) || (l == 5 & m == 1)) {
					dropOffBlocks[l][m] = 0;
				}
			}
		}

		changedStateValues.add(i);
		changedStateValues.add(j);
		changedStateValues.add(x);
		changedStateValues.add(op);

		return changedStateValues;
	}

	/**
	 * This method finds the max Q value of all the applicable operators for a
	 * particular state
	 * 
	 * @param qtable
	 * @param l
	 * @param m
	 * @param x
	 * @param appOp
	 * @return
	 */
	public static double findMaxQValueAllApplicableActions(double qtable[][][][], int l, int m, int x,
			List<Integer> appOp) {

		int i = 0;
		double maxqValue = qtable[l][m][x][appOp.get(i)];

		// finding maxq value for that particular state
		for (i = 1; i < appOp.size(); i++) {
			if (qtable[l][m][x][appOp.get(i)] > maxqValue) {
				maxqValue = qtable[l][m][x][appOp.get(i)];
			}
		}
		return maxqValue;
	}

	/**
	 * This method performs pick up action and returns changed state of the agent
	 * 
	 * @return
	 */
	public static List<Integer> pickupAction(double qtable[][][][], int i, int j, int x, int steps, String method,
			String policy, Random r) {

		List<Integer> stateInfo = new ArrayList<Integer>();
		List<Integer> appOp = new ArrayList<>();

		int nexti = 0;
		int nextj = 0;
		int nextx = 0;
		int nextop = 0;

		reward = 12;
		op = 5;
		pickupBlocks[i][j]--;
		steps++;
		
		nextx = 1;
		nexti = i;
		nextj = j;
		nextop = op;

		appOp = applicableOperators(nexti, nextj, nextx, pickupBlocks, dropOffBlocks);

		if (method.equalsIgnoreCase("qLearning")) {
			calculateNewQvalue(qtable[i][j][x][op], qtable, i, j, x, nexti, nextj, nextx, nextop, op, reward, appOp);
		} else if (method.equalsIgnoreCase("sarsa")) {
			calculateNewQvalueSarsa(qtable[i][j][x][op], qtable, i, j, x, nexti, nextj, nextx, nextop, op, reward,
					policy, r);
		}
		// adding i,j,x,op,steps in order
		stateInfo.add(nexti);
		stateInfo.add(nextj);
		stateInfo.add(nextx);
		stateInfo.add(nextop);
		stateInfo.add(steps);
		stateInfo.add(reward);

		return stateInfo;

	}

	/**
	 * This method performs drop off action and returns changed state of the agent
	 * 
	 * @return
	 */

	public static List<Integer> dropOffAction(double qtable[][][][], int i, int j, int x, int steps, String method,
			String policy, Random r) {

		List<Integer> stateInfo = new ArrayList<Integer>();
		List<Integer> appOp = new ArrayList<>();

		int nexti = 0;
		int nextj = 0;
		int nextx = 0;
		int nextop = 0;

		reward = 12;
		op = 6;
		dropOffBlocks[i][j]++;
		steps++;

		nextx = 0;
		nexti = i;
		nextj = j;
		nextop = op;

		appOp = applicableOperators(nexti, nextj, nextx, pickupBlocks, dropOffBlocks);

		if (method.equalsIgnoreCase("qLearning")) {
			calculateNewQvalue(qtable[i][j][x][op], qtable, i, j, x, nexti, nextj, nextx, nextop, op, reward, appOp);
		} else if (method.equalsIgnoreCase("sarsa")) {
			calculateNewQvalueSarsa(qtable[i][j][x][op], qtable, i, j, x, nexti, nextj, nextx, nextop, op, reward,
					policy, r);
		}

		// adding i,j,x,op,steps in order
		stateInfo.add(nexti);
		stateInfo.add(nextj);
		stateInfo.add(nextx);
		stateInfo.add(nextop);
		stateInfo.add(steps);
		stateInfo.add(reward);

		return stateInfo;
	}

	/**
	 * This method performs move action and returns changed state of the agent
	 * 
	 * @return
	 */

	public static List<Integer> moveAction(int r, double qtable[][][][], int i, int j, int x, int steps, String method,
			String policy, Random rand) {

		List<Integer> stateInfo = new ArrayList<Integer>();
		List<Integer> appOp = new ArrayList<>();
		int nexti = 0;
		int nextj = 0;
		int nextx = 0;
		int nextop = 0;

		// MS
		if (r == 1) {
			// i,j,x,op values change
			// calculate new q value for this state for this q
			op = 1;
			reward = -1;
			if (isInMatrix(i + 1, j)) {
				nexti = i + 1;
				steps++;
			}

			nextj = j;
			nextx = x;
			nextop = op;

			appOp = applicableOperators(nexti, nextj, nextx, pickupBlocks, dropOffBlocks);

			if (method.equalsIgnoreCase("qLearning")) {
				calculateNewQvalue(qtable[i][j][x][op], qtable, i, j, x, nexti, nextj, nextx, nextop, op, reward,
						appOp);
			} else if (method.equalsIgnoreCase("sarsa")) {
				calculateNewQvalueSarsa(qtable[i][j][x][op], qtable, i, j, x, nexti, nextj, nextx, nextop, op, reward,
						policy, rand);
			}

			// adding i,j,x,op,steps in order
			stateInfo.add(nexti);
			stateInfo.add(nextj);
			stateInfo.add(nextx);
			stateInfo.add(nextop);
			stateInfo.add(steps);
			stateInfo.add(reward);

			// MN
		} else if (r == 2) {
			// i,j,x,op values change
			// calculate new q value for this state for this q
			op = 2;
			reward = -1;
			if (isInMatrix(i - 1, j)) {
				nexti = i - 1;
				steps++;
			}
			nextj = j;
			nextx = x;
			nextop = op;

			appOp = applicableOperators(nexti, nextj, nextx, pickupBlocks, dropOffBlocks);

			if (method.equalsIgnoreCase("qLearning")) {
				calculateNewQvalue(qtable[i][j][x][op], qtable, i, j, x, nexti, nextj, nextx, nextop, op, reward,
						appOp);
			} else if (method.equalsIgnoreCase("sarsa")) {
				calculateNewQvalueSarsa(qtable[i][j][x][op], qtable, i, j, x, nexti, nextj, nextx, nextop, op, reward,
						policy, rand);
			}
			// adding i,j,x,op,steps in order
			stateInfo.add(nexti);
			stateInfo.add(nextj);
			stateInfo.add(nextx);
			stateInfo.add(nextop);
			stateInfo.add(steps);
			stateInfo.add(reward);

			// ME
		} else if (r == 3) {
			// i,j,x,op values change
			// calculate new q value for this state for this q
			op = 3;
			reward = -1;

			if (isInMatrix(i, j + 1)) {
				nextj = j + 1;
				steps++;
			}

			nexti = i;
			nextx = x;
			nextop = op;

			appOp = applicableOperators(nexti, nextj, nextx, pickupBlocks, dropOffBlocks);

			if (method.equalsIgnoreCase("qLearning")) {
				calculateNewQvalue(qtable[i][j][x][op], qtable, i, j, x, nexti, nextj, nextx, nextop, op, reward,
						appOp);
			} else if (method.equalsIgnoreCase("sarsa")) {
				calculateNewQvalueSarsa(qtable[i][j][x][op], qtable, i, j, x, nexti, nextj, nextx, nextop, op, reward,
						policy, rand);
			}
			// adding i,j,x,op,steps in order
			stateInfo.add(nexti);
			stateInfo.add(nextj);
			stateInfo.add(nextx);
			stateInfo.add(nextop);
			stateInfo.add(steps);
			stateInfo.add(reward);

			return stateInfo;
			// MW
		} else if (r == 4) {
			// i,j,x,op values change
			// calculate new q value for this state for this q
			op = 4;
			reward = -1;

			if (isInMatrix(i, j - 1)) {
				nextj = j - 1;
				steps++;
			}

			nexti = i;
			nextx = x;
			nextop = op;

			appOp = applicableOperators(nexti, nextj, nextx, pickupBlocks, dropOffBlocks);

			if (method.equalsIgnoreCase("qLearning")) {
				calculateNewQvalue(qtable[i][j][x][op], qtable, i, j, x, nexti, nextj, nextx, nextop, op, reward,
						appOp);
			} else if (method.equalsIgnoreCase("sarsa")) {
				calculateNewQvalueSarsa(qtable[i][j][x][op], qtable, i, j, x, nexti, nextj, nextx, nextop, op, reward,
						policy, rand);
			}

			// adding i,j,x,op,steps in order
			stateInfo.add(nexti);
			stateInfo.add(nextj);
			stateInfo.add(nextx);
			stateInfo.add(nextop);
			stateInfo.add(steps);
			stateInfo.add(reward);
		}
		return stateInfo;
	}

	/**
	 * This method prints the obtained q table in table format with states as rows
	 * and operators as columns
	 * 
	 * @return
	 */
	public static void prettyPrint(double qtable[][][][], String policy, int steps) {

		System.out.println("qtable for x=0 with " + policy + " and steps " + steps);

		for (int x = 0; x <= 1; x++) {
			for (int op = 1; op <= 6; op++) {
				qtable[1][0][x][op] = op;
			}
		}

		for (int i = 1; i <= 5; i++) {
			for (int j = 0; j <= 5; j++) {
				for (int op = 1; op <= 6; op++) {
					if (op == 1) {
						if (!((i == 1 || i == 2 || i == 3 || i == 4 || i == 5) && (j == 0)))
							System.out.print("(" + i + "," + j + ")");
					}
					if (i == 1 && j == 0) {
						int ipart = (int) (qtable[i][j][0][op]);
						System.out.print(String.format(" %10s ", operators.get(ipart)) + "  ");
					} else if ((i == 5 || i == 4 || i == 3 || i == 2) && ((j == 0))) {
						break;
					}

					else {
						System.out.print(String.format("|%10f|", qtable[i][j][0][op]) + "  ");
						// System.out.format(qtable[i][j][0][op] + " ");
					}
				}
				if (!((i == 2 || i == 3 || i == 4 || i == 5) && (j == 0))) {
					System.out.println(" ");
				}
			}
		}

		System.out.println("qtable for x=1 with " + policy + " and steps " + steps);

		for (int i = 1; i <= 5; i++) {
			for (int j = 0; j <= 5; j++) {
				for (int op = 1; op <= 6; op++) {
					if (op == 1) {
						if (!((i == 1 || i == 2 || i == 3 || i == 4 || i == 5) && (j == 0)))
							System.out.print("(" + i + "," + j + ")");
					}
					if (i == 1 && j == 0) {
						int ipart = (int) (qtable[i][j][0][op]);
						System.out.print(String.format(" %10s ", operators.get(ipart)) + "  ");
					} else if ((i == 5 || i == 4 || i == 3 || i == 2) && ((j == 0))) {
						break;
					}

					else {
						System.out.print(String.format("|%10f|", qtable[i][j][1][op]) + "  ");
						// System.out.format(qtable[i][j][0][op] + " ");
					}
				}
				if (!((i == 2 || i == 3 || i == 4 || i == 5) && (j == 0))) {
					System.out.println(" ");
				}
			}
		}
	}

	/**
	 * 
	 * This method returns all the applicable operators for a given state based on
	 * whether agent is carrying a block or not and pick and drop are not considered
	 * as applicable operators if there are no blocks left in pick up state or drop
	 * off state is already full.
	 * 
	 * @return
	 */
	public static List<Integer> applicableOperators(int i, int j, int x, int[][] pickupBlocks, int[][] dropOffBlocks) {
		List<Integer> appOp = new ArrayList<>();
		
		if (((i == 3 & j == 3) || (i == 1 & j == 1) || (i == 5 & j == 5) || (i == 4 & j == 1)) && ((x == 0))
				&& ((pickupBlocks[i][j] <= 4)) && ((pickupBlocks[i][j] >= 1))) {
			appOp.add(5);
		} else if (((i == 4 & j == 4) || (i == 5 & j == 1)) && ((x == 1)) && ((dropOffBlocks[i][j] < 8))) {
			appOp.add(6);
		} else {
			// MS
			if (isInMatrix(i + 1, j)) {
				appOp.add(1);
			}
			// MN
			if (isInMatrix(i - 1, j)) {
				appOp.add(2);
			}
			// ME
			if (isInMatrix(i, j + 1)) {
				appOp.add(3);
			}
			// MW
			if (isInMatrix(i, j - 1)) {
				appOp.add(4);
			}
		}
		return appOp;
	}

	/**
	 * This method adds the reward of the agent that is calculated for every 100
	 * steps into a list
	 * 
	 * @param steps
	 * @param totalReward
	 * @param totalBankAccount
	 */
	public static void bankAccount(int steps, int totalReward, List<HashMap<Integer, Integer>> totalBankAccount) {
		HashMap<Integer, Integer> bankAccount = new HashMap<>();
		bankAccount.put(steps, totalReward);
		totalBankAccount.add(bankAccount);
	}

	/**
	 * This method prints the performance measures of bank account of agent and
	 * number of operators needed to reach terminal state for every experiment.
	 * 
	 * @param bankAccount
	 * @param terminalStateReachedSteps
	 * @param experiment
	 */
	public static void printPerformanceMeasures(List<HashMap<Integer, Integer>> bankAccount,
			List<Integer> terminalStateReachedSteps, String experiment, String policy) {

		List<Integer> numOperators=new ArrayList<>();
		
		sb.append(System.lineSeparator());
		sb.append("Performance measures for " + experiment);
		sb.append(System.lineSeparator());

		sb.append("Bank Account of agent for " + policy + "," + experiment);
		sb.append(System.lineSeparator());

		try (FileWriter fw = new FileWriter(new File("src/reward_rl.txt"))) {
			sb.append(String.format("%10s", "Steps"));
			sb.append(String.format("%10s", "Reward"));
			sb.append(System.lineSeparator());
			for (int i = 0; i < bankAccount.size(); i++) {
				Set<Entry<Integer, Integer>> mapSet = bankAccount.get(i).entrySet();
				Iterator mapIterator = mapSet.iterator();

				while (mapIterator.hasNext()) {
					Map.Entry mapEntry = (Map.Entry) mapIterator.next();
					Integer steps = (Integer) mapEntry.getKey();
					Integer reward = (Integer) mapEntry.getValue();
					// iterate over the array and print each value
					sb.append(String.format("%10d", steps));
					sb.append(String.format("%10d", reward));
				}
				sb.append(System.lineSeparator());
			}
			sb.append(System.lineSeparator());
			sb.append("Terminal state reached at steps " + policy + ","
					+ terminalStateReachedSteps);
			sb.append(System.lineSeparator());
			
			numOperators.add(terminalStateReachedSteps.get(0));
			
			for(int d=0;d<terminalStateReachedSteps.size()-1;d++) {
				int value=terminalStateReachedSteps.get(d+1)-terminalStateReachedSteps.get(d);
				numOperators.add(value);
			}
			
			sb.append("Number of operators needed to reach terminal state "+numOperators);
			
			fw.write(String.valueOf(sb));
		} catch (Exception e) {
			e.getMessage();
		}
	}

	/**
	 * 
	 * This method returns the next action for the agent for a particular state
	 * based on the policies for SARSA Q value update calculation.
	 * 
	 * Finding A' for S'
	 * 
	 * @return
	 */
	public static Integer actionsBasedOnPolicy(double qtable[][][][], int i, int j, int x, int[][] pickupBlocks,
			int[][] dropOffBlocks, String policy, Random r) {

		int action = 0;
		// PRANDOM
		if (policy.equalsIgnoreCase("PRANDOM")) {
			List<Integer> appOp = new ArrayList<>();
			appOp = applicableOperators(i, j, x, pickupBlocks, dropOffBlocks);

			if (appOp.size() >= 1) {
				if ((appOp.size() == 1) && (appOp.get(0) == 5)) {
					action = 5;
				} else if ((appOp.size() == 1) && (appOp.get(0) == 6)) {
					action = 6;
				} else {
					int rand = r.nextInt(appOp.size());
					action = appOp.get(rand);
				}
			}
		}

		// PEXPLOIT
		if (policy.equalsIgnoreCase("PEXPLOIT")) {
			List<Integer> appOp = new ArrayList<>();
			appOp = applicableOperators(i, j, x, pickupBlocks, dropOffBlocks);
			int tempAction = 0;
			int prob = r.nextInt(100);

			if (appOp.size() >= 1) {
				if ((appOp.size() == 1) && (appOp.get(0) == 5)) {
					action = 5;
				} else if ((appOp.size() == 1) && (appOp.get(0) == 6)) {
					action = 6;
				} else {
					// finding highest Q value action
					HashMap<Double, Integer> maxQWithAction = new HashMap<>();
					maxQWithAction = findMaxQValueMoveAction(qtable, i, j, x, appOp, r);
					int maxQAction = 0;

					for (Entry<Double, Integer> entry : maxQWithAction.entrySet()) {
						maxQAction = entry.getValue();
					}

					tempAction = maxQAction;
					// With prob of 0.85 selecting max q action
					if (prob <= 84 && prob >= 0) {
						action = maxQAction;
					} else {
						// now taking remaining applicable actions excluding max action
						Boolean maxActionNotExcluded = true;
						while (maxActionNotExcluded) {
							int index = r.nextInt(appOp.size());
							action = appOp.get(index);
							if (tempAction != action) {
								maxActionNotExcluded = false;
							}
						}

					}

				}
			}

		}
		return action;
	}
}