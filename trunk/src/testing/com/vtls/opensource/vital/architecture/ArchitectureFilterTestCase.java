package testing.com.vtls.opensource.vital.architecture;

import junit.framework.TestCase;
import java.io.IOException;

public class ArchitectureFilterTestCase extends TestCase
{
	public ArchitectureFilterTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testAssert() throws IOException
	{
		assertTrue(true);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(ArchitectureFilterTestCase.class);
	}
}

