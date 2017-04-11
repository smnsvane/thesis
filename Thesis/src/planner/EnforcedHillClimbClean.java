package planner;

import static pddl.Requirement.*;

import java.util.HashSet;
import java.util.LinkedList;

import action.GroundAction;
import actionGrounder.ActionGrounder;
import actionGrounder.SimpleGrounder;
import meta.Timer;
import pddl.Requirement;
import state.Plan;
import state.State;

public class EnforcedHillClimbClean implements Planner
{
	private int stateExpansionLimit;
	@Override
	public void setStateExpansionLimit(int stateExpansionLimit)
	{
		this.stateExpansionLimit = stateExpansionLimit;
	}
	
	private State currentState, initState;
	@Override
	public void setInitState(State init) { initState = init; }
	
	@Override
	public boolean solve()
	{
		ActionGrounder grounder = new SimpleGrounder();
		
		LinkedList<State> open = new LinkedList<State>();
		open.add(initState); // add init state to open list
		
		HashSet<State> closed = new HashSet<State>();
		
		int bestHeuristic = initState.heuristic();
		
		while (!open.isEmpty())
		{
			currentState = open.pop();
			
			// remember that we have been in this state
			closed.add(currentState);
			
			// calculate all applicable actions
			LinkedList<GroundAction> groundActions =
				grounder.getApplicableActions(currentState);
			
			while (!groundActions.isEmpty())
			{
				// calculate next successor / child
				State child = currentState.doTransition(groundActions.pop());
				
				// check to see if we already know this state
				if (closed.contains(child) || open.contains(child))
					continue;
				
				if (child.isGoal())
				{
					// setting currentState to goal
					// so that getSolution will return the correct solution
					currentState = child;
					System.out.println("Solution found");
					return true;
				}
				
				if (child.heuristic() < bestHeuristic)
				{
					open.clear();
					// must be included to ensure completeness
					closed.clear();
					bestHeuristic = child.heuristic();
				}
				
				open.add(child);
				
				if (open.size() + closed.size() > stateExpansionLimit)
				{
					System.out.println("State expansion limit reached ("+
							stateExpansionLimit+")");
					return false;
				}
			}
		}
		
		System.out.println("Unable to reach goal");
		return false;
	}
	@Override
	public Plan getSolution() { return currentState.getPath(); }
	
	@Override
	public Timer getTimer() { return new Timer(); }

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
}
