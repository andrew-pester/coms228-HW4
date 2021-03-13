package edu.iastate.cs228.hw4;

/**
 *  
 * @author Andrew Pester
 *
 */

import java.util.HashMap;

/**
 * 
 * This class represents an infix expression. It implements infix to postfix
 * conversion using one stack, and evaluates the converted postfix expression.
 *
 */

public class InfixExpression extends Expression {
	private String infixExpression; // the infix expression to convert
	private boolean postfixReady = false; // postfix already generated if true
	private int rankTotal = 0; // Keeps track of the cumulative rank of the infix expression.

	private PureStack<Operator> operatorStack; // stack of operators

	/**
	 * Constructor stores the input infix string, and initializes the operand stack
	 * and the hash map.
	 * 
	 * @param st     input infix string.
	 * @param varTbl hash map storing all variables in the infix expression and
	 *               their values.
	 */
	public InfixExpression(String st, HashMap<Character, Integer> varTbl) {

		if (st.charAt(0) == 'I') {
			st = st.substring(2);
		}
		infixExpression = st;
		super.varTable = varTbl;
		operatorStack = new ArrayBasedStack<>();
	}

	/**
	 * Constructor supplies a default hash map.
	 * 
	 * @param s
	 * @throws ExpressionFormatException
	 */
	public InfixExpression(String s) {

		if (s.charAt(0) == 'I') {
			s = s.substring(2);
		}
		infixExpression = s;
		super.varTable = new HashMap<>();
		operatorStack = new ArrayBasedStack<>();
	}

	/**
	 * Outputs the infix expression according to the format in the project
	 * description.
	 */
	@Override
	public String toString() {

		String ret = "";
		infixExpression = Expression.removeExtraSpaces(infixExpression);
		for (int i = 0; i < infixExpression.length(); i++) {
			if (infixExpression.charAt(i) == '(' && infixExpression.charAt(i + 1) == ' ') {
				ret += infixExpression.charAt(i);
				i++;
			} else if (infixExpression.charAt(i) == ' ' && infixExpression.charAt(i + 1) == ')') {
				ret += ')';
				i++;
			} else {
				ret += infixExpression.charAt(i);
			}
		}
		return ret;
	}

	/**
	 * @return equivalent postfix expression, or
	 * 
	 *         a null string if a call to postfix() inside the body (when
	 *         postfixReady == false) throws an exception.
	 */
	public String postfixString() {

		if (postfixReady) {
			return this.postfixExpression;
		} else {
			try {
				postfix();
				return this.postfixExpression;
			} catch (ExpressionFormatException e) {
				return null;
			}
		}

	}

	/**
	 * Resets the infix expression.
	 * 
	 * @param st
	 */
	public void resetInfix(String st) {

		infixExpression = st;
	}

	/**
	 * Converts infix expression to an equivalent postfix string stored at
	 * postfixExpression. then sets postfixReady to true
	 */
	public void postfix() throws ExpressionFormatException {

		int rankTotal = 0;
		if (postfixReady == false) {
			for (int i = 0; i < infixExpression.length(); i += 2) {
				if (Expression.isInt(infixExpression.substring(i, i + 1))) {
					while (i < infixExpression.length() && Expression.isInt(infixExpression.substring(i, i + 1))) {
						this.postfixExpression += infixExpression.substring(i, i + 1);
						i++;

					}
					rankTotal++;
					if (rankTotal > 1) {
						throw new ExpressionFormatException("Operator expected");
					}
					this.postfixExpression += " ";
					i--;
				} else if (infixExpression.charAt(i) == '~') {
					throw new ExpressionFormatException("Invalid character");
				} else if (Expression.isOperator(infixExpression.charAt(i))) {
					Operator o;
					if (i - 2 < 0 && infixExpression.charAt(i) == '-') {
						o = new Operator('~');
					} else if (infixExpression.charAt(i) == '-' && Expression.isOperator(infixExpression.charAt(i - 2))
							&& infixExpression.charAt(i - 2) != ')') {
						o = new Operator('~');
					} else {
						o = new Operator(infixExpression.charAt(i));
					}
					if (operatorStack.isEmpty()) {
						operatorStack.push(o);
					} else if (operatorStack.peek().compareTo(o) == -1) {
						operatorStack.push(o);
					} else {
						outputHigherOrEqual(o);
						if (o.operator == '#') {
							throw new ExpressionFormatException("Missing '('");
						}
					}
					if (o.operator != '(' && o.operator != ')' && o.operator != '~') {
						rankTotal--;
					}
					if (rankTotal < 0) {
						throw new ExpressionFormatException("Operand expected");
					}

				} else if (Expression.isVariable(infixExpression.charAt(i))) {
					this.postfixExpression += infixExpression.substring(i, i + 1) + " ";
					rankTotal++;
				} else {
					throw new ExpressionFormatException("Invalid character");
				}
			}
			while (!operatorStack.isEmpty()) {
				if (operatorStack.peek().operator == '(') {
					throw new ExpressionFormatException("Missing ')'");
				}
				this.postfixExpression += operatorStack.pop().operator + " ";
			}
		}
		postfixReady = true;
	}

	/**
	 * This function first calls postfix() to convert infixExpression into
	 * postfixExpression. Then it creates a PostfixExpression object and calls its
	 * evaluate() method (which may throw an exception). It also passes any
	 * exception thrown by the evaluate() method of the PostfixExpression object
	 * upward the chain.
	 * 
	 * @return value of the infix expression
	 * @throws ExpressionFormatException, UnassignedVariableException
	 */
	public int evaluate() throws ExpressionFormatException, UnassignedVariableException {
		postfix();
		PostfixExpression p = new PostfixExpression(this.postfixExpression, this.varTable);
		return p.evaluate();
	}

	/**
	 * Pops the operator stack and output as long as the operator on the top of the
	 * stack has a stack precedence greater than or equal to the input precedence of
	 * the current operator op. Writes the popped operators to the string
	 * postfixExpression.
	 * 
	 * If op is a ')', and the top of the stack is a '(', also pops '(' from the
	 * stack but does not write it to postfixExpression.
	 * 
	 * @param op current operator
	 */
	private void outputHigherOrEqual(Operator op) {

		for (int i = 0; i <= operatorStack.size(); i++) {
			if (!operatorStack.isEmpty() && op.operator == ')' && operatorStack.peek().operator == '(') {
				operatorStack.pop();
				break;
			} else if (!operatorStack.isEmpty() && operatorStack.peek().compareTo(op) != -1) {
				this.postfixExpression += operatorStack.pop().operator + " ";
				i--;
			} else if (operatorStack.isEmpty() && op.operator == ')') {
				op.operator = '#';
			}
		}
		if (op.operator != ')') {
			operatorStack.push(op);
		}
	}

	// other helper methods if needed
}
