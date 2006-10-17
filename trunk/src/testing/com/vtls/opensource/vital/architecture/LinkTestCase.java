package testing.com.vtls.opensource.vital.architecture;

import junit.framework.TestCase;
import com.vtls.opensource.vital.architecture.Link;

public class LinkTestCase extends TestCase
{
   private Link m_link = null;
   
	public LinkTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testTypicalUsage()
	{
	   m_link = new Link("/administration/manager/Index");
	   m_link.setBaseTarget("/vital");
	   m_link.setParameter("parameter1", "value1");
	   m_link.setParameter("parameter2", "value2");
	   m_link.removeParameter("parameter1");
      assertEquals("/vital/administration/manager/Index/Action?parameter2=value2", m_link.changeTarget("Action").toString());
	   assertEquals("/vital/administration/manager/Index?parameter2=value2", m_link.toString());
	   
	   Link temp = m_link;
	   m_link.setTarget("/administration/manager/Index");
	   assertEquals("/vital/administration/manager/Index?parameter2=value2", temp.toString());
	}

	public void testSetTarget()
	{
	   m_link = new Link("/administration/manager/Index");
	   m_link.setBaseTarget("/vital");
	   m_link.modifyTarget("../AlternatePage");

	   assertEquals("/vital/administration/manager/AlternatePage", m_link.toString());
	}

	public void testCombined()
	{
	   m_link = new Link("View").setXHTML(true).setParameter("locale", "en-us").setParameter("view", "Template");
	   System.out.println("Combined: " + m_link.toString());
	   assertEquals("View?locale=en-us&amp;view=Template", m_link.toString());
	}

	public void testCombinedRemove()
	{
	   m_link = new Link("View").setXHTML(true).setParameter("locale", "en-us").setParameter("view", "Template");
	   System.out.println("CombinedRemove: " + m_link.toString());
	   assertEquals("View?locale=en-us&amp;view=Template", m_link.toString());
	}

	public void testRemovePattern()
	{
	   m_link = new Link("View").setXHTML(true).setParameter("f0", "filter1").setParameter("locale", "en-us").setParameter("locale", "en-us").setParameter("f5", "filter5");
	   System.out.println("RemovePattern: " + m_link.clearParameters("^f\\d+$").toString());
	   assertEquals("View?locale=en-us", m_link.toString());
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(LinkTestCase.class);
	}
}
