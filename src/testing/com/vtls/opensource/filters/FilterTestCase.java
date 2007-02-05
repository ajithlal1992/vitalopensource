package testing.com.vtls.opensource.filters;

import com.vtls.opensource.logging.Log4JLogger;
import junit.framework.TestCase;
import org.apache.log4j.Logger;

import com.vtls.opensource.normalization.NormalizationFilter;
import com.vtls.opensource.normalization.filters.PluralFilter;
import com.vtls.opensource.normalization.filters.UppercaseFilter;
import com.vtls.opensource.normalization.filters.StopwordFilter;
import com.vtls.opensource.normalization.filters.ISODateFilter;

public class FilterTestCase extends TestCase
{
   private static final Logger log = Log4JLogger.getLogger(FilterTestCase.class);

	protected void setUp() throws java.io.IOException
	{
	}

	protected void tearDown()
	{
	}

	public void testISODate()
	{
	   NormalizationFilter filter = new ISODateFilter();
	   assertEquals("19771228171500", filter.getString("1977-12-28T17:15:00"));
	   assertEquals("20050000000000", filter.getString("c2005"));
	   assertEquals("20050000000000", filter.getString("20050000000000"));
	   assertEquals("20050000000000", filter.getString("2005000000000011"));
	   assertEquals("20050000000000", filter.getString("Z-/!@#$%^&*()))2005000000000011"));
   }

	public void testStopwords()
	{
	   NormalizationFilter filter = new StopwordFilter();
	   assertEquals("zoo", filter.getString("The Zoo"));
	   assertEquals("elephant", filter.getString("an elephant"));
	   assertEquals("pygmie marmoset", filter.getString("A PYGMIE MARMOSET"));
	   assertTrue(!"pygmie marmoset".equals(filter.getString("ANY PYGMIE MARMOSET")));

	   String[] stopwords = { "a", "an", "the", "any" };
      filter = new StopwordFilter(stopwords);
	   assertEquals("pygmie marmoset", filter.getString("ANY PYGMIE MARMOSET"));
   }

	public void testLifeCycle()
	{
	   NormalizationFilter filter = new PluralFilter();
	   assertEquals("properties", filter.getString("property"));
	   assertEquals("movies", filter.getString("movie"));
	   assertEquals("keys", filter.getString("key"));
	   assertEquals("days", filter.getString("day"));
	   assertEquals("children", filter.getString("child"));
	   assertEquals("series", filter.getString("series"));
	   assertEquals("axes", filter.getString("axis"));
	   assertEquals("octopi", filter.getString("octopus"));
	   assertEquals("viruses", filter.getString("virus"));
	   assertEquals("aliases", filter.getString("alias"));
	   assertEquals("statuses", filter.getString("status"));
	   assertEquals("buses", filter.getString("bus"));
	   assertEquals("buffaloes", filter.getString("buffalo"));
	   assertEquals("tomatoes", filter.getString("tomato"));
	   assertEquals("analyses", filter.getString("analysis"));
	   assertEquals("consortia", filter.getString("consortium"));
	   assertEquals("knives", filter.getString("knife"));
	   assertEquals("elves", filter.getString("elf"));
	   assertEquals("taxes", filter.getString("tax"));
	   assertEquals("matrices", filter.getString("matrix"));
	   assertEquals("mice", filter.getString("mouse"));
	   assertEquals("oxen", filter.getString("ox"));
	   assertEquals("wives", filter.getString("wife"));
	   assertEquals("laypeople", filter.getString("layperson"));
	   assertEquals("men", filter.getString("man"));
	   assertEquals("women", filter.getString("woman"));
	   assertEquals("quizzes", filter.getString("quiz"));

	   // TODO: assertEquals("culs-de-sac", filter.getString("cul-de-sac"));
	}

   /*
	public void testLifeCycleChained()
	{
	   NormalizationFilter filter = new PluralFilter(new CapitalizingFilter());
	   assertEquals("Movies", filter.getString("movie"));
	}
	*/
}
