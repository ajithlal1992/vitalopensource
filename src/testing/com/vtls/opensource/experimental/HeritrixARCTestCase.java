package testing.com.vtls.opensource.experimental;

import junit.framework.TestCase;
import java.net.URL;

/*
import org.archive.crawler.admin.CrawlJob;
import org.archive.crawler.datamodel.CrawlOrder;
import org.archive.crawler.settings.ComplexType;
import org.archive.crawler.settings.StringList;
import org.archive.crawler.writer.ARCWriterProcessor;
import org.archive.io.arc.ARCReader;
import org.archive.io.arc.ARCReaderFactory;
import org.archive.io.arc.ARCRecordMetaData;
import org.archive.util.FileUtils;

import org.archive.crawler.admin.CrawlJobErrorHandler;
*/

public class HeritrixARCTestCase extends TestCase
{
	public HeritrixARCTestCase(String name)
	{
		super(name);
	}

	protected void setUp() throws Exception
	{
      URL url = new URL("http://www.vtls.com/");

      /*
      File arc_file = new File("src/testing/data/archive.arc");
      ARCReader arc_reader = ARCReaderFactory.get(arc_file);
      List metadata_list = arc_reader.validate();
      System.out.println(metadata_list.toString());
      */
	}

	protected void tearDown()
	{
	}

	public void testAssert()
	{
		assertTrue(true);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(HeritrixARCTestCase.class);
	}
}
