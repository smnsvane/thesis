package unit;

import action.GroundEffect;
import action.GroundPrecondition;
import object.ObjectList;
import state.State;

public class GroundPredicate extends GroundUnit implements GroundPrecondition, GroundEffect
{
	private boolean negated;
	@Override
	public boolean isNegated() { return negated; }
	
	private int creationTime;
	public int getCreationTime() { return creationTime; }
	
	public GroundPredicate(String image, boolean negated, ObjectList arguments)
	{
		super(image, arguments);
		this.negated = negated;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof GroundPredicate))
			return false;
		
		return super.equals(obj);
	}
	
	@Override
	public String toString()
	{
		return (negated?"¬":"")+super.toString();
	}
	
	@Override
	public void apply(State state)
	{
		if (negated)
			state.remove(this);
		else
		{
			creationTime = state.getGScore();
			state.add(this);
		}
	}

	@Override
	public boolean isTrue(State state)
	{
		return state.cwaContains(this) ^ negated;
	}
}
