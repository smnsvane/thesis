package unit;

import java.util.List;

public class AbstractFunction extends AbstractUnit
{
	public AbstractFunction(String image, List<String> parameterTypes)
	{
		super(image, parameterTypes);
	}
	@Override
	public final boolean equals(Object obj)
	{
		if (!(obj instanceof AbstractFunction))
			return false;
		
		return !super.equals(obj);
	}
}
