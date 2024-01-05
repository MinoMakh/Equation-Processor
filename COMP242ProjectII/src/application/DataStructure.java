package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class DataStructure {

	private CStack<String> stack = new CStack<>(2);
	private int delimitersHead, tagsHead, conversionHead, calculateHead, equationsHead, displayHead;

	// Getting all the lists heads when the object is created
	public DataStructure() {
		delimitersHead = stack.createList();
		tagsHead = stack.createList();
		conversionHead = stack.createList();
		calculateHead = stack.createList();
		equationsHead = stack.createList();
		displayHead = stack.createList();
	}

	// Checking if the Delimiters are Balanced (Open and Close)
	public boolean checkDelimiters(File file) {
		String delimiters = "";

		// Reading from the file
		try {
			Scanner scanFile = new Scanner(file);
			while (scanFile.hasNext()) {
				char[] chars = scanFile.nextLine().toCharArray();

				// Getting the delimiters from the line
				for (int i = 0; i < chars.length; i++) {
					if (chars[i] == '<' || chars[i] == '>')
						delimiters += chars[i];
				}
			}
			scanFile.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}

		// Checking the delimiters
		for (int i = 0; i < delimiters.length(); i++) {
			String d = delimiters.substring(i, i + 1);
			switch (d) {
			case "<":
				stack.push(d, delimitersHead);
				break;
			case ">":
				if (stack.isEmpty(delimitersHead))
					return false;
				String open = stack.pop(delimitersHead);
				if (!open.equals("<") && !d.equals(">"))
					return false;
			}
		}
		if (stack.isEmpty(delimitersHead))
			return true;
		return false;
	}

	// Checking if the tags are balanced
	public boolean checkTags(File file) {
		// Reading the file
		try {
			Scanner scanFile = new Scanner(file);
			while (scanFile.hasNext()) {
				String p = scanFile.next();

				// Searching for each tag
				int indexOpen = p.indexOf("<");
				if (indexOpen != -1) {
					int indexClose = p.indexOf(">") + 1;
					String tag = p.substring(indexOpen, indexClose);

					switch (tag) { // If its an open tag
					case "<242>":
					case "<section>":
					case "<infix>":
					case "<postfix>":
					case "<equation>":
						stack.push(tag, tagsHead);
						break;

					default: // If its an close tag
						if (stack.isEmpty(tagsHead))
							return false;
						String open = stack.pop(tagsHead);
						if (!open.equals(tag.replace("/", "")))
							return false;
					}
				}
			}
			scanFile.close();
			if (stack.isEmpty(tagsHead))
				return true;
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
		return false;
	}

	// Converting a Infix equation to Postfix
	public String infixToPostfix(String infix) {
		String postFix = "";
		String[] infixList = infix.split(" ");

		for (int i = 0; i < infixList.length; i++) {
			String nextCharacter = infixList[i];
			if (!nextCharacter.isBlank()) {
				switch (nextCharacter) {

				case "^":
				case "(":
					stack.push(nextCharacter, conversionHead);
					break;

				case "+":
				case "-":
				case "*":
				case "/":
					while (!stack.isEmpty(conversionHead)
							&& precedence(nextCharacter) <= precedence(stack.peek(conversionHead))) {
						postFix += stack.pop(conversionHead) + " ";
					}
					stack.push(nextCharacter, conversionHead); // Push the operator onto the stack
					break;

				case ")":
					String topOperator = stack.pop(conversionHead);
					while (!topOperator.equals("(")) {
						postFix += topOperator + " ";
						topOperator = stack.pop(conversionHead);
					}
					break;

				default:
					postFix += nextCharacter + " "; // Append operands to the postfix expression
					break;
				}
			}
		}

		while (!stack.isEmpty(conversionHead))
			postFix += stack.pop(conversionHead) + " ";

		return postFix;
	}

	// Taking the precedence of each operator
	private static int precedence(String s1) {
		switch (s1) {
		case "*":
		case "/":
			return 2;
		case "+":
		case "-":
			return 1;
		}
		return 0;
	}

	// Calculating the value of a Postfix equation
	public String calculatePostfix(String postfix) {
		String[] postfixList = postfix.split(" ");

		for (int i = 0; i < postfixList.length; i++) {
			String nextCharacter = postfixList[i];

			switch (nextCharacter) {
			case "+":
			case "-":
			case "*":
			case "/":
			case "^":
				String Operand1 = stack.pop(calculateHead);
				String Operand2 = stack.pop(calculateHead);
				stack.push(calculate(nextCharacter, Operand2, Operand1), calculateHead);
				break;
			default: // If its a variable
				stack.push(nextCharacter, calculateHead);
				break;
			}
		}
		return stack.pop(calculateHead);
	}

	// Converting postfix operation to prefix
	public String postfixToPrefix(String postfix) {
		String[] postfixList = postfix.split(" ");

		for (int i = 0; i < postfixList.length; i++) {
			String nextCharacter = postfixList[i];
			if (!nextCharacter.isBlank()) {
				switch (nextCharacter) {
				case "+":
				case "-":
				case "*":
				case "/":
					String operand1 = stack.pop(conversionHead);
					String operand2 = stack.pop(conversionHead);
					String temp = nextCharacter + " " + operand2 + " " + operand1;
					stack.push(temp, conversionHead);
					break;

				default: // If its operand
					stack.push(nextCharacter + " ", conversionHead);
					break;
				}
			}
		}

		String prefix = "";
		while (stack.peek(conversionHead) != null) {
			prefix += stack.pop(conversionHead);
		}
		return prefix;
	}

	public double calculatePrefix(String prefix) {
		String[] prefixList = prefix.split(" ");

		for (int i = 0; i < prefixList.length; i++) {
			String nextCharacter = prefixList[prefixList.length - 1 - i];

			if (!nextCharacter.isBlank()) {
				switch (nextCharacter) {
				case "+":
				case "-":
				case "*":
				case "/":
				case "^":
					String operand1 = stack.pop(calculateHead);
					String operand2 = stack.pop(calculateHead);
					if (operand1.equals("null") || operand2.equals("null"))
						return -1;
					stack.push(calculate(nextCharacter, operand1, operand2), calculateHead);
					break;
				default: // If its numeric
					stack.push(nextCharacter, calculateHead);
					break;
				}
			}
		}
		return Double.parseDouble(stack.pop(calculateHead));
	}

	// Calculating the result of a normal operation
	private String calculate(String operator, String operand1, String operand2) {
		double op1 = Double.parseDouble(operand1);
		double op2 = Double.parseDouble(operand2);

		switch (operator) {
		case "+":
			return String.valueOf(op1 + op2);
		case "-":
			return String.valueOf(op1 - op2);
		case "*":
			return String.valueOf(op1 * op2);
		case "/":
			return String.valueOf(op1 / op2);
		case "^":
			return String.valueOf(Math.pow(op1, op2));
		}
		return null;
	}

	// Saving the equations into the stack
	public void saveEquation(String equation) {
		stack.push(equation, equationsHead);
	}

	public String nextEquation() {
		String equation = stack.pop(equationsHead);
		stack.push(equation, displayHead);
		return equation;
	}

	public String prevEquation() {
		String equation = stack.pop(displayHead);
		stack.push(equation, equationsHead);
		return stack.peek(displayHead);
	}

	public boolean hasNext() {
		return !stack.isEmpty(equationsHead);
	}

	public boolean hasPrev() {
		String equation = stack.pop(displayHead);
		boolean hasPrev = stack.peek(displayHead) != null;
		stack.push(equation, displayHead);
		return hasPrev;
	}
}
