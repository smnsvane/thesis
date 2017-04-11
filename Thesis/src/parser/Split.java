package parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import meta.Utilities;

public class Split extends Node
{
	public Split(Split parent, String content)
	{
		super(parent);
		parseChildren(content);
	}
	
	private List<Leaf> leaves = new ArrayList<Leaf>();
	public List<Leaf> getLeaves() { return leaves; }
	
	private List<Split> splits = new ArrayList<Split>();
	public List<Split> getSplits() { return splits; }
	
	private List<Node> children = new ArrayList<Node>();
	public List<Node> getChildren() { return children; }
	
	@Override
	public boolean isTreeLeaf() { return children.isEmpty(); }
	@Override
	public String header()
	{
		if (!leaves.isEmpty())
			return leaves.get(0).header();
		throw new RuntimeException("internal error in Split class");
	}
	
	private void addLeafChild(String content)
	{
		Leaf l = new Leaf(this, content);
		leaves.add(l);
		children.add(l);
	}
	private void addSplitChild(String content)
	{
		Split s = new Split(this, content);
		splits.add(s);
		children.add(s);
	}
	
	private void parseChildren(String content)
	{
		int offset = 0;
		int parDepth = 0;
		int charIndex;
		for (charIndex = 0; charIndex < content.length(); charIndex++)
		{
			char ch = content.charAt(charIndex);
			// char is a space?
			if (ch == 0x20 && parDepth == 0)
			{
				addLeafChild(content.substring(offset, charIndex));
				offset = charIndex + 1;
			}
			// char is a '('?
			if (ch == 0x28)
			{
				if (parDepth == 0)
				{
					if  (offset < charIndex)
						addLeafChild(content.substring(offset, charIndex));
					offset = charIndex + 1;					
				}
				parDepth++;
			}
			// char is a ')'?
			if (ch == 0x29)
			{
				if (parDepth == 1)
				{
					addSplitChild(content.substring(offset, charIndex));
					offset = charIndex + 1;
				}
				parDepth--;
				if (parDepth < 0)
					Utilities.parserError("More ')' than '(' in this text '"+content+"'");
			}
		}
		// fetching tail leaf
		if (offset < content.length())
			addLeafChild(content.substring(offset, content.length()));
		if (parDepth != 0)
			if (parDepth > 0)
				Utilities.parserError(
						parDepth+" More '(' than ')' in this text '"+content+"'");
			else
				Utilities.parserError(
						-parDepth+" More ')' than '(' in this text '"+content+"'");
		
		leaves = Collections.unmodifiableList(leaves);
		splits = Collections.unmodifiableList(splits);
		children = Collections.unmodifiableList(children);
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append('(');
		
		if (!children.isEmpty())
			sb.append(children.get(0));
		
		for (int i = 1; i < children.size(); i++)
			sb.append(" "+children.get(i));
		
		sb.append(')');
		
		return sb.toString();
	}
	
	class SplitIterator implements Iterator<Split>
	{
		public SplitIterator() { findNext(); }
		
		private Split next;
		private int index = 0;
		private void findNext()
		{
			next = null;
			for (int i = index; i < children.size(); i++)
				if (children.get(i) instanceof Split)
				{
					next = (Split) children.get(i);
					index = i + 1;
				}
		}
		
		@Override
		public Split next() { return next; }
		@Override
		public boolean hasNext() { return next != null; }
		
		@Override
		public void remove() { throw new UnsupportedOperationException(); }
	}
}
