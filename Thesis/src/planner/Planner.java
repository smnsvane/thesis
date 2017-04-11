package planner;

import meta.Timer;

import pddl.Requirement;
import state.Plan;
import state.State;

public interface Planner
{
	public boolean solve();
	public Plan getSolution();
	public void setStateExpansionLimit(int stateExpansionLimit);
	public Timer getTimer();
	public boolean support(Requirement req);
	public void setInitState(State init);
}
