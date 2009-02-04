package com.vtls.opensource.caching.memcached;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
import com.vtls.opensource.caching.CachingStrategy;
import com.vtls.opensource.caching.helpers.SetPreservingHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MemcachedCachingStrategy implements CachingStrategy
{
	private static MemcachedCachingStrategy instance = null;
   
   private Log log = LogFactory.getLog(getClass());	
   private MemCachedClient cache = null;
   private SetPreservingHashMap groupings = null;

   private MemcachedCachingStrategy()
   {
      cache = new MemCachedClient();
      groupings = new SetPreservingHashMap();
   }
   
   private MemcachedCachingStrategy(final String[] serverList)
   {
      this();
      this.setServers(serverList);
   }

	public static synchronized MemcachedCachingStrategy getInstance()
	{
      if(instance == null)
         instance = new MemcachedCachingStrategy();

      return instance;
	}

	public static synchronized MemcachedCachingStrategy getInstance(final String[] serverList)
	{
	   getInstance().setServers(serverList);
      return instance;
	}
   
   /* CachingStrategy Interface
   ***************************************************************************/
   
   public boolean containsKey(String key)
   {
      return (this.get(key) != null);
   }
   
   public void delete(String key)
   {
      this.getClient().delete(key);
   }
   
   public Object get(String key)
   {
      return this.getClient().get(key);
   }
   
   public void invalidate()
   {
      this.getClient().flushAll();
   }
   
   public void invalidate(String group)
   {
      Set<String> set = (Set<String>)this.groupings.get(group);
      if(set == null)
         return;
         
      for(String key : set)
         this.delete(key);

      this.groupings.remove(group);
   }
   
   public void put(String key, Object value)
   {
      this.getClient().set(key, value);
   }
   
   public void put(String key, Object value, Date expirationDate)
   {
      this.getClient().set(key, value, expirationDate);
   }

   public void put(String group, String key, Object value)
   {
      groupings.put(group, key);
      this.put(key, value);
   }
   
   public void put(String group, String key, Object value, Date expirationDate)
   {
      groupings.put(group, key);
      this.put(key, value, expirationDate);
   }
   
   /* Accessor/Mutators
   ***************************************************************************/
   public void setServers(String[] serverList)
   {
      SockIOPool pool = SockIOPool.getInstance();
      pool.setServers(serverList);

      if(!pool.isInitialized())
         pool.initialize();      

      this.setClient(new MemCachedClient());
   }
   
   public MemCachedClient getClient()
   {
      return cache;
   }

   public void setClient(MemCachedClient cache)
   {
      this.cache = cache;
   }

   /* Local Methods
   ***************************************************************************/
}
