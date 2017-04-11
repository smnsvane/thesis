package state;

import action.GroundAction;

public class GraphNode
{
	private GraphNode parent;
	public GraphNode getParent() { return parent; }
	
	private State self;
	public State getState() { return self; }
	
	private GroundAction transition;
	public GroundAction getTransition() { return transition; }
	
	public GraphNode(GraphNode parent, GroundAction transition, State self)
	{
		this.parent = parent;
		this.transition = transition;
		this.self = self;
	}
}