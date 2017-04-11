package unit;

import java.util.List;

public class AbstractPredicate extends AbstractUnit
{
	public AbstractPredicate(String image, List<String> parameterTypes)
	{
		super(image, parameterTypes);
	}
	@Override
	public final boolean equals(Object obj)
	{
		if (!(obj instanceof AbstractPredicate))
			return false;
		
		return !super.equals(obj);
	}
}
