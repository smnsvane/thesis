package unit;

import java.util.HashMap;

import action.Effect;
import action.Precondition;

import object.Assignment;
import object.ObjectList;
import object.PDDLObject;

public class Predicate extends Unit implements Precondition, Effect
{
	private boolean negated;
	public boolean isNegated() { return negated; }
	public void setNegated(boolean negated) { this.negated = negated; }

	public Predicate(String image, boolean negated,
			ObjectList parameters,
			HashMap<Integer, PDDLObject> fixedArguments)
	{
		super(image, parameters, fixedArguments);
		this.negated = negated;
	}
	
	@Override
	public GroundPredicate ground(Assignment assignment)
	{
		ObjectList arguments = groundParameters(assignment);
		return new GroundPredicate(getImage(), negated, arguments);
	}
	
	@Override
	public String toString()
	{
		return (isNegated()?"¬":"")+super.toString();
	}
}
