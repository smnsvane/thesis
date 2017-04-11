package object;

import java.util.ArrayList;
import java.util.List;

public class Assignment implements Cloneable
{
	private ObjectList parameters, arguments;
	public ObjectList getParameters() { return parameters; }
	public ObjectList getArguments() { return arguments; }
	
	public Assignment(ObjectList parameters)
	{
		this(parameters, new ObjectList());
		
		// fill argument list so that it have the correct length
		for (int i = 0; i < parameters.length(); i++)
			arguments.getObjectList().add(null);
	}
	
	public Assignment(ObjectList parameters, ObjectList arguments)
	{
		this.parameters = parameters;
		this.arguments = arguments;
	}
	
	private int indexOf(PDDLObject parameter)
	{
		int index = parameters.getObjectList().indexOf(parameter);
		if (index == -1)
			throw new RuntimeException("The parameter '"+parameter+
					"' was not found in the assignment");
		return index;
	}
	
	public PDDLObject getArg(PDDLObject parameter)
	{
		int index = indexOf(parameter);
		PDDLObject arg = arguments.get(index);
		if (arg == null)
			throw new RuntimeException("The parameter '"+parameter+"' is not ground");
		return arg;
	}
	
	/////////////////////////////////////
	// methods for setting the arguments
	/////////////////////////////////////
	
	public boolean isGround(PDDLObject parameter)
	{
		int index = indexOf(parameter);
		return arguments.get(index) != null;
	}
	public void clearArguments()
	{
		for (int i = 0; i < parameters.length(); i++)
			arguments.getObjectList().set(i, null);
	}
	public void groundParameter(PDDLObject parameter, PDDLObject argument)
	{
		int index = indexOf(parameter);
		PDDLObject arg = arguments.get(index);
		if (arg != null)
			throw new RuntimeException("The parameter '"+parameter+
					"' is already ground with the object '"+arg+"'");
		arguments.getObjectList().set(index, argument);
	}
	public PDDLObject ungroundParameter(PDDLObject parameter)
	{
		int index = indexOf(parameter);
		PDDLObject arg = arguments.get(index);
		// test if parameter is ground if it is not an exception is thrown
		if (arg == null)
			throw new RuntimeException("The parameter '"+parameter+"' is not ground");
		arguments.getObjectList().set(index, null);
		return arg;
	}
	public List<PDDLObject> getUngroundParameters()
	{
		List<PDDLObject> ungroundPars = new ArrayList<PDDLObject>();
		for (int i = 0; i < parameters.length(); i++)
			if (arguments.get(i) == null)
				ungroundPars.add(parameters.get(i));
		return ungroundPars;
	}
	
	@Override
	public Assignment clone()
	{
		return new Assignment(parameters.clone(), arguments.clone());
	}
}
