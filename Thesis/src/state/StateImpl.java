package state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import fluents.AssignmentOperator;
import fluents.GroundFluentEffect;

import object.ObjectList;
import object.PDDLObject;

import action.GroundAction;
import action.GroundEffect;
import action.GroundPrecondition;
import actionGrounder.ParameterGrounder;

import pddl.Data;
import unit.GroundFunction;
import unit.GroundPredicate;
import unit.Predicate;

public class StateImpl implements State, InitState
{
	private Data data;
	@Override
	public Data getData() { return data; }
	
	private HashSet<GroundPredicate> global;
	private HashMap<String, ArrayList<GroundPredicate>> globalByImage;
	
	private ArrayList<GroundPredicate> dynamic;
	
	private HashMap<GroundFunction, Double> functionValue;
	
	// distance to root / init state (0 indicates that this is the init state)
	private int gScore;
	@Override
	public int getGScore() { return gScore; }
	
	public StateImpl(Data data)
	{
		this.data = data;
		gScore = 0;
		
		node = new GraphNode(null, null, this);
		
		dynamic = new ArrayList<GroundPredicate>();
		global = new HashSet<GroundPredicate>();
		globalByImage = new HashMap<String, ArrayList<GroundPredicate>>();
		functionValue = new HashMap<GroundFunction, Double>();
	}
	private StateImpl(Collection<GroundPredicate> dynamic,
			HashMap<GroundFunction, Double> functionValue, Data data)
	{
		this.data = data;
		this.dynamic = new ArrayList<GroundPredicate>(dynamic);
		this.functionValue = new HashMap<GroundFunction, Double>(functionValue);
	}
	
	private GraphNode node;
	
	@Override
	public int size() { return dynamic.size(); }
	
	@Override
	public boolean cwaContains(GroundPredicate predicate)
	{
		return global.contains(predicate) || dynamic.contains(predicate);
	}
	
	@Override
	public void add(GroundPredicate gp)
	{
		if (!dynamic.contains(gp))
		{
			dynamic.add(gp);
			hashCode += gp.hashCode();
		}
	}
	@Override
	public void remove(GroundPredicate gp)
	{
		if (dynamic.contains(gp))
		{
			dynamic.remove(gp);
			hashCode -= gp.hashCode();
		}
	}
	@Override
	public void assign(GroundFunction function, double value)
	{
		Double oldValue = functionValue.put(function, value);
		
		if (oldValue != null)
			value -= oldValue;
		
		hashCode += value * 1000;
	}
	@Override
	public Double lookUp(GroundFunction key)
	{
		return functionValue.get(key);
	}
	
	@Override
	public State doTransition(GroundAction gAction)
	{
		StateImpl child = new StateImpl(dynamic, functionValue, data);
		child.hashCode = hashCode;
		child.global = global;
		child.globalByImage = globalByImage;
		child.node = new GraphNode(node, gAction, child);
		child.gScore = gScore + 1;
		
		// list for containing the non-negated predicates
		List<GroundPredicate> positivePredicates = new ArrayList<GroundPredicate>();
		
		for (GroundEffect ge : gAction.getGroundEffects())
		{
			if (ge.isNegated())
				positivePredicates.add((GroundPredicate) ge);
			ge.apply(child);
		}
		
		for (GroundPredicate gp : positivePredicates)
			gp.apply(child);
		
		return child;
	}
	
	private int hashCode = 0;
	@Override
	public int hashCode() { return hashCode; }
	
	@Override
	public Iterator<GroundPredicate> iterator()
	{
		return dynamic.iterator();
	}
	
	@Override
	public ArrayList<GroundPredicate> getPredicates(
			Predicate predicate,
			PDDLObject... arguments)
	{
		if (predicate.isNegated())
			return getNegatedPredicates(predicate, arguments);
		else
			return getPositivePredicates(predicate, arguments);
	}
	
	private ArrayList<GroundPredicate> getPositivePredicates(
			Predicate predicate,
			PDDLObject... arguments)
	{
		ArrayList<GroundPredicate> list = null;
		
		if (data.isDynamic(predicate.getImage()))
		{
			list = new ArrayList<GroundPredicate>();
			for (GroundPredicate p : dynamic)
				if (p.getImage().equals(predicate.getImage()))
					list.add(p);
		}
		else if (globalByImage.containsKey(predicate.getImage()))
			list = globalByImage.get(predicate.getImage());
		else
			list = new ArrayList<GroundPredicate>();
		
		// !list.isEmpty() is needed by the filterPredicates method
		if (arguments != null && !list.isEmpty())
			filterPredicates(list, arguments);
		
		return list;
	}
	private ArrayList<GroundPredicate> getNegatedPredicates(
			Predicate predicate,
			PDDLObject... arguments)
	{
		if (arguments != null)
			throw new RuntimeException("Internal exception: argument use not implemented");
		
		ArrayList<PDDLObject> params = new ArrayList<PDDLObject>();
		for (int i = 0; i < predicate.getParameters().length(); i++)
			params.add(new PDDLObject("param"+i, predicate.getParameters().get(i).getType()));
		ParameterGrounder pg = new ParameterGrounder(data, params);
		
		ArrayList<GroundPredicate> list = new ArrayList<GroundPredicate>();
		for (HashMap<PDDLObject, PDDLObject> assignment : pg)
		{
			ObjectList args = new ObjectList();
			for (int i = 0; i < params.size(); i++)
			{
				PDDLObject arg = assignment.get(params.get(i));
				args.getObjectList().add(arg);
			}
			args.makeImmutable();
			GroundPredicate gp = new GroundPredicate(predicate.getImage(),
					true, args);
			
			if (gp.isTrue(this))
				list.add(gp);
		}
		
		return list;
	}
	private void filterPredicates(ArrayList<GroundPredicate> list,
			PDDLObject[] arguments)
	{
		String predicateImage = list.get(0).getImage();
		if (arguments.length != data.getPredicate(predicateImage).getTypeList().size())
			throw new RuntimeException("Inconsistency in implementation");
		
		for (int i = 0; i < arguments.length; i++)
		{
			if (arguments[i] == null)
				continue;
			
			for (Iterator<GroundPredicate> it = list.iterator(); it.hasNext();)
			{
				GroundPredicate gp = it.next();
				
				PDDLObject arg = gp.getArguments().get(i);
				if (arg != arguments[i])
					it.remove();
			}
		}
	}
	
	private int h = -1;
	@Override
	public int heuristic()
	{
		if (h == -1)
		{
			h = data.getGoal().size();
			for (GroundPrecondition gp : data.getGoal())
				if (gp.isTrue(this))
					h--;
		}
		return h;
	}
	@Override
	public boolean isGoal()
	{
		if (h == -1)
			return heuristic() == 0;
		return h == 0;
	}
	
	@Override
	public int compareTo(State other)
	{
		return heuristic() - other.heuristic();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			throw new RuntimeException();
		
		if (!(obj instanceof State))
			return false;
		
		State other = (State) obj;
		
		if (size() != other.size())
			return false;
		
		for (GroundPredicate gp : dynamic)
			if (!other.cwaContains(gp))
				return false;
		
		return true;
	}
	
	@Override
	public String toString()
	{
		Collections.sort(dynamic);
		return dynamic.toString();
	}
	
	@Override
	public Plan getPath()
	{
		Plan plan;
		
		if (node.getParent() != null)
		{
			plan = node.getParent().getState().getPath();
			plan.addToTail(node.getTransition());
			return plan;
		}
		return new Plan();
	}
	
	/* ***********************
	 * INIT STATE METHODS
	 *************************/
	
	@Override
	public void initialize(GroundPredicate predicate)
	{
		if (data.isDynamic(predicate.getImage()))
			predicate.apply(this);
		else
		{
			//FIXME: set predicate age
			if (!globalByImage.containsKey(predicate.getImage()))
				globalByImage.put(predicate.getImage(),
						new ArrayList<GroundPredicate>());
			globalByImage.get(predicate.getImage()).add(predicate);
			global.add(predicate);
		}
	}
	
	@Override
	public void initialize(GroundFluentEffect functionEffect)
	{
		AssignmentOperator operator = functionEffect.getOperator();
		
		if (!AssignmentOperator.assign.equals(operator))
			throw new RuntimeException("Internal exception -"+
					" failed function assignment when creating initial state");
		
		functionEffect.apply(this);
	}
}
