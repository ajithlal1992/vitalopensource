package com.vtls.opensource.normalization.filters;

import org.apache.commons.lang.StringUtils;
import com.vtls.opensource.normalization.NormalizationFilter;

public class StopwordFilter extends NormalizationFilter
{
   private static String[] DEFAULT_WORDLIST = { "a", "an", "the" };
   private String[] stopwords = null;
   private StringBuffer stopwordExpression = null;

   public StopwordFilter(final NormalizationFilter chainedFilter, final String[] stopwords)
   {
      super(chainedFilter);
      this.setStopwords(stopwords);
   }
   
   public StopwordFilter(NormalizationFilter chainedFilter)
   {
      this(chainedFilter, DEFAULT_WORDLIST);
   }
   
   public StopwordFilter(final String[] stopwords)
   {
      this(null, stopwords);
   }

   public StopwordFilter()
   {
      this(null, DEFAULT_WORDLIST);
   }
   
   public void setStopwords(final String[] stopwords)
   {
      this.stopwords = stopwords;

      // Create a regular expression of the form '^(a|an|the) ' to make the
      // replacement. There's likely a faster way to do this.
      stopwordExpression = new StringBuffer();
      stopwordExpression.append("^(").append(StringUtils.join(stopwords, "|")).append(") ");
   }

   public String[] getStopwords()
   {
      return stopwords;
   }
   
   protected String getNormalizedString(final String string)
   {
      if(string == null)
         throw new IllegalArgumentException("StopwordFilter expects a non-null string.");

      return string.replaceAll("^\\p{Punct}", "").toLowerCase().replaceAll(stopwordExpression.toString(), "");
   }
}
