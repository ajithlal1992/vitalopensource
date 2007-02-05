package com.vtls.opensource.normalization.filters;

import org.apache.commons.lang.StringUtils;
import com.vtls.opensource.normalization.NormalizationFilter;

public class LeadingZeroFilter extends NormalizationFilter
{
   public LeadingZeroFilter(final NormalizationFilter chainedFilter)
   {
      super(chainedFilter);
   }

   public LeadingZeroFilter()
   {
      super(null);
   }
      
   protected String getNormalizedString(final String string)
   {
      if(string == null)
         throw new IllegalArgumentException("LeadingZeroFilter expects a non-null string.");

      return string.replaceAll("^0+", "");
   }
}
