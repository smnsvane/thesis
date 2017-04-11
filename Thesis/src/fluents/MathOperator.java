package fluents;

import object.Assignment;

public class MathOperator implements FluentComparable
{
	private MathOperatorType operator;
	private FluentComparable left, right;

	public MathOperator(MathOperatorType operator,
			FluentComparable left, FluentComparable right)
	{
		this.operator = operator;
		this.left = left;
		this.right = right;
	}

	@Override
	public GroundFluentComparable ground(Assignment assignment)
	{
		GroundFluentComparable gLeft = left.ground(assignment);
		GroundFluentComparable gRight = right.ground(assignment);
		
		return new GroundMathOperator(operator, gLeft, gRight);
	}
}
