package com.vtls.opensource.caching.memory;

import com.vtls.opensource.caching.CachingStrategy;
import com.vtls.opensource.caching.helpers.SetPreservingHashMap;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class LRUCachingStrategy implements CachingStrategy
{
	private static LRUCachingStrategy instance = null;

   private Map<String, Object> map = null;
   private SetPreservingHashMap groupings = null;
   
   private LRUCachingStrategy()
   {
      this(512);
   }
   
   private LRUCachingStrategy(final int initialSize)
   {
      groupings = new SetPreservingHashMap();

      // This uses a 'special' constructor that will iterate based on access.
      // Perfect for a LRU cache. (Here, we're overriding 'removeEldestEntry'.)
      map = new LinkedHashMap<String, Object>(initialSize, 0.75f, true)
      {
         protected boolean removeEldestEntry(Map.Entry eldest)
         {
            return (map.size() > initialSize);
         }
      };
   }

	public static synchronized LRUCachingStrategy getInstance()
	{
      if(instance == null)
         instance = new LRUCachingStrategy();

      return instance;
	}

	public static synchronized LRUCachingStrategy getInstance(final int initialSize)
	{
      if(instance == null)
         instance = new LRUCachingStrategy(initialSize);

      return instance;
	}

   public boolean containsKey(String key)
   {
      return map.containsKey(key);
   }
   
   public void delete(String key)
   {
      map.remove(key);
   }
   
   public Object get(String key)
   {
      return map.get(key);
   }
   
   public synchronized void invalidate()
   {
      map.clear();
   }
   
   public synchronized void invalidate(String group)
   {
      Set<String> set = (Set<String>)this.groupings.get(group);
      if(set == null)
         return;
         
      for(String key : set)
         this.delete(key);

      this.groupings.remove(group);
   }
   
   public synchronized void put(String key, Object value)
   {
      map.put(key, value);
   }
   
   public synchronized void put(String group, String key, Object value)
   {
      groupings.put(group, key);
      map.put(key, value);
   }
}
