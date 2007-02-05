package testing.com.vtls.opensource.filters;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Properties;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;

import com.vtls.opensource.sort.SortComparator;
import com.vtls.opensource.normalization.filters.LowercaseFilter;
import com.vtls.opensource.normalization.filters.ISODateFilter;
import com.vtls.opensource.normalization.filters.StopwordFilter;
import com.vtls.opensource.normalization.filters.UppercaseFilter;
import com.vtls.opensource.normalization.filters.LeadingZeroFilter;

public class ComparatorTestCase extends TestCase
{
   // Log4j instance.
   private static final Logger log = Log4JLogger.getLogger(ComparatorTestCase.class);

	public ComparatorTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testLifeCycle()
	{
	   List list = new ArrayList();
	   list.add("B");
	   list.add("A");
	   list.add("C");
	   assertEquals("BAC", StringUtils.join(list.iterator(), ""));
	   
      ////////////////////////////////////////////////////////////////////////
      // Ascending. //////////////////////////////////////////////////////////
	   SortComparator comparator = new SortComparator();
	   comparator.addAscendingSort(SortComparator.String(), new LowercaseFilter());

      Collections.sort(list, comparator);
	   assertEquals("ABC", StringUtils.join(list.iterator(), ""));

      ////////////////////////////////////////////////////////////////////////
      // Descending. /////////////////////////////////////////////////////////
      comparator = new SortComparator();
	   comparator.addDescendingSort(SortComparator.String(), new LowercaseFilter());      

      Collections.sort(list, comparator);
	   assertEquals("CBA", StringUtils.join(list.iterator(), ""));

		assertTrue(true);
	}

	public void testLifeCycleMap()
	{
	   // See: http://java.sun.com/developer/JDCTechTips/2001/tt1023.html
	   String words[] = { "The man", "Man", "Woman", "wombat", "Manana", "manhattan",
	      "mañana", "Masking tape", "A Mantra", "mantra", "An antfarm", "An Ante" }; 

	   List list = new ArrayList();

	   for(int i = 0; i < words.length; i++)
	   {
	      Properties properties = new Properties();
	      properties.setProperty("word", words[i]);
	      properties.setProperty("order", String.valueOf(i));
   	   list.add(properties);
      }

      log.info(StringUtils.join(list.iterator(), "\n"));

      // Ascending Sort
	   SortComparator comparator = new SortComparator();
	   comparator.addAscendingSort(SortComparator.String("word"));
      Collections.sort(list, comparator);
      log.info(StringUtils.join(list.iterator(), "\n"));
      log.info(StringUtils.join(getListForNestedMapField(list, "order").iterator(), ","));
      assertEquals("8,11,10,1,4,7,0,2,5,9,6,3",
         StringUtils.join(getListForNestedMapField(list, "order").iterator(), ","));

      comparator.clear();
	   comparator.addAscendingSort(SortComparator.String("word"), new UppercaseFilter());
      Collections.sort(list, comparator);
      log.info(StringUtils.join(list.iterator(), "\n"));
      log.info(StringUtils.join(getListForNestedMapField(list, "order").iterator(), ","));
      assertEquals("8,11,10,1,4,5,9,7,6,0,2,3",
         StringUtils.join(getListForNestedMapField(list, "order").iterator(), ","));
         
      // Ascending Sort with Filter
      comparator.clear();
	   comparator.addAscendingSort(SortComparator.String("word"), new StopwordFilter());
      Collections.sort(list, comparator);
      log.info(StringUtils.join(list.iterator(), "\n"));
      log.info(StringUtils.join(getListForNestedMapField(list, "order").iterator(), ","));
      assertEquals("11,10,1,0,4,5,8,9,7,6,2,3",
         StringUtils.join(getListForNestedMapField(list, "order").iterator(), ","));
	}

   private List getListForNestedMapField(final List list, final String field)
   {
	   List _return = new ArrayList();
	   
	   Iterator iterator = list.iterator();
	   while(iterator.hasNext())
	   {
	      Map map = (Map)iterator.next();
   	   _return.add(map.get(field));
      }
      return _return;
   }

	public void testLifeCycleNumeric()
	{
	   String order[] = { "001", "2", "10", "11", "12", "13",
	      "14", "15", "25", "0100", "1000", "170" }; 
      List list = new ArrayList(Arrays.asList(order));

	   SortComparator comparator = new SortComparator();
	   comparator.addAscendingSort(SortComparator.Numeric(), new LeadingZeroFilter());
      Collections.sort(list, comparator);
      log.info(StringUtils.join(list.iterator(), "\n"));
      
	   // comparator.addAscendingSort(SortComparator.Numeric("order"));
   }
}
