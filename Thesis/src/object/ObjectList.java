package object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ObjectList implements Iterable<PDDLObject>, Cloneable
{
	private List<PDDLObject> list;
	public List<PDDLObject> getObjectList() { return list; }
	
	public PDDLObject get(int index) { return list.get(index); }
	public int length() { return list.size(); }
	@Override
	public Iterator<PDDLObject> iterator() { return list.iterator(); }
	
	private boolean immutable = false;
	public void makeImmutable()
	{
		if (immutable)
			return;
		list = Collections.unmodifiableList(list);
		immutable = true;
	}
	
	public ObjectList(Collection<PDDLObject> list, boolean immutable)
	{
		this.list = new ArrayList<PDDLObject>(list);
		if (immutable)
			makeImmutable();
	}
	public ObjectList() { this(new ArrayList<PDDLObject>(), false); }
	
	public Assignment ground(ObjectList arguments)
	{
		return new Assignment(this, arguments);
	}
	
	public Assignment createAssignment() { return new Assignment(this); }
	
	@Override
	public String toString() { return list.toString(); }
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (!(obj instanceof ObjectList))
			return false;
		
		ObjectList other = (ObjectList) obj;
		return list.equals(other.list);
	}
	
	@Override
	public ObjectList clone()
	{
		return new ObjectList(list, immutable);
	}
}
