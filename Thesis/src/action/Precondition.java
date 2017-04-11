package action;

import object.Assignment;

public interface Precondition
{
	public GroundPrecondition ground(Assignment assignment);
}
