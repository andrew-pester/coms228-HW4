package edu.iastate.cs228.hw4;

/**
 *  
 * @author Andrew Pester 
 *
 */

/**
 * 
 * This class evaluates a postfix expression using one stack.    
 *
 */

import java.util.HashMap;
import java.util.NoSuchElementException;

public class PostfixExpression extends Expression {
	private int leftOperand; // left operand for the current evaluation step
	private int rightOperand; // right operand (or the only operand in the case of
								// a unary minus) for the current evaluation step

	private PureStack<Integer> operandStack; // stack of operands

	/**
	 * Constructor stores the input postfix string and initializes the operand
	 * stack.
	 * 
	 * @param st     input postfix string.
	 * @param varTbl hash map that stores variables from the postfix string and
	 *               their values.
	 */
	public PostfixExpression(String st, HashMap<Character, Integer> varTbl) {

		if (st.charAt(0) == 'P') {
			st = st.substring(2);
		}
		super.postfixExpression = st;
		super.varTable = varTbl;
		operandStack = new ArrayBasedStack<>();
	}

	/**
	 * Constructor supplies a default hash map.
	 * 
	 * @param s
	 */
	public PostfixExpression(String s) {

		if (s.charAt(0) == 'P') {
			s = s.substring(2);
		}
		super.postfixExpression = s;
		super.varTable = new HashMap<>();
		operandStack = new ArrayBasedStack<>();
	}

	/**
	 * Outputs the postfix expression according to the format in the project
	 * description.
	 */
	@Override
	public String toString() {

		postfixExpression = super.removeExtraSpaces(postfixExpression);
		return postfixExpression;
	}

	/**
	 * Resets the postfix expression.
	 * 
	 * @param st
	 */
	public void resetPostfix(String st) {

		postfixExpression = st;
	}

	/**
	 * Scan the postfixExpression and carry out the following:
	 * 
	 * 1. Whenever an integer is encountered, push it onto operandStack. 2. Whenever
	 * a binary (unary) operator is encountered, invoke it on the two (one) elements
	 * popped from operandStack, and push the result back onto the stack. 3. On
	 * encountering a character that is not a digit, an operator, or a blank space,
	 * stop the evaluation.
	 * 
	 * @return value of the postfix expression
	 * @throws ExpressionFormatException   with one of the messages below:
	 * 
	 *                                     -- "Invalid character" if encountering a
	 *                                     character that is not a digit, an
	 *                                     operator or a whitespace (blank, tab); --
	 *                                     "Too many operands" if operandStack is
	 *                                     non-empty at the end of evaluation; --
	 *                                     "Too many operators" if getOperands()
	 *                                     throws NoSuchElementException; -- "Divide
	 *                                     by zero" if division or modulo is the
	 *                                     current operation and rightOperand == 0;
	 *                                     -- "0^0" if the current operation is "^"
	 *                                     and leftOperand == 0 and rightOperand ==
	 *                                     0; -- self-defined message if the error
	 *                                     is not one of the above.
	 * 
	 *                                     UnassignedVariableException if the
	 *                                     operand as a variable does not have a
	 *                                     value stored in the hash map. In this
	 *                                     case, the exception is thrown with the
	 *                                     message
	 * 
	 *                                     -- "Variable <name> was not assigned a
	 *                                     value", where <name> is the name of the
	 *                                     variable.
	 * @throws UnassignedVariableException
	 * 
	 */
	public int evaluate() throws ExpressionFormatException, UnassignedVariableException {
		// this works
		for (int i = 0; i < this.postfixExpression.length(); i += 2) {
			int tmp = 0;
			// adds ints to operand stack
			if (Expression.isInt(postfixExpression.substring(i, i + 1))) {
				while (i < postfixExpression.length() && Expression.isInt(postfixExpression.substring(i, i + 1))) {
					tmp *= 10;
					tmp += Integer.parseInt(postfixExpression.substring(i, i + 1));
					i++;
				}
				operandStack.push(tmp);
				i--;
			}
			// evaluates when operator occurs
			else if (Expression.isOperator(this.postfixExpression.charAt(i))) {
				try {
					getOperands(postfixExpression.charAt(i));
				} catch (NoSuchElementException e) {
					throw new ExpressionFormatException("Too many operators");
				}
				if (rightOperand == 0
						&& (this.postfixExpression.charAt(i) == '/' || this.postfixExpression.charAt(i) == '%')) {
					throw new ExpressionFormatException("Divide by zero");
				} else if (rightOperand == 0 && leftOperand == 0 && this.postfixExpression.charAt(i) == '^') {
					throw new ExpressionFormatException("0^0");
				}
				operandStack.push(compute(postfixExpression.charAt(i)));
			}
			// sees if variable is present in hash map
			else if (Expression.isVariable(postfixExpression.charAt(i))) {
				if (this.varTable.containsKey(postfixExpression.charAt(i))) {
					operandStack.push(this.varTable.get(postfixExpression.charAt(i)));
				} else {
					throw new UnassignedVariableException(
							"Variable " + postfixExpression.charAt(i) + " was not assigned a value");
				}
			} else {
				throw new ExpressionFormatException("Invalid character");
			}
		}
		if (operandStack.size() != 1) {
			throw new ExpressionFormatException("Too many operands");
		}
		return operandStack.pop();
	}

	/**
	 * For unary operator, pops the right operand from operandStack, and assign it
	 * to rightOperand. The stack must have at least one entry. Otherwise, throws
	 * NoSuchElementException. For binary operator, pops the right and left operands
	 * from operandStack, and assign them to rightOperand and leftOperand,
	 * respectively. The stack must have at least two entries. Otherwise, throws
	 * NoSuchElementException.
	 * 
	 * @param op char operator for checking if it is binary or unary operator.
	 */
	private void getOperands(char op) throws NoSuchElementException {
		if (op == '~') {
			rightOperand = operandStack.pop();
		} else {
			rightOperand = operandStack.pop();
			leftOperand = operandStack.pop();
		}
	}

	/**
	 * Computes "leftOperand op rightOprand" or "op rightOprand" if a unary
	 * operator.
	 * 
	 * @param op operator that acts on leftOperand and rightOperand.
	 * @return
	 */
	private int compute(char op) {
		if (op == '~') {
			return -rightOperand;
		} else {
			if (op == '+') {
				return leftOperand + rightOperand;
			} else if (op == '-') {
				return leftOperand - rightOperand;
			} else if (op == '/') {
				return leftOperand / rightOperand;
			} else if (op == '*') {
				return leftOperand * rightOperand;
			} else if (op == '%') {
				return leftOperand % rightOperand;
			} else if (op == '^') {
				return (int) Math.pow(leftOperand, rightOperand);
			}
		}

		return 0;
	}
}
