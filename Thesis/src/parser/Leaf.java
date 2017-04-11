package parser;

public class Leaf extends Node
{
	private String content;
	@Override
	public String header() { return content; }
	
	public Leaf(Split parent, String content)
	{
		super(parent);
		this.content = content;
	}
	
	@Override
	public boolean isTreeLeaf() { return true; }
	
	@Override
	public String toString() { return content; }

}
