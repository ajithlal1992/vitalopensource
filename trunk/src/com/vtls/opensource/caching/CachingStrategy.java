package com.vtls.opensource.caching;

import java.util.Date;

public interface CachingStrategy
{
   public boolean containsKey(String key);
   public void delete(String key);
   public Object get(String key);
   public void invalidate();
   public void invalidate(String group);
   public void put(String key, Object value);
   public void put(String group, String key, Object value);
}
