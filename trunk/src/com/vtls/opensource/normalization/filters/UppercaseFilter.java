package com.vtls.opensource.normalization.filters;

import org.apache.commons.lang.StringUtils;
import com.vtls.opensource.normalization.NormalizationFilter;

public class UppercaseFilter extends NormalizationFilter
{
   public UppercaseFilter(NormalizationFilter chainedFilter)
   {
      super(chainedFilter);
   }
   
   public UppercaseFilter()
   {
      super(null);
   }
   
   protected String getNormalizedString(final String string)
   {
      if(string == null)
         throw new IllegalArgumentException("UppercaseFilter expects a non-null string.");

      return StringUtils.capitalize(string);
   }
}
