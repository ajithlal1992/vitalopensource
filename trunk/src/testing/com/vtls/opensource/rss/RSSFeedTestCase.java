package testing.com.vtls.opensource.rss;

import junit.framework.TestCase;
import java.util.Date;

import com.vtls.opensource.rss.RSSFeed;
import com.vtls.opensource.rss.GenericFeedEntry;

public class RSSFeedTestCase extends TestCase
{
   private RSSFeed m_feed;
   
	public RSSFeedTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testGeneric()
	{
	   m_feed = new RSSFeed("Generic Feed", "http://www.w3c.org", "A generic feed.", "en-us");
	   m_feed.addFeedEntry(new GenericFeedEntry("Feed One", "http://www.w3c.org/1", "Description One", new Date()));
	   m_feed.addFeedEntry(new GenericFeedEntry("Feed Two", "http://www.w3c.org/2", "Description Two"));

	   String feed_string = m_feed.toString();
	   assertNotNull(feed_string);
	}

	public void testAssert()
	{
		assertTrue(true);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(RSSFeedTestCase.class);
	}
}
