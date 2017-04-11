package fluents;

import action.Precondition;

import object.Assignment;

public class FluentPrecondition implements Precondition
{
	private FluentComparator comp;
	private FluentComparable left, right;
	
	public FluentPrecondition(FluentComparator comp,
			FluentComparable left, FluentComparable right)
	{
		this.comp = comp;
		this.left = left;
		this.right = right;
	}
	
	@Override
	public GroundFluentPrecondition ground(Assignment assignment)
	{
		GroundFluentComparable gLeft = left.ground(assignment);
		GroundFluentComparable gRight = right.ground(assignment);
		
		return new GroundFluentPrecondition(comp, gLeft, gRight);
	}
}
