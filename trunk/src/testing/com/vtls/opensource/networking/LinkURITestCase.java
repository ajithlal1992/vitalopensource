package testing.com.vtls.opensource.networking;

import junit.framework.TestCase;
import com.vtls.opensource.networking.LinkURI;

public class LinkURITestCase extends TestCase
{
   private LinkURI m_link = null;
   
	public LinkURITestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testSimple()
	{
	   // TODO: This type of functionality would be useful, so the LinkURI
	   // class needs to be split to provide the typical getters and setters
	   // along with the additive functionality.
	   
	   m_link = new LinkURI("View");
	   m_link.setXHTML(false);
	   m_link.setParameter("will", "not");
	   m_link.setParameter("show", "up");
	   assertEquals("View", m_link.toString());
	}

	public void testCombined()
	{
	   m_link = new LinkURI("View").setXHTML(true).setParameter("locale", "en-us").setParameter("view", "Template");
	   assertEquals("View?locale=en-us&amp;view=Template", m_link.toString());
	}

	public void testCombinedRemove()
	{
	   m_link = new LinkURI("View").setXHTML(true).setParameter("locale", "en-us").setParameter("view", "Template");
	   assertEquals("View?locale=en-us&amp;view=Template", m_link.toString());
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(LinkURITestCase.class);
	}
}
