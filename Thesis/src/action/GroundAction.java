package action;

import java.util.List;

import meta.Utilities;

import object.ObjectList;
import pddl.Image;
import pddl.ImageType;

import state.State;

public class GroundAction extends Image
{
	private ObjectList arguments;
	private List<GroundPrecondition> gPrecond;
	private List<GroundEffect> gEffect;
	
	public ObjectList getArguments() { return arguments; }
	public List<GroundPrecondition> getGroundPreconditions() { return gPrecond; }
	public List<GroundEffect> getGroundEffects() { return gEffect; }

	public GroundAction(String image, ObjectList arguments,
			List<GroundPrecondition> gPrecond,
			List<GroundEffect> gEffect)
	{
		super(image, ImageType.actionImage);
		this.arguments = arguments;
		this.gPrecond = gPrecond;
		this.gEffect = gEffect;
	}
	public boolean isApplicable(State state)
	{
		for (GroundPrecondition gp : gPrecond)
			if (!gp.isTrue(state))
				return false;
		return true;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof GroundAction))
			return false;
		
		GroundAction other = (GroundAction) obj;
		
		return super.equals(other) &&
			arguments.equals(other.arguments);
	}
	
	@Override
	public String toString()
	{
		return getImage()+" "+Utilities.implode(arguments.getObjectList(), " ");
	}
}
