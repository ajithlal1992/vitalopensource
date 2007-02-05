package com.vtls.opensource.normalization;

public abstract class NormalizationFilter
{
   NormalizationFilter chainedFilter = null;

   public NormalizationFilter()
   {
   }

   public NormalizationFilter(NormalizationFilter chainedFilter)
   {
      this.chainedFilter = chainedFilter;
   }
   
   public String getString(final String name)
   {
      if(chainedFilter != null)
         return this.getNormalizedString(chainedFilter.getString(name));

      return this.getNormalizedString(name);
   }

   protected abstract String getNormalizedString(final String string);
   
   protected boolean hasChainedFilter()
   {
      return (chainedFilter != null);
   }
}