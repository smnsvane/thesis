package planner;

import static pddl.Requirement.*;

import java.util.HashSet;
import java.util.LinkedList;

import meta.Timer;

import action.GroundAction;
import actionGrounder.ActionGrounder;
import actionGrounder.SimpleGrounder;

import pddl.Requirement;

import state.Plan;
import state.State;

public class BreathFirstClean implements Planner
{
	private int stateExpansionLimit;
	@Override
	public void setStateExpansionLimit(int stateExpansionLimit)
	{
		this.stateExpansionLimit = stateExpansionLimit;
	}
	
	private State currentState;
	
	@Override
	public void setInitState(State init)
	{
		open.add(init);
	}
	
	private Timer timer = new Timer();
	@Override
	public Timer getTimer() { return timer; }
	
	@Override
	public Plan getSolution() { return currentState.getPath(); }
	
	private HashSet<State> closed =
		new HashSet<State>();
	private LinkedList<State> open =
		new LinkedList<State>();
	
	@Override
	public boolean solve()
	{
		ActionGrounder grounder = new SimpleGrounder();
		
		while (!open.isEmpty())
		{
			currentState = open.removeFirst();
			
			if (currentState.isGoal())
				return true;
			
			closed.add(currentState);
			
			LinkedList<GroundAction> groundActions =
				grounder.getApplicableActions(currentState);
			
			for (GroundAction ga : groundActions)
			{
				State childState =
					currentState.doTransition(ga);
				
				if (closed.contains(childState) || open.contains(childState))
					continue;
				
				if (open.size() + closed.size() > stateExpansionLimit)
					return false;
				
				open.add(childState);
			}
		}
		
		return false;
	}
	
	@Override
	public boolean support(Requirement req)
	{
		return
			req.equals(strips) ||
			req.equals(equality) ||
			req.equals(typing) ||
			req.equals(negative_preconditions) ||
			req.equals(fluents) ||
			req.equals(shadow);
	}
	
//	private void debug(State state, String image, String...argImages)
//	{
//		List<PDDLObject> list = new ArrayList<PDDLObject>();
//		for (int i = 0; i < argImages.length; i++)
//			list.add(new PDDLObject(argImages[i], null));
//		ObjectList args = new ObjectList(list, true);
//		System.out.println(state.lookUp(new GroundFunction(image, args)));
//	}
}
