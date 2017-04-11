package unit;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import meta.Utilities;

import pddl.Image;
import pddl.ImageType;

public abstract class AbstractUnit extends Image
{
	private List<String> parameterTypes;
	public List<String> getTypeList() { return parameterTypes; }
	
	public AbstractUnit(String image, List<String> parameterTypes)
	{
		super(image, ImageType.unitImage);
		this.parameterTypes =
			Collections.unmodifiableList(new ArrayList<String>(parameterTypes));
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getImage());
		sb.append(" "+Utilities.implode(parameterTypes, " "));
		return sb.toString();
	}
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof AbstractUnit))
			return false;
		
		AbstractUnit other = (AbstractUnit) obj;
		
		// image check
		if (!super.equals(other))
			return false;
		
		return parameterTypes.equals(other.parameterTypes);
	}
}
