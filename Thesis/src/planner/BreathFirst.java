package planner;

import static pddl.Requirement.*;

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

public class BreathFirst implements Planner
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
		ActionGrounder grounder = new SimpleGrounder();
		
		while (!open.isEmpty())
		{
			currentState = open.removeFirst();
			
//			Utilities.printDebug("h-score: "+currentState.heuristic());
//			Utilities.printDebug("open size: "+open.size());
			
			Utilities.printDebug("\nState choosen: "+currentState);
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
			
			LinkedList<GroundAction> groundActions =
				grounder.getApplicableActions(currentState);
			
			timer.stop("ground");

//			Utilities.printDebug("Action grounds found:\n"+Utilities.implode(groundedActions,"\n"));			
			
			for (GroundAction ga : groundActions)
			{
				timer.start("transition");
				
				State childState =
					currentState.doTransition(ga);
				
				timer.stop("transition");
				
				Utilities.printDebug("Action:"+ga);
				Utilities.printDebug("Child: "+childState);
				
				timer.start("hash/equals");
				
				if (closed.contains(childState))
				{
					timer.stop("hash/equals");
					Utilities.printDebug("Found state was in closed set");
					continue;
				}
				if (open.contains(childState))
				{
					timer.stop("hash/equals");
					Utilities.printDebug("Found state was in open list");
					continue;
				}
				
				timer.stop("hash/equals");
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
				
				open.add(childState);
			}
		}
		timer.stopMaster();
		
		System.out.print("Could not reach goal");
		
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
