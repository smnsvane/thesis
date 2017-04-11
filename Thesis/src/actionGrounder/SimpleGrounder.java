package actionGrounder;

import java.util.Iterator;
import java.util.LinkedList;

import action.Action;
import action.GroundAction;

import state.State;

public class SimpleGrounder implements ActionGrounder
{
	@Override
	public LinkedList<GroundAction> getApplicableActions(State state)
	{
		LinkedList<GroundAction> list = new LinkedList<GroundAction>();
		
		for (Action action : state.getData().getActions())
		{
//			Utilities.printDebug("TRY TO GROUND "+action);
			
			SingleActionGrounder ag = new SingleActionGrounder(action, state);
			ag.groundAction();
			
			LinkedList<GroundAction> groundActions =
				ag.getGroundData().getGroundActions();
			
			if (groundActions.isEmpty())
				continue;
			
//			for (GroundAction ga : groundActions)
//				Utilities.printDebug("\t GROUND FOUND "+ga);
			
//			Utilities.printDebug("APPLICATION TESTING");
			for (Iterator<GroundAction> i = groundActions.iterator(); i.hasNext();)
			{
				GroundAction ga = i.next();
				
				if (ga.isApplicable(state))
//					Utilities.printDebug("PASSED: "+ga)
					;
				else
				{
//					Utilities.printDebug("FAILED: "+ga);
					i.remove();
				}
			}
			
			list.addAll(ag.getGroundData().getGroundActions());
		}
		
		return list;
	}
}
