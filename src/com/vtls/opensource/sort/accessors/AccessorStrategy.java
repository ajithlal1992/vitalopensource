package com.vtls.opensource.filters.comparison.accessors;

public interface AccessorStrategy
{
   public Object getValue(final Object object);
   public Object getFieldValue(final Object object, final String field);
   
   public boolean hasSupportForObject(Object object);
}