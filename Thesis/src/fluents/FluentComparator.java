package fluents;

public enum FluentComparator
{
	less("<"),
	lessEqual("<="),
	equal("="),
	notEqual("<>"),
	greater(">"),
	greaterEqual(">=");
	
	private String image;
	private FluentComparator(String image)
	{
		this.image = image;
	}
	@Override
	public String toString() { return image; }
	
	public static FluentComparator lookup(String image)
	{
		for (FluentComparator comp : values())
			if (comp.image.equals(image))
				return comp;
		return null;
	}
}
