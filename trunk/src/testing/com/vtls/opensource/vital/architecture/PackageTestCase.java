package testing.com.vtls.opensource.vital.architecture;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PackageTestCase extends TestCase
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("VITAL Architecture Tests");
		suite.addTestSuite(LinkTestCase.class);
		suite.addTestSuite(ArchitectureFilterTestCase.class);
		return suite;
	}
	
	public static void main(String args[])
	{
		junit.textui.TestRunner.run(suite());
	}
}
