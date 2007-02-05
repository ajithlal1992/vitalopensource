package com.vtls.opensource.sort;

import com.vtls.opensource.normalization.NormalizationFilter;

public class Sort
{
	private String field = null;
	private boolean direction = true;
	private int type = 0;
	private NormalizationFilter filter = null;
	
   public static final boolean ASCENDING = true;
   public static final boolean DESCENDING = false;
   
   public Sort(String _field, int _type, boolean _direction, NormalizationFilter _filter)
	{
	   this.setField(_field);
	   this.setDirection(_direction);
	   this.setType(_type);
	   this.setFilter(_filter);
	}

   public Sort(String _field, int _type, boolean _direction)
	{
	   this(_field, _type, _direction, null);
	}

   public Sort(String _field, int _type)
	{
	   this(_field, _type, Sort.ASCENDING);
	}

   ///////////////////////////////////////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////////////

	public void setField(String _field)
	{
	   field = _field;
   }
   
	public void setDirection(boolean _direction)
	{
	   direction = _direction;
   }
   
	public void setType(int _type)
	{
	   type = _type;
   }
   
	public void setFilter(NormalizationFilter _filter)
	{
	   filter = _filter;
   }
   
	public NormalizationFilter getFilter()
	{
	   return filter;
   }
	
	public String getField()
	{
	   return field;
   }
	
	public boolean getDirection()
	{
	   return direction;
   }
	
	public int getType()
	{
	   return type;
   }
   
   ///////////////////////////////////////////////////////////////////////////
   ///////////////////////////////////////////////////////////////////////////
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		if(field != null)
		{
   		buffer.append("Field:'").append((field != null) ? field : "");
   		buffer.append("'; ");
		}

	   buffer.append("Type:");
		if(type == SortComparator.STRING)
		   buffer.append("string");
		else if(type == SortComparator.NUMERIC)
		   buffer.append("numeric");
		else if(type == SortComparator.DATE)
		   buffer.append("date");

		buffer.append("; ");

		buffer.append("Sort:");
		if(direction == Sort.ASCENDING)
		   buffer.append("ascending");
		else
		   buffer.append("descending");
		
		buffer.append("; ");
		buffer.append(filter);
		return buffer.toString();
	}
}