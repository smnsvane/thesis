package planner;

import static pddl.Requirement.*;


import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;

import meta.Timer;
import meta.Utilities;

import action.GroundAction;
import actionGrounder.ActionGrounder;
import actionGrounder.SimpleGrounder;

import pddl.Requirement;

import state.Plan;
import state.State;

public class BestFirst implements Planner
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
		timer.startMaster();
		
		while (!open.isEmpty())
		{
			Collections.sort(open);
			
			currentState = open.removeLast();
			
			Utilities.printDebug("h score: "+currentState.heuristic());
			Utilities.printDebug("open size: "+open.size());
			
			timer.start("hash/equals");
			boolean wasInClosed = closed.contains(currentState);
			timer.stop("hash/equals");
			
			if (wasInClosed)
				continue;
			
//			Utilities.printDebug("\nState choosen: "+currentState);
//			Utilities.printDebug(closed);
//			System.out.println(closed.size()+" "+open.size()+" "+statesExplored);
			
			if (currentState.isGoal())
			{
				timer.stopMaster();
				System.out.println("Solution found ("+closed.size()+" states expanded, "+ (closed.size()+open.size()) +" states visited)");
				return true;
			}
			
			timer.start("hash/equals");
			
			closed.add(currentState);
			
			timer.stop("hash/equals");
			
			
			timer.start("ground");
			
			ActionGrounder grounder = new SimpleGrounder();
			LinkedList<GroundAction> groundedActions = grounder.getApplicableActions(currentState);
			
//			Utilities.printDebug("Action grounds found:\n"+Utilities.implode(groundedActions,"\n"));
			
			timer.stop("ground");
			
			for (GroundAction ga : groundedActions)
			{
				timer.start("update");
				
				State childState =
					currentState.doTransition(ga);
				
//				Utilities.printDebug("Child found: "+childState);
				
				timer.stop("update");
				timer.start("hash/equals");
				
				if (closed.contains(childState))
				{
					timer.stop("hash/equals");
//					Utilities.printDebug("Found state was in closed list");
					continue;
				}
				
				timer.stop("hash/equals");
				timer.start("maintain plan");
				
				if (open.size() + closed.size() > stateExpansionLimit)
				{
					timer.stop("maintain plan");
					timer.stopMaster();
					System.out.println("State expansion limit reached ("+
							stateExpansionLimit+")");
					return false;
				}
				
				timer.stop("maintain plan");
				
				open.add(childState);
			}
		}
		System.out.print("Could not reach goal");
		
		timer.stopMaster();
		
		return false;
	}
	
	@Override
	public boolean support(Requirement req)
	{
		return
			req.equals(strips) ||
			req.equals(equality) ||
			req.equals(typing) ||
			req.equals(negative_preconditions);
	}
}
