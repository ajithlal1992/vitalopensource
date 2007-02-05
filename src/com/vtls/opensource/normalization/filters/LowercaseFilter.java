package com.vtls.opensource.normalization.filters;

import org.apache.commons.lang.StringUtils;
import com.vtls.opensource.normalization.NormalizationFilter;

public class LowercaseFilter extends NormalizationFilter
{
   public LowercaseFilter(NormalizationFilter chainedFilter)
   {
      super(chainedFilter);
   }
   
   public LowercaseFilter()
   {
      super(null);
   }
   
   protected String getNormalizedString(final String string)
   {
      if(string == null)
         throw new IllegalArgumentException("LowercaseFilter expects a non-null name.");

      return StringUtils.uncapitalize(string);
   }
}