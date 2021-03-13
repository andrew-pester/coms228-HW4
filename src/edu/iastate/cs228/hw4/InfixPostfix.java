package edu.iastate.cs228.hw4;

import java.io.File;
import java.io.FileNotFoundException;

/**
 *  
 * @author Andrew Pester
 *
 */

/**
 * 
 * This class evaluates input infix and postfix expressions. 
 *
 */

import java.util.HashMap;
import java.util.Scanner;

public class InfixPostfix {

	/**
	 * Repeatedly evaluates input infix and postfix expressions. See the project
	 * description for the input description. It constructs a HashMap object for
	 * each expression and passes it to the created InfixExpression or
	 * PostfixExpression object.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 * @throws UnassignedVariableException
	 * @throws ExpressionFormatException
	 **/
	public static void main(String[] args)
			throws FileNotFoundException, ExpressionFormatException, UnassignedVariableException {
		
		Scanner in = new Scanner(System.in); 
		System.out.println("Evaluation of Infix and Postfix Expressions");
		System.out.println("keys: 1 (standard input) 2 (file input) 3 (exit)");
		System.out.println("(Enter \"I\" before an infix expression, \"P\" before a postfix expression)\n");
		int trial = 1;

		while (true) {
			// all get reset
			HashMap<Character, Integer> input = new HashMap<>(); // hash map used to create expressions
			String expression = "";
			String tmp = "";
			System.out.print("Trial " + trial++ + ": ");
			while(in.hasNextLine()) {
				tmp = in.nextLine();
				break;
			}
			if (tmp.compareTo("1") == 0) {
				System.out.print("Expression: ");
				expression = in.nextLine();
				// post fix part
				if (expression.charAt(0) == 'P') {
					PostfixExpression p = new PostfixExpression(expression);
					System.out.println("Postfix form: " + p.toString());
					input = findVar(expression);
					if (!input.isEmpty()) {
						p.setVarTable(input);
					}
					System.out.println("Expression value: " + p.evaluate());
					System.out.println();
				}
				// infix part
				else if (expression.charAt(0) == 'I') {
					InfixExpression i = new InfixExpression(expression);
					System.out.println("Infix form: " + i.toString());
					System.out.println("Postfix form: " + i.postfixString());
					input = findVar(expression);
					if (!input.isEmpty()) {
						i.setVarTable(input);
					}
						System.out.println("Expression value: " + i.evaluate());
						System.out.println();

				}

			}
			else if (tmp.compareTo("2") == 0) {
				// from a file
				System.out.println("Input from a file");
				System.out.print("Enter file name: ");
				File f = new File(in.nextLine());
				Scanner sc = new Scanner(f);
				String test = "";
				expression = sc.nextLine();
				while (sc.hasNext()) {
					test = "";
					if (expression.charAt(0) == 'P') {
						PostfixExpression p = new PostfixExpression(expression);
						System.out.println("Postfix form: " + p.toString());
						while (sc.hasNextLine()) {
							test = sc.nextLine();
							if (test.charAt(0) == 'I' || test.charAt(0) == 'P') {
								expression = test;
								break;
							} else if (Expression.isVariable(test.charAt(0))) {
								System.out.println(test);
								int j = 4;// start of all ints in correct format
								int ret = 0;
								while (j < test.length() && Expression.isInt(test.substring(j, j + 1))) {
									ret *= 10;
									ret += Integer.parseInt(test.substring(j, j + 1));
									j++;
								}
								input.put(test.charAt(0), ret);
							}
							p.varTable = input;
						}
						System.out.println("Expression value: " + p.evaluate());
						System.out.println();

					} else if (expression.charAt(0) == 'I') {
						InfixExpression i = new InfixExpression(expression);
						System.out.println("Infix form: " + i.toString());
						System.out.println("Postfix form: " + i.postfixString());
						while (sc.hasNextLine()) {
							test = sc.nextLine();
							if (test.charAt(0) == 'I' || test.charAt(0) == 'P') {
								expression = test;
								break;
							} else if (Expression.isVariable(test.charAt(0))) {
								System.out.println(test);
								int j = 4;// start of all ints in correct format
								int ret = 0;
								while (j < test.length() && Expression.isInt(test.substring(j, j + 1))) {
									ret *= 10;
									ret += Integer.parseInt(test.substring(j, j + 1));
									j++;
								}
								input.put(test.charAt(0), ret);
							}
							i.varTable = input;
						}
						System.out.println("Expression value: " + i.evaluate());

						System.out.println();
					}
				}
				sc.close();

			} else if (tmp.compareTo("3") == 0) {
				break;
			}
		}
	}

	/**
	 * scans through the string given until it finds a variables then prompts the
	 * user for the value
	 * 
	 * @param ex the expression that needs searched for variables
	 * @return the variables
	 */
	private static HashMap<Character, Integer> findVar(String ex) {
		HashMap<Character, Integer> ret = new HashMap<>();
		Scanner in = new Scanner(System.in);
		boolean first = true;
		for (int i = 1; i < ex.length(); i++) {
			if (Expression.isVariable(ex.charAt(i)) && !ret.containsKey(ex.charAt(i))) {
				if (first) {
					System.out.println("where");
					first = false;
				}
				System.out.print(ex.charAt(i) + " = ");
				int ret1 = Integer.parseInt(in.nextLine());
				ret.put(ex.charAt(i), ret1);

			}
		}
		return ret;
	}
}
