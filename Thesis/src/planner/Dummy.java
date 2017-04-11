package planner;

import meta.Timer;
import pddl.Requirement;
import state.Plan;
import state.State;

public class Dummy implements Planner
{
	private State init;
	@Override
	public void setInitState(State init) { this.init = init; }
	@Override
	public boolean solve()
	{
		System.out.println(init.getData());
		init.lookUp(null);
		return false;
	}
	
	@Override
	public Plan getSolution() { return new Plan(); }
	@Override
	public Timer getTimer() { return new Timer(); }
	@Override
	public void setStateExpansionLimit(int stateExpansionLimit) { }
	@Override
	public boolean support(Requirement req) { return true; }
}
