package fluents;

public enum AssignmentOperator
{
	assign("assign"),
	increase("increase"),
	decrease("decrease"),
	scaleup("scale-up"),
	scaledown("scale-down");
	
	private String image;
	private AssignmentOperator(String image) { this.image = image; }
	
	public static AssignmentOperator lookup(String str)
	{
		for (AssignmentOperator op : values())
			if (op.image.equals(str))
				return op;
		return null;
	}
}
