package fluents;

import state.State;

public class GroundMathOperator implements GroundFluentComparable
{
	private MathOperatorType operator;
	private GroundFluentComparable left, right;
	
	public GroundMathOperator(MathOperatorType operator,
			GroundFluentComparable left, GroundFluentComparable right)
	{
		this.operator = operator;
		this.left = left;
		this.right = right;
		
		if (right == null && !MathOperatorType.minus.equals(operator))
			throw new RuntimeException(
					"Internal exception - error in math operator parsing");
	}
	
	private boolean isUnaryMinus()
	{
		return operator.equals(MathOperatorType.minus) && right == null;
	}
	
	@Override
	public Double getValue(State state)
	{
		// left check
		if (left == null) return null;
		Double leftValue = left.getValue(state);
		if (leftValue == null) return null;
		
		// unary check
		if (isUnaryMinus())
			return leftValue * -1;
		
		// right check
		if (right == null) return null;
		Double rightValue = right.getValue(state);
		if (rightValue == null) return null;
		
		switch (operator)
		{
		case add:		return leftValue + rightValue;
		case multiply:	return leftValue * rightValue;
		case divide:	return leftValue / rightValue;
		case minus:		return leftValue - rightValue;
		default:
			throw new RuntimeException("Internal exception - unknown math operator");
		}
	}
}
