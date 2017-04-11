package fluents;

public enum MathOperatorType
{
	add("+"),
	minus("-"), // both used as unary and binary operator
	multiply("*"),
	divide("/");
	
	private String image;
	public String getImage() { return image; }
	private MathOperatorType(String image) { this.image = image; }
	
	public static MathOperatorType lookup(String str)
	{
		for (MathOperatorType op : values())
			if (op.image.equals(str))
				return op;
		return null;
	}
}
