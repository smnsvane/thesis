package fluents;

import state.State;
import action.GroundPrecondition;

public class GroundFluentPrecondition implements GroundPrecondition
{
	private FluentComparator comp;
	private GroundFluentComparable left, right;
	
	public GroundFluentPrecondition(FluentComparator comp,
			GroundFluentComparable left, GroundFluentComparable right)
	{
		this.comp = comp;
		this.left = left;
		this.right = right;
	}
	
	@Override
	public boolean isTrue(State state)
	{
		Double leftValue	= left.getValue(state);
		Double rightValue	= right.getValue(state);
		
		if (leftValue == null || rightValue == null)
			return false;
		
		switch (comp)
		{
		case less:			return leftValue.compareTo(rightValue) <  0;
		case lessEqual:		return leftValue.compareTo(rightValue) <= 0;
		case equal:			return leftValue.compareTo(rightValue) == 0;
		case notEqual:		return leftValue.compareTo(rightValue) != 0;
		case greater:		return leftValue.compareTo(rightValue) >  0;
		case greaterEqual:	return leftValue.compareTo(rightValue) >= 0;
		default:
			throw new RuntimeException("Unknown comparator type");
		}
	}
}
