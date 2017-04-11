package action;

import state.State;

public interface GroundPrecondition
{
	public boolean isTrue(State state);
}
