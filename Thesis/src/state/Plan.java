package state;

import java.util.LinkedList;

import action.GroundAction;




public class Plan
{
	private LinkedList<String> planList = new LinkedList<String>();
	
	public void addToTail(GroundAction action) { planList.addLast(action.toString()); }
	public void addToHead(GroundAction action) { planList.addFirst(action.toString()); }
	public void addToTail(String action) { planList.addLast(action); }
	public void addToHead(String action) { planList.addFirst(action); }
	public int length() { return planList.size(); }
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder("Solution (length: "+length()+"):\n");
		for (String s : planList)
			sb.append(s+"\n");
		return sb.toString();
	}
}
