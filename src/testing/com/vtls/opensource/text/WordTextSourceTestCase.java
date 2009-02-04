package testing.com.vtls.opensource.text;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import com.vtls.opensource.text.WordTextSource;

import junit.framework.TestCase;

public class WordTextSourceTestCase extends TestCase
{
   private static final String m_test_filename = "src/testing/data/test.doc";
	private WordTextSource m_text_source = null;
	
	public WordTextSourceTestCase(String name)
	{
		super(name);
	}

	protected void setUp() throws IOException
	{
		m_text_source = new WordTextSource(new FileInputStream(m_test_filename));
	}

	protected void tearDown()
	{
	}

	public void testGetText() throws IOException
	{
		String text = m_text_source.getText();
		assertNotNull(text);
		assertTrue(text.indexOf("A B C D") >= 0);
		
		// TODO: Find a Word extractor that handles the full Unicode set.
		// assertTrue(text.contains("豈 更 車 賈"));
	}
	
	public void testPOITest() throws FileNotFoundException, IOException
	{
      POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(m_test_filename));
      DirectoryEntry directory = fs.getRoot();

/*
      Iterator i = directory.getEntries();
      while(i.hasNext())
      {
          Entry entry = (Entry)i.next();
          System.out.print("Entry: " + entry.getName());
          if(entry instanceof DirectoryEntry)
             System.out.println(" (Directory)");
          if(entry instanceof DocumentEntry)
             System.out.println(" (Document)");
       }
*/
   }
		
	public void testAssert()
	{
		assertTrue(true);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(WordTextSourceTestCase.class);
	}
}
