package testing.com.vtls.opensource.services;

import junit.framework.TestCase;

public class XFireTestCase extends TestCase
{
	public XFireTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
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
		junit.textui.TestRunner.run(XFireTestCase.class);
	}
}
