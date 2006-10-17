package com.vtls.opensource.utilities;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

/**
 * An object that maps keys to a List of values. Every addition to this
 * {@link java.util.HashMap} will preserve any values associated with a given key as elements
 * within a {@link java.util.List}.
 * <br>
 * The added getList method returns a unique,
 * sorted List of pair objects of the form (({@link java.lang.Object}) Key, ({@link java.util.List}) Value).
 * @author <a href="mailto:liversedgej@vtls.com">Joe Liversedge</a>
 */

public final class PreservingHashMap extends LinkedHashMap
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(PreservingHashMap.class);

   /**
    * Class constructor
    */
   public PreservingHashMap()
   {
      super();
   }

   /**
    * Returns the number of objects that have been {@link #put(Object, Object)}
    * into the map for this key.
    * @param   _key  key whose associated values are to be counted.
    * @return  the number of Objects associated with the _key in the map.
    */
   public int getCount(Object _key)
   {
      // Get the current List for this key.
      Object value = super.get(_key);
      
      // If we have something to work with, return the List's size.
      if(value != null)
      {
    	  return ((List)value).size();
      }
      // Otherwise, return zero.
      else
      {
    	  return 0;
      }
   }

   /**
    * Associates the specified value with the specified key in this map.
    * Again, the values will be preserved in a List kept along with the key.
    * @param _key key with which the specified value is to be associated.
    * @param _value  value to be associated with the specified key.
    */
   public Object put(Object _key, Object _value)
   {
      // Don't want to deal with 'null's.
      if(_key == null)
      {
    	  return null;
      }
      
      // If it's a new Object key, then we want to create a new List
      // associated with the key with our '_value' as its first element.
      if(!super.containsKey(_key))
      {
         List list = new LinkedList();
         list.add(_value);
         return super.put(_key, list);
      }
      // Otherwise, we'll want to fetch the existing value, which is an
      // instance of 'List' and add the value to it.
      else
      {
         List list = (List)(super.get(_key));
      
         if(list != null)
         {
        	 list.add(_value);
         }

         return list;
      }
   }

   /**
    * Copies all of the mappings from the specified map to this map. <i>Unused.</i>
    * @todo This method may need to be supported or handled better.
    */
   public void putAll(Map t)
   {
      m_logger.error("The 'putAll' method is not supported in " + PreservingHashMap.class.getName() + ".");
   }
   
   /**
    * Returns a List of unique keys paired with a List of associated values for the key.
    * @return a {@link Map} 
    */
   public Map getMap()
   {
      Map _return = new LinkedHashMap();
      
      // Loop through our HashMap and append a collection of 'Pairs' to an ArrayList.
   	  for(Iterator j = keySet().iterator(); j.hasNext();)
   	  {
   	     Object key = j.next();
   	     List value = (List)super.get(key);
	   
   	     // Group the two and add to the List.
   	     _return.put(key, value);
   	  }
   	
  	   return _return;
	}
}