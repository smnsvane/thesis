package unit;


import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import meta.Utilities;

import object.Assignment;
import object.ObjectList;
import object.PDDLObject;
import pddl.Image;
import pddl.ImageType;

public abstract class Unit extends Image implements Iterable<PDDLObject>
{
	private Map<Integer, PDDLObject> fixedArguments;
	/** return null if parameter at index is not fixed, otherwise its argument */
	public PDDLObject getFixedArgument(int index) { return fixedArguments.get(index); }
	public boolean haveFixedArguments() { return !fixedArguments.isEmpty(); }
	
	private ObjectList parameters;
	public ObjectList getParameters() { return parameters; }
	
	@Override
	public Iterator<PDDLObject> iterator() { return parameters.iterator(); }
	
	public Unit(String image, ObjectList parameters,
			HashMap<Integer, PDDLObject> fixedArguments)
	{
		super(image, ImageType.unitImage);
		this.fixedArguments = Collections.unmodifiableMap(fixedArguments);
		this.parameters = parameters;

		for (int i = 0; i < parameters.length(); i++)
			if (fixedArguments.containsKey(image))
			{
				String parType = parameters.get(i).getType();
				PDDLObject fixedArg = fixedArguments.get(i);
				if (!fixedArg.subTypeOf(parType))
					throw new RuntimeException("Mismatch in parameter types");
			}
	}
	
	public ObjectList groundParameters(Assignment assignment)
	{
		ObjectList arguments = new ObjectList();
		for (int i = 0; i < getParameters().length(); i++)
		{
			PDDLObject argument = getFixedArgument(i);
			if (argument == null)
			{
				PDDLObject param = getParameters().get(i);
				argument = assignment.getArg(param);
				
				if (!argument.subTypeOf(param.getType()))
					throw new RuntimeException("Argument '"+argument+
							"' is not a sub-type of parameter '"+
							getParameters().get(i)+"'");
			}
			arguments.getObjectList().add(argument);
		}
		arguments.makeImmutable();
		return arguments;
	}
	
	public abstract GroundUnit ground(Assignment assignment);
	
	@Override
	public String toString()
	{
		return getImage()+" "+
				Utilities.implode(parameters.getObjectList(), " ");
	}
}
