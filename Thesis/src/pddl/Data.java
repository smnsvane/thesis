package pddl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import meta.Utilities;

import object.PDDLObject;

import action.Action;
import action.GroundPrecondition;

import state.InitState;
import state.State;
import unit.AbstractFunction;
import unit.AbstractPredicate;
import unit.Predicate;

import static pddl.Requirement.*;

public class Data
{
	/* ***************************************************************************
	 *		DOMAIN NAME
	 *****************************************************************************/
	private String domainName;
	public String getDomainName() { return domainName; }
	public void setDomainName(String name) { domainName = name; }
	
	/* ***************************************************************************
	 *		REQUIREMENTS
	 *****************************************************************************/
	private HashSet<Requirement> instanceRequirements = new HashSet<Requirement>();
	public void addRequirement(Requirement req) { instanceRequirements.add(req); }
	public boolean instanceSupport(Requirement req) { return instanceRequirements.contains(req); }
	public boolean instanceSupports(Collection<Requirement> reqs)
	{
		return instanceRequirements.containsAll(reqs);
	}
	public HashSet<Requirement> getInstanceRequirements() { return instanceRequirements; }
	
	/* ***************************************************************************
	 *		ATOMS AND TYPES
	 *****************************************************************************/
	private ArrayList<PDDLObject> constants = new ArrayList<PDDLObject>();
	public ArrayList<PDDLObject> getConstants() { return constants; }
	
	private HashMap<String, PDDLObject> constantByImage = new HashMap<String, PDDLObject>();
	public PDDLObject getObject(String image) { return constantByImage.get(image); }
	public boolean containConstant(PDDLObject constant) { return constantByImage.containsKey(constant.getImage()); }
	
	private HashMap<String, ArrayList<PDDLObject>> constantsByType = new HashMap<String, ArrayList<PDDLObject>>();
	public ArrayList<PDDLObject> getConstantsByType(String type) { return constantsByType.get(type); }
	public void addType(String type) { constantsByType.put(type, new ArrayList<PDDLObject>()); }
	public boolean containsType(String type) { return constantsByType.containsKey(type); }
	
	public void addObject(PDDLObject atom)
	{
		if (constantByImage.containsKey(atom.getImage()))
		{
			Utilities.printWarning("Atom '"+atom+"' already known");
			return;
		}
		
		constantByImage.put(atom.getImage(), atom);
		constants.add(atom);
		
		if (atom.getType() == null)
			return;
		if (!constantsByType.containsKey(atom.getType()))
		{
			constantsByType.put(atom.getType(), new ArrayList<PDDLObject>());
			Utilities.printWarning("New type '"+atom.getType()+"' found outside type-def");
		}
		constantsByType.get(atom.getType()).add(atom);
	}
	public void addAtoms(Collection<PDDLObject> atoms) { for (PDDLObject atom : atoms) addObject(atom); }
	
	/* ***************************************************************************
	 *		PREDICATES
	 *****************************************************************************/
	private HashMap<String, AbstractPredicate> predicates = new HashMap<String, AbstractPredicate>();
	public void addAbstractPredicate(AbstractPredicate predicate)
	{
		predicates.put(predicate.getImage(), predicate);
	}
	public AbstractPredicate getPredicate(String name) { return predicates.get(name); }
	
	/* ***************************************************************************
	 *		FUNCTIONS
	 *****************************************************************************/
	private HashMap<String, AbstractFunction> functions = new HashMap<String, AbstractFunction>();
	public void addFunction(AbstractFunction function)
	{
		functions.put(function.getImage(), function);
	}
	public AbstractFunction getFunction(String name) { return functions.get(name); }
	
	/* ***************************************************************************
	 *		SHADOW
	 *****************************************************************************/
	private HashMap<Predicate, Predicate> lightToShadow = new HashMap<Predicate, Predicate>();
	public void addShadow(Predicate lightTaker, Predicate shadowed)
	{
		lightToShadow.put(lightTaker, shadowed);
	}
	public Predicate getShadowed(Predicate light) { return lightToShadow.get(light); }
	public Set<Predicate> getLightTakers() { return lightToShadow.keySet(); }
	
	/* ***************************************************************************
	 *		ACTIONS
	 *****************************************************************************/
	private ArrayList<Action> actions = new ArrayList<Action>();
	public void addAction(Action action) { actions.add(action); }
	public ArrayList<Action> getActions() { return actions; }
	
	private HashSet<String> dynamicPredicates = new HashSet<String>();
	public void setDynamic(String predicateImage)
	{
		dynamicPredicates.add(predicateImage);
	}
	public boolean isDynamic(String predicateImage)
	{
		return dynamicPredicates.contains(predicateImage);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\ntoString output from "+this.getClass()+"\n");
		sb.append("(define (domain "+getDomainName()+")\n");
		sb.append("\t(:requirements "+Utilities.implode(instanceRequirements, " ")+")\n");
		if (!constantsByType.keySet().isEmpty())
			sb.append("\t(:types "+Utilities.implode(constantsByType.keySet(), " ")+")\n");
		if (!constants.isEmpty())
			sb.append("\t(:constants "+Utilities.implode(constants, " ")+")\n");
		sb.append("\t(:predicates "+Utilities.implode(predicates.values(), " ")+")\n");
		if (instanceSupport(fluents))
			sb.append("\t(:functions "+Utilities.implode(functions.values(), " ")+")\n");
		for (Action act : actions)
			sb.append("\t(:action "+act+")\n");
		sb.append("\t(:init\n"+init+"\n");
		sb.append("\t(:goal\n"+goal+"\n");
		sb.append(")");
		
		return sb.toString();
	}
	
	/* ***************************************************************************
	 *		PROBLEM NAME
	 *****************************************************************************/
	private String problemName;
	public String getProblemName() { return problemName; }
	public void setProblemName(String name) { problemName = name; }
	
	/* ***************************************************************************
	 *		PROBLEM - INITIAL STATE AND GOAL STATE
	 *****************************************************************************/
	private State init;
	public State getInitState() { return init; }
	public void setInitState(InitState init) { this.init = (State) init; }
	
	private List<GroundPrecondition> goal;
	public List<GroundPrecondition> getGoal() { return goal; }
	public void setGoal(List<GroundPrecondition> goal)
	{
		this.goal = Collections.unmodifiableList(goal);
	}
}
