package fluents;

import object.Assignment;

import state.State;

public class Constant implements FluentComparable, GroundFluentComparable
{
	private Double value;
	public Constant(double value) { this.value = value; }
	
	@Override
	public GroundFluentComparable ground(Assignment assignment)
	{
		return this;
	}
	
	@Override
	public Double getValue(State state)
	{
		return value;
	}
}
