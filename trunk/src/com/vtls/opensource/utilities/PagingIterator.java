package com.vtls.opensource.utilities;

import java.util.Iterator;

/**
 * This class is an extended Iterator used for iterating
 * paged elements
 */
public class PagingIterator implements Iterator
{
   private Iterator m_iterator = null;
   private int m_start;
   private int m_end;

   private int m_position = 0;

   /**
    * Class constructor.
    * @param _iterator  A specific source {@link Iterator} to create 
    *                   the paging iterator
    * @param _start  The start position for paging
    * @param _number  The number of elements to iterate for paging
    * @param _total The total number of elements in the specific Iterator
    */
   public PagingIterator(Iterator _iterator, int _start, int _number, int _total)
   {
      super();
      m_iterator = _iterator;
      m_start = _start;

      if(m_start < 0)
      {
    	  m_start = 0;
      }
      
      m_end = Math.min(m_start + _number, _total);
   
      if(m_end < m_start)
      {
         throw new IllegalArgumentException("Total must be greater than start.");
      }

      m_position = 0;
      while(m_position < m_start && m_iterator.hasNext())
      {
         m_iterator.next();
         m_position ++;
      }
   }

   /**
    * Returns true if the paging iteration has more elements.
    * @return True if the paging iterator has more elements
    */
   public boolean hasNext()
   {
      return (m_position < m_end);
   }

   /**
    * Returns the next element in the iteration.
    * @return the next element
    */
   public Object next()
   {
      m_position++;
      return m_iterator.next();
   }
   
   /**
    * Throws an UnsupportedOperationException with message
    */
   public void remove()
   {
      throw new UnsupportedOperationException("remove() is not supported in this version.");
   }
}