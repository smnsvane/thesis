package fluents;

import action.GroundEffect;
import state.State;

import unit.GroundFunction;

public class GroundFluentEffect implements GroundEffect
{
	@Override
	public boolean isNegated() { return false; }

	private AssignmentOperator operator;
	private GroundFunction function;
	private GroundFluentComparable value;
	
	public AssignmentOperator getOperator() { return operator; }
	
	public GroundFluentEffect(AssignmentOperator operator,
			GroundFunction function, GroundFluentComparable value)
	{
		this.operator = operator;
		this.function = function;
		this.value = value;
	}
	
	@Override
	public void apply(State state)
	{
		Double oldValue = state.lookUp(function);
		double newValue;
		
		switch (operator)
		{
		case assign:	newValue = value.getValue(state); break;
		case increase:	newValue = oldValue + value.getValue(state); break;
		case decrease:	newValue = oldValue - value.getValue(state); break;
		case scaleup:	newValue = oldValue * value.getValue(state); break;
		case scaledown:	newValue = oldValue / value.getValue(state); break;
		default: throw new RuntimeException(
				"Unknown function assignment operator '"+operator+"'");
		}
		
		state.assign(function, newValue);
	}
}
