package testing.com.vtls.opensource.experimental;

import junit.framework.TestCase;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.vtls.opensource.utilities.PreservingHashMap;

public class KeywordExtractionTestCase extends TestCase
{
   private static final String m_test_filename = "src/testing/data/corpus.txt";

	public KeywordExtractionTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testAssert() throws FileNotFoundException, IOException
	{
	   Map map = new PreservingHashMap();
	   
	   // Load the corpus into a String.
	   FileInputStream input_stream = new FileInputStream(m_test_filename);
	   String string = getStringFromInputStream(input_stream).toLowerCase().replaceAll("'", "");

      // Load the PreservingHashMap with the individual words along with their
      // position in the document.
	   String[] string_array = string.split("\\p{Space}|\\p{Punct}");
	   for(int i = 0; i < string_array.length; i++)
	      map.put(string_array[i], new Integer(i));
      
      // Display the state of the Map. The map is in order by term ocurrence.
      Iterator iterator = map.keySet().iterator();
      while(iterator.hasNext())
      {
         String term = (String)iterator.next();
         List list = (List)map.get(term);
         
         // Process the term.
         processTerm(string_array, map, term);
      }
	}
	
	public void processTerm(String[] _array, Map _map, String _term)
	{
      List list = (List)_map.get(_term);
      
      PreservingHashMap match_map = new PreservingHashMap();
      
      for(int i = 0; i < list.size(); i++)
      {
         Integer integer_position = (Integer)(list.get(i));
         int position = integer_position.intValue() + 1;
         if(position < _array.length)
         {
            List following_list = (List)_map.get(_array[position]);
            match_map.put(_term, _array[position]);
         }
      }

      System.out.println(_term + "\t" + match_map);
   }

	public static String getStringFromInputStream(InputStream _stream) throws IOException, FileNotFoundException
	{
      StringWriter _return = new StringWriter();
      BufferedReader reader = new BufferedReader(new InputStreamReader(_stream));

      int length = 0;
      char buffer[] = new char[1024];

      while((length = reader.read(buffer, 0, 1024 )) != -1)
         _return.write(buffer, 0, length);

      return _return.toString();
   }

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(KeywordExtractionTestCase.class);
	}
}
