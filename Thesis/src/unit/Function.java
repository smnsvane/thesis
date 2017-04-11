package unit;

import java.util.HashMap;

import fluents.FluentComparable;

import object.Assignment;
import object.ObjectList;
import object.PDDLObject;

public class Function extends Unit implements FluentComparable
{
	public Function(String image, ObjectList parameters,
			HashMap<Integer, PDDLObject> fixedArguments)
	{
		super(image, parameters, fixedArguments);
	}
	
	@Override
	public GroundFunction ground(Assignment assignment)
	{
		ObjectList arguments = groundParameters(assignment);
		return new GroundFunction(getImage(), arguments);
	}
}
