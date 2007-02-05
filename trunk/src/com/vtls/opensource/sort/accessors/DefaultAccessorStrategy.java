package com.vtls.opensource.filters.comparison.accessors;

import java.util.Map;

public class DefaultAccessorStrategy implements AccessorStrategy
{
   public Object getValue(final Object object)
   {
      return object.toString();
   }
   
   public Object getFieldValue(final Object object, String field)
   {
      if(object instanceof Map && field != null)
      {
         Map map = (Map)object;
         return map.get(field);
      }

      return this.getValue(object);
   }

   public boolean hasSupportForObject(Object _object)
   {
      return true;
   }
}
