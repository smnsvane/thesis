package unit;

import meta.Utilities;
import object.ObjectList;
import pddl.Image;
import pddl.ImageType;

public abstract class GroundUnit extends Image implements Comparable<GroundUnit>
{
	private ObjectList arguments;
	public ObjectList getArguments() { return arguments; }
	
	private int hash;
	@Override
	public int hashCode() { return hash; }
	
	public GroundUnit(String image, ObjectList arguments)
	{
		super(image, ImageType.unitImage);
		
		this.arguments = arguments;
		
		// compute hash-code
		hash = super.hashCode();
		for (int i = 0; i < arguments.length(); i++)
			hash += (arguments.get(i).hashCode() << i);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof GroundUnit))
			return false;
		
		GroundUnit other = (GroundUnit) obj;
		
		if (!getImage().equals(other.getImage()))
			return false;
		
		return arguments.equals(other.arguments);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getImage()+" ");
		sb.append(Utilities.implode(arguments.getObjectList(), " "));
		return sb.toString();
	}
	@Override
	public int compareTo(GroundUnit other)
	{
		int imgComp = getImage().compareTo(other.getImage());
		if (imgComp != 0)
			return imgComp;
		for (int i = 0; i < arguments.length(); i++)
		{
			int argComp =
				arguments.get(i).compareTo(other.arguments.get(i));
			if (argComp != 0)
				return argComp;
		}
		return 0;
	}
}
