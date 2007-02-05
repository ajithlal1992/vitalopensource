package com.vtls.opensource.normalization.filters;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.Map;
import com.vtls.opensource.normalization.NormalizationFilter;

public class PluralFilter extends NormalizationFilter
{
   protected Map rules = null;
   
   public PluralFilter(NormalizationFilter chainedFilter)
   {
      super(chainedFilter);
		rules = new LinkedHashMap();

      // Irregular rules.
   	rules.put("child", "children");
   	rules.put("series", "series");
   	rules.put("^(ax)is$", "$1es");
   	rules.put("(octop)us$", "$1i");
   	rules.put("(buffal|tomat)o$", "$1oes");
   	rules.put("(\\w+[ti])um$", "$1a");
   	rules.put("(\\w+)sis$", "$1ses");
   	rules.put("(?:([^f])fe|([lr])f)$", "$1$2ves");
   	rules.put("(matr|vert|ind)ix|ex$", "$1ices");
   	rules.put("^(ox)$", "$1en");
   	rules.put("(\\w+)(x|ch|ss|sh)$", "$1$2es");
   	rules.put("([m|l])ouse$", "$1ice");
   	rules.put("(quiz)$", "$1zes");
   	rules.put("(\\w*)(f)$", "$1ves");
   	rules.put("(\\w*)(fe)$", "$1ves");
   	rules.put("(\\w*)person$", "$1people");
   	rules.put("(\\w*)man$", "$1men");
   	
   	// Default rules.
		rules.put("(\\w+[^aeiouy])y$", "$1ies");
	   rules.put("(\\w+)s$", "$1ses");
      rules.put("(\\w+)$", "$1s");
   }

   public PluralFilter()
   {
      this(null);
   }
   
   protected String getNormalizedString(final String string)
   {
		Iterator iterator = rules.entrySet().iterator();
		while(iterator.hasNext())
		{
			Map.Entry rule = (Map.Entry)iterator.next();
			String pattern = rule.getKey().toString();
			String replace = rule.getValue().toString();
			if(string.matches(pattern))
				return string.replaceFirst(pattern, replace);
		}
		
		return string;
	}
}