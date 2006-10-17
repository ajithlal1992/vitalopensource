package com.vtls.opensource.comparator;

public class ComparatorField
{
	private String m_field = null;
	private boolean m_direction = true;
	private int m_type = 0;

   public ComparatorField(String _field, boolean _direction, int _type)
	{
	   m_field = _field;
	   m_direction = _direction;
	   m_type = _type;
	}
	
	public String getField()
	{
	   return m_field;
   }
	
	public boolean getDirection()
	{
	   return m_direction;
   }
	
	public int getType()
	{
	   return m_type;
   }
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("(");
		buffer.append((m_field != null) ? m_field : "");
		buffer.append(", ");
		buffer.append(m_direction);
		buffer.append(", ");
		buffer.append(m_type);
		buffer.append(")");
		return buffer.toString();
	}
}