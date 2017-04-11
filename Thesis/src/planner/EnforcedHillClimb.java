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

public class EnforcedHillClimb implements Planner
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
	
	private Timer timer = new Timer();
	@Override
	public Timer getTimer() { return timer; }
	
	@Override
	public boolean solve()
	{
		timer.startMaster();
		
		ActionGrounder grounder = new SimpleGrounder();
		
		LinkedList<State> open = new LinkedList<State>();
		open.add(initState); // add init state to open list
		
		HashSet<State> closed = new HashSet<State>();
		
		int bestHeuristic = initState.heuristic();
		
		while (!open.isEmpty())
		{
			currentState = open.pop();
			
			timer.start("hash/equals");
			
			// remember that we have been in this state
			closed.add(currentState);
			
			timer.stop("hash/equals");
			timer.start("ground");
			
			// calculate all applicable actions
			LinkedList<GroundAction> groundActions =
				grounder.getApplicableActions(currentState);
			
			timer.stop("ground");
			
			while (!groundActions.isEmpty())
			{
				timer.start("transition");
				
				// calculate next successor / child
				State child = currentState.doTransition(groundActions.pop());
				
				timer.stop("transition");
				timer.start("hash/equals");
				
				boolean knowChild =
					closed.contains(child) || open.contains(child);
				
				timer.stop("hash/equals");
				
				// check to see if we already know this state
				if (knowChild)
					continue;
				
				timer.start("heuristic");
				
				if (child.isGoal())
				{
					timer.stop("heuristic");
					timer.stopMaster();
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
				
				timer.stop("heuristic");
				
				open.add(child);
				
				timer.start("maintain exp-limit");
				
				if (open.size() + closed.size() > stateExpansionLimit)
				{
					timer.stop("maintain exp-limit");
					timer.stopMaster();
					System.out.println("State expansion limit reached ("+
							stateExpansionLimit+")");
					return false;
				}
				
				timer.stop("maintain exp-limit");
			}
		}
		timer.stopMaster();
		
		System.out.println("Unable to reach goal");
		
		return false;
	}
	@Override
	public Plan getSolution() { return currentState.getPath(); }
	

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
