package action;

import fluents.FluentPrecondition;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import meta.Utilities;

import object.Assignment;
import object.ObjectList;

import pddl.Image;
import pddl.ImageType;
import unit.Predicate;

public class Action extends Image
{
	private ObjectList parameters;
	public ObjectList getParameters() { return parameters; }
	
	private List<Predicate> pPrecond;
	public List<Predicate> getPPrecond() { return pPrecond; }
	
	private List<FluentPrecondition> fPrecond;
	public List<FluentPrecondition> getFPrecond() { return fPrecond; }
	
	private List<Effect> effect;
	public List<Effect> getEffect() { return effect; }
	
	public Action(String image, ObjectList parameters,
			List<Precondition> precond, List<Effect> effect)
	{
		super(image, ImageType.actionImage);
		this.parameters = parameters;
		this.effect = Collections.unmodifiableList(effect);
		
		pPrecond = new ArrayList<Predicate>();
		fPrecond = new ArrayList<FluentPrecondition>();
		
		for (Precondition p : precond)
		{
			if (p instanceof Predicate)
				pPrecond.add((Predicate) p);
			else if (p instanceof FluentPrecondition)
				fPrecond.add((FluentPrecondition) p);
			else
				throw new RuntimeException();
		}
		
		pPrecond = Collections.unmodifiableList(pPrecond);
		fPrecond = Collections.unmodifiableList(fPrecond);
	}
	
	public GroundAction ground(Assignment assignment)
	{
		// create grounded preconditions
		List<GroundPrecondition> gPrecond = new ArrayList<GroundPrecondition>();		
		
		for (Predicate p : pPrecond)
			gPrecond.add(p.ground(assignment));
		for (FluentPrecondition fp : fPrecond)
			gPrecond.add(fp.ground(assignment));
		
		// create grounded effects
		List<GroundEffect> gEffect = new ArrayList<GroundEffect>();
		
		for (Effect e : effect)
			gEffect.add(e.ground(assignment));
		
		return new GroundAction(getImage(), assignment.getArguments(), gPrecond, gEffect);
	}
	
	@Override
	public String toString()
	{
		return getImage()+" "+Utilities.implode(parameters.getObjectList(), " ");
	}
}
