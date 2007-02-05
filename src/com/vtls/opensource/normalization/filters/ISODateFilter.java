package com.vtls.opensource.normalization.filters;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.StringEscapeUtils;
import com.vtls.opensource.normalization.NormalizationFilter;

public class ISODateFilter extends NormalizationFilter
{
   // YYYYMMDDhhmmss. (14)
   private static final int ISO_DATE_WIDTH = 14;
   
   public ISODateFilter(NormalizationFilter chainedFilter)
   {
      super(chainedFilter);
   }
   
   public ISODateFilter()
   {
      super(null);
   }
   
   protected String getNormalizedString(final String string)
   {
      if(string == null)
         throw new IllegalArgumentException("ISODateFilter expects a non-null name.");

      // Convert '&entityreferences;' to their glyphic equivalent. (Possibly numeric.)
      String value = StringEscapeUtils.unescapeXml(string);
      
      value = StringUtils.rightPad(value.replaceAll("[\\p{Alpha}\\p{Space}\\p{Punct}]", ""), ISO_DATE_WIDTH, '0');

      if(value.length() > ISO_DATE_WIDTH)
         value = value.substring(0, ISO_DATE_WIDTH);

      return StringUtils.uncapitalize(value);
   }
}

