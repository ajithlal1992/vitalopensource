package testing.com.vtls.opensource.filters;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PackageTestCase extends TestCase
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("JUnit Test Cases");
		suite.addTestSuite(ComparatorTestCase.class);
//		suite.addTestSuite(FilterTestCase.class);
		return suite;
	}
	
	public static void main(String args[])
	{
		junit.textui.TestRunner.run(suite());
	}
}

