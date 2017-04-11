package state;

import java.util.ArrayList;

import action.GroundAction;

import object.PDDLObject;

import pddl.Data;
import unit.GroundFunction;
import unit.GroundPredicate;
import unit.Predicate;

public interface State extends Iterable<GroundPredicate>, Comparable<State>
{
	public boolean isGoal();
	public int heuristic();
	public int getGScore();
	
	public int size();
	boolean cwaContains(GroundPredicate predicate);
	public ArrayList<GroundPredicate> getPredicates(
			Predicate predicate, PDDLObject... arguments);
	
	public Plan getPath();
	public Data getData();
	
	public Double lookUp(GroundFunction key);
	public void assign(GroundFunction function, double value);
	
	public void add(GroundPredicate gp);
	public void remove(GroundPredicate gp);
	
	public State doTransition(GroundAction gAction);
}
