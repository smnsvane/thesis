package unit;

import meta.Utilities;
import fluents.GroundFluentComparable;
import object.ObjectList;
import state.State;

public class GroundFunction extends GroundUnit implements GroundFluentComparable
{
	public GroundFunction(String image, ObjectList arguments)
	{
		super(image, arguments);
	}

	@Override
	public Double getValue(State state)
	{
		Double value = state.lookUp(this);
		
		if (value == null)
			Utilities.printWarning("Value lookup of "+this+" failed");
		
		return value;
	}
}
