package actionGrounder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import object.PDDLObject;

import pddl.Data;

public class ParameterGrounder implements Iterable<HashMap<PDDLObject, PDDLObject>>
{
	private ArrayList<AtomPointer> pointers = new ArrayList<AtomPointer>();
	public void addPointer(AtomPointer pointer) { pointers.add(pointer); }
	
	private List<PDDLObject> parameters;
	public ParameterGrounder(Data data, List<PDDLObject> parameters)
	{
		this.parameters = parameters;
		for (PDDLObject param : parameters)
		{
			String paramType = param.getType();
			List<PDDLObject> matchingAtoms;
			
			if (paramType == null)
				matchingAtoms = data.getConstants();
			else
				matchingAtoms = data.getConstantsByType(paramType);
			
			addPointer(new AtomPointer(matchingAtoms));
		}
	}
	
	@Override
	public Iterator<HashMap<PDDLObject, PDDLObject>> iterator()
	{
		return new Iterator<HashMap<PDDLObject, PDDLObject>>()
		{
			private boolean endReached = false;
			@Override
			public boolean hasNext()
			{
				return !endReached;
			}
			@Override
			public HashMap<PDDLObject, PDDLObject> next()
			{
				boolean pointerMoved = false;
				endReached = true;
				ArrayList<PDDLObject> arguments = new ArrayList<PDDLObject>();
				for (int i = 0; i < pointers.size(); i++)
				{
					AtomPointer ap = pointers.get(i);
					arguments.add(ap.getCurrent());
					// increment
					if (!pointerMoved)
						if (ap.hasNext())
						{
							ap.incrementPointer();
							pointerMoved = true;
							endReached = false;
						}
						else
							ap.reset();
				}
				
				// create assignment map
				HashMap<PDDLObject, PDDLObject> assignment =
					new HashMap<PDDLObject, PDDLObject>(arguments.size());
				
				int index = 0;
				for (PDDLObject param : parameters)
					assignment.put(param, arguments.get(index++));
				
				return assignment;
			}
			@Override
			public void remove() { throw new UnsupportedOperationException(); }
		};
	}
	
	class AtomPointer
	{
		private List<PDDLObject> list;
		public List<PDDLObject> getList() { return list; }
		private int pointer = 0;
		public AtomPointer(List<PDDLObject> list) { this.list = list; }
		
		public boolean hasNext() { return pointer+1 < list.size(); }
		public PDDLObject getCurrent() { return list.get(pointer); }
		public void incrementPointer() { pointer++; }
		public void reset() { pointer = 0; }
	}
}
