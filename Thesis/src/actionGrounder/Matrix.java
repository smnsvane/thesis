package actionGrounder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Matrix<H, E>
{
	private Comparator<H> comp;
	public void setComparator(Comparator<H> comp) { this.comp = comp; }
	
	private ArrayList<Row> rows = new ArrayList<Row>();
	public void appendRow(H header, ArrayList<E> list) { rows.add(new Row(header, list)); }
	public void sortRows() { Collections.sort(rows); }
	
	private int rowIndex = 0;
	public void incrementRowIndex() { rowIndex++; }
	public void decrementRowIndex() { rowIndex--; }
	public int getRowIndex() { return rowIndex; }
	
	public boolean haveCurrentRow() { return haveRowAtIndex(rowIndex); }
	public boolean havePrevRow() { return haveRowAtIndex(rowIndex - 1); }
	public boolean haveNextRow() { return haveRowAtIndex(rowIndex + 1); }
	public boolean haveRowAtIndex(int rowIndex)
	{
		return 0 <= rowIndex && rowIndex < rows.size();
	}
	
	private Row getRow() { return rows.get(rowIndex); }
	
	public void incrementIndex() { getRow().index++; }
	public void decrementIndex() { getRow().index--; }
	public void resetIndex() { getRow().index = 0; }
	
	public boolean haveCurrentElement() { return haveElementAtIndex(getRow().index); }
	public boolean havePrevElement() { return haveElementAtIndex(getRow().index - 1); }
	public boolean haveNextElement() { return haveElementAtIndex(getRow().index + 1); }
	public boolean haveElementAtIndex(int index)
	{
		return 0 <= index && index < getRow().rowElements.size();
	}
	
	public E getElement() { return getRow().rowElements.get(getRow().index); }
	public E getElementAtRow(int rowIndex)
	{
		Row row = rows.get(rowIndex);
		return row.rowElements.get(row.index);
	}
	
	public H getHeader() { return getRow().header; }
	public H getHeaderAtRow(int rowIndex) { return rows.get(rowIndex).header; }
	
	public String currentRowToString()
	{
		return getRow().toString();
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rows.size(); i++)
		{
			if (rowIndex == i)
				sb.append("*");
			sb.append(rows.get(i)+"\n");
		}
		return sb.toString();
	}
	
	class Row implements Comparable<Matrix<H, E>.Row>
	{
		private int index = 0;
		
		private H header;
		private ArrayList<E> rowElements;
		public Row(H header, ArrayList<E> row)
		{
			this.header = header;
			this.rowElements = row;
		}
		@Override
		public int compareTo(Row other)
		{
			int sizeDiff = rowElements.size() - other.rowElements.size();
			if (sizeDiff == 0 && comp != null)
				return comp.compare(header, other.header);
			else
				return sizeDiff;
		}
		
		@Override
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			sb.append("H:"+header+" E:[");
			for (int i = 0; i < rowElements.size(); i++)
			{
				sb.append((i==index?" *":" "));
				sb.append(rowElements.get(i)+",");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("]");
			
			return sb.toString();
		}

	}

}
