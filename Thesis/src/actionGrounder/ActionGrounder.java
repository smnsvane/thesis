package actionGrounder;

import java.util.LinkedList;

import action.GroundAction;


import state.State;

public interface ActionGrounder
{
	public LinkedList<GroundAction> getApplicableActions(State state);
}
