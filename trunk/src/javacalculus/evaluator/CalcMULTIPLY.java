package javacalculus.evaluator;

import javacalculus.struct.*;
import javacalculus.core.*;

/**
 * Evaluator that handles multiplication of expressions. Handles basic simplification.
 * @author Duyun Chen <A HREF="mailto:duchen@seas.upenn.edu">[duchen@seas.upenn.edu]</A>,
 * Seth Shannin <A HREF="mailto:sshannin@seas.upenn.edu">[sshannin@seas.upenn.edu]</A>
 *  
 *
 */
public class CalcMULTIPLY extends CalcNParamFunctionEvaluator implements CalcOperatorEvaluator {

	@Override
	protected CalcObject evaluateObject(CalcObject input1, CalcObject input2) {
		if (input1.equals(CALC.ZERO) || input2.equals(CALC.ZERO)) {
			return CALC.ZERO;
		}
		if (input1.equals(CALC.ONE)) {
			return input2;
		}
		if (input2.equals(CALC.ONE)) {
			return input1;
		}
		if (input1.equals(input2)) {
			return CALC.POWER.createFunction(input1, CALC.TWO);
		}
		if (input1.getHeader() == CALC.POWER && ((CalcFunction)input1).size() == 2) {
			CalcFunction function1 = (CalcFunction)input1;
			if (function1.get(1).isNumber()) {
				if (function1.get(0).equals(input2)) {
					return CALC.POWER.createFunction(input2, CALC.ADD.createFunction(CALC.ONE, function1.get(1)));
				}
				if (input2.getHeader().equals(CALC.POWER) && ((CalcFunction)input2).size() == 2) {
					CalcFunction function2 = (CalcFunction)input2;
					if (function2.get(1).isNumber()) {
						if (function1.get(0).equals(function2.get(0))) {
							return CALC.POWER.createFunction(function1.get(0), CALC.ADD.createFunction(function1.get(1), function2.get(1)));
						}
					}
				}
			}
		}
		
		if (input2.getHeader().equals(CALC.POWER) && ((CalcFunction)input2).size() == 2) {
			CalcFunction function2 = (CalcFunction)input2;
			if (function2.get(0).equals(input1)) {
				return CALC.POWER.createFunction(input1, CALC.ADD.createFunction(CALC.ONE, function2.get(1)));
			}
		}
		if (input1 instanceof CalcSymbol) {
			if (input2 instanceof CalcSymbol) {
				return CALC.MULTIPLY.createFunction(input1, input2);
			}
			else if (input2 instanceof CalcFunction) {
				CalcFunction function2 = (CalcFunction) input2;
				CalcFunction function = CALC.MULTIPLY.createFunction(input1, input2);
				return function.evaluateParameters();
			}
		}
		
		return null;
	}
	
	@Override
	protected CalcObject evaluateInteger(CalcInteger input1, CalcInteger input2) {
		return input1.multiply(input2);
	}
	
	@Override
	protected CalcObject evaluateDouble(CalcDouble input1, CalcDouble input2) {
		return input1.multiply(input2);
	}

	@Override
	protected CalcObject evaluateFraction(CalcFraction input1, CalcFraction input2) {
		return input1.multiply(input2);
	}

	@Override
	protected CalcObject evaluateFunction(CalcFunction input1, CalcFunction input2) {
		return null;
	}

	@Override
	protected CalcObject evaluateFunctionAndInteger(CalcFunction input1, CalcInteger input2) {
		return null;
	}

	@Override
	protected CalcObject evaluateSymbol(CalcSymbol input1, CalcSymbol input2) {
		return null;
	}

	@Override
	public int getPrecedence() {
		return 300;
	}


	@Override
	public String toOperatorString(CalcFunction function) {
		int precedence = getPrecedence();
		char operatorChar = '*';
		StringBuffer buffer = new StringBuffer();
		CalcObject temp;
		
		for (int ii = 0; ii < function.size(); ii++) {
			temp = function.get(ii);
			//a*1/x -> a/x
			if (temp instanceof CalcFunction && 	//handle '/' cases
					temp.getHeader().equals(CALC.POWER) &&
					((CalcFunction)temp).get(1).compareTo(CALC.ZERO) < 0) {
				CalcObject temp2 = ((CalcFunction)temp).get(0);
				buffer.deleteCharAt(buffer.length() - 1);
				buffer.append('/');
				if (temp2 instanceof CalcFunction) {
					buffer.append('(');	//embedded function -> parenthesis required
				}
				buffer.append(((CalcFunction)temp).get(0));
				if (temp2 instanceof CalcFunction) {
					buffer.append(')');
				}				
				continue;
			}
			if (temp.equals(CALC.NEG_ONE)) {
				buffer.append('-');	//unary negative
			}
			else {
				if (temp.getPrecedence() < precedence) {
					buffer.append('('); //handle parenthesis
				}
				
				buffer.append(temp.toString());
				
				if (temp.getPrecedence() < precedence) {
					buffer.append(')');
				}
				if (ii != function.size()-1) {
					buffer.append(operatorChar); //insert '*' between every parameter
				}
			}
		}
		
		return buffer.toString();
	}
	
}
