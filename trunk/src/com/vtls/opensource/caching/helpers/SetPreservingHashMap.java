package com.vtls.opensource.caching.helpers;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class SetPreservingHashMap extends HashMap
{
   private Log log = LogFactory.getLog(getClass());	

   public SetPreservingHashMap()
   {
      super();
   }

   public int getCount(Object key)
   {
      // Get the current Collection for this key.
      Collection collection = (Collection)super.get(key);

      // If we have something to work with, return the size.
      return (collection != null) ? collection.size() : 0;
   }
   
   public Object put(Object key, Object value)
   {
      if(key == null)
         throw new IllegalArgumentException("SetPreservingHashMap expects a non-null key in 'put()'.");
      
      // If it's a new Object key, then we want to create a new Collection
      // associated with the key with our 'value' as its first element.
      if(!super.containsKey(key))
      {
         // TODO: Allow this to be defined by the user?
         Set collection = new LinkedHashSet();
         collection.add(value);
         return super.put(key, collection);
      }
      // Otherwise, we'll want to fetch the existing value, which is an
      // instance of 'Collection' and add the value to it.
      else
      {
         Set collection = (Set)(super.get(key));
         if(collection != null && !collection.contains(value))
            collection.add(value);

         return collection;
      }
   }

   public void putAll(Map t)
   {
      throw new UnsupportedOperationException("The 'putAll' method is not supported in " + SetPreservingHashMap.class.getName() + ".");
   }
}