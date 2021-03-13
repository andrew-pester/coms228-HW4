package edu.iastate.cs228.hw4;

import java.util.HashMap;
/**
 * 
 * @author Andrew Pester
 *
 */
public abstract class Expression {
	protected String postfixExpression;
	protected HashMap<Character, Integer> varTable; // hash map to store variables in the

	/**
	 * default constructor for an expression
	 */
	protected Expression() {
		postfixExpression = "";
	}

	/**
	 * Initialization with a provided hash map.
	 * 
	 * @param varTbl
	 */
	protected Expression(String st, HashMap<Character, Integer> varTbl) {

		postfixExpression = st;
		varTable = varTbl;
	}

	/**
	 * Initialization with a default hash map.
	 * 
	 * @param st
	 */
	protected Expression(String st) {

		postfixExpression = st;
		varTable = new HashMap<Character, Integer>();
	}

	/**
	 * Setter for instance variable varTable.
	 * 
	 * @param varTbl
	 */
	public void setVarTable(HashMap<Character, Integer> varTbl) {

		varTable = varTbl;
	}

	/**
	 * Evaluates the infix or postfix expression.
	 * 
	 * @return value of the expression
	 * @throws ExpressionFormatException, UnassignedVariableException
	 */
	public abstract int evaluate() throws ExpressionFormatException, UnassignedVariableException;

	// --------------------------------------------------------
	// Helper methods for InfixExpression and PostfixExpression
	// --------------------------------------------------------

	/**
	 * Checks if a string represents an integer.
	 * 
	 * @param s
	 * @return true if it is an integer else false
	 */
	protected static boolean isInt(String s) {

		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * @param c
	 * @return true if it is one of '~', '+', '-', '*', '/', '%', '^', '(', ')'.
	 *         else false
	 */
	protected static boolean isOperator(char c) {

		if (c == '~' || c == '+' || c == '-' || c == '*' || c == '/' || c == '%' || c == '^' || c == '(' || c == ')') {
			return true;
		}
		return false;
	}

	/**
	 * @param c
	 * @return true if it is a lower case English letter else false
	 */
	protected static boolean isVariable(char c) {

		if (Character.isLowerCase(c) && Character.isLetter(c)) {
			return true;
		}
		return false;
	}

	/**
	 * Removes extra blank spaces in a string.
	 * 
	 * @param s
	 * @return the string without extra spaces
	 */
	protected static String removeExtraSpaces(String s) {

		s = s.trim();
		for (int i = 0; i < s.length(); i++) {
			s = s.replace("	", " ");
			s = s.replace("  ", " ");
		}
		return s;
	}

}
