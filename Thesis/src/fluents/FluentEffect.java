package fluents;

import object.Assignment;

import action.Effect;
import action.GroundEffect;

import unit.Function;
import unit.GroundFunction;

public class FluentEffect implements Effect
{
	private AssignmentOperator operator;
	private Function function;
	private FluentComparable value;
	
	public FluentEffect(AssignmentOperator operator,
			Function function, FluentComparable value)
	{
		this.operator = operator;
		this.value = value;
		this.function = function;
	}

	@Override
	public GroundEffect ground(Assignment assignment)
	{
		GroundFunction gf = function.ground(assignment);
		GroundFluentComparable gfc = value.ground(assignment);
		return new GroundFluentEffect(operator, gf, gfc);
	}
}
