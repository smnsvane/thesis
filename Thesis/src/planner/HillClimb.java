package planner;

import static pddl.Requirement.*;

import java.util.LinkedList;

import action.GroundAction;
import actionGrounder.ActionGrounder;
import actionGrounder.SimpleGrounder;
import meta.Timer;
import pddl.Requirement;
import state.Plan;
import state.State;

public class HillClimb implements Planner
{
	@Override
	public void setStateExpansionLimit(int stateExpansionLimit) {}
	
	private State currentState;
	@Override
	public void setInitState(State init) { currentState = init; }
	
	@Override
	public boolean solve()
	{
		while (true)
		{
			ActionGrounder grounder = new SimpleGrounder();
			LinkedList<GroundAction> groundActions = grounder.getApplicableActions(currentState);
			
			int bestSuccessorHeuristic = Integer.MAX_VALUE;
			State bestChild = null;
			
			for (GroundAction transition : groundActions)
			{
				State child = currentState.doTransition(transition);
				
				if (child.isGoal())
				{
					// setting currentState to goal
					// so that getSolution will return the correct solution
					currentState = child;
					System.out.println("Solution found");
					return true;
				}
				
				if (child.heuristic() < bestSuccessorHeuristic)
				{
					bestChild = child;
					bestSuccessorHeuristic = child.heuristic();
				}
				
				if (bestSuccessorHeuristic > currentState.heuristic())
				{
					// give up - we are in a local minimum
					System.out.println("Failed by local heuristic minimum");
					return false;
				}
				
				currentState = bestChild;
		   }
	   }
	}
	@Override
	public Plan getSolution() { return currentState.getPath(); }
	
	@Override
	public Timer getTimer()
	{
		return new Timer();
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
}
