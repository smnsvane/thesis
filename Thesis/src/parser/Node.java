package parser;

public abstract class Node
{
	private Split parent;
	/**
	 * Get this node's parent
	 * @return a split node or null if this is the root
	 */
	public Split getParent() { return parent; }
	
	public Node(Split parent) { this.parent = parent; }
	
	/**
	 * @return true if this node have children, false otherwise
	 */
	public abstract boolean isTreeLeaf();
	
	/**
	 * Get the first child element in string form.
	 * Note: the first element is always a leaf
	 * @return first child as a string
	 */
	public abstract String header();
}
