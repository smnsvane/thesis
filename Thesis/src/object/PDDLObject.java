/**
 * this class represents strips objects and
 * the action arguments that the planner applies when it instantiates an action
 */
package object;

import pddl.Image;
import pddl.ImageType;

public class PDDLObject extends Image implements Cloneable, Comparable<PDDLObject>
{
	private String type;
	public String getType() { return type; }
	
	public PDDLObject(String image, String type)
	{
		super(image, (image.charAt(0)=='?'?ImageType.parameterImage:ImageType.objectImage));
		this.type = type;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof PDDLObject))
			return false;
		
		PDDLObject other = (PDDLObject) obj;
		
		if (!getImage().equals(other.getImage()))
			return false;
		
		boolean equalTypes =
			(type == null ? other.type == null : type.equals(other.type));
		if (!equalTypes)
			throw new RuntimeException("Two same image atoms have different types");
		
		return true;
	}
	public boolean subTypeOf(String masterType)
	{
		return masterType == null || type.equals(masterType);
	}
	
	public boolean isArgument() { return getImage().charAt(0) != '?'; }
	
	@Override
	public String toString()
	{
		return getImage();//+(getType()==null?"[untyped]":" - "+getType());
	}
	
	@Override
	public int compareTo(PDDLObject other)
	{
		return getImage().compareTo(other.getImage());
	}
	
	// UGLY HACK SUGGESTED BY NICKLAS
	private PDDLObject(String image, String type, Object x)
	{
		super(image, null);
		this.type = type;
	}
	public static PDDLObject createDummyParameter(String type)
	{
		return new PDDLObject("?[unspecified name]", type, null);
	}
}
