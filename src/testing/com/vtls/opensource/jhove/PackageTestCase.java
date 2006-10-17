package testing.com.vtls.opensource.jhove;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PackageTestCase extends TestCase
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("JHOVE Package Tests");
		suite.addTestSuite(JHOVEDocumentFactoryTestCase.class);
		return suite;
	}
	
	public static void main(String args[])
	{
		junit.textui.TestRunner.run(suite());
	}
}
