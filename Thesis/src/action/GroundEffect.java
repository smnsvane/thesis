package action;

import state.State;

public interface GroundEffect
{
	public boolean isNegated();
	public void apply(State state);
}
