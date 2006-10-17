package testing.com.vtls.opensource.formats;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class PackageTestCase extends TestCase
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Format Tests");
      //suite.addTestSuite(FormatsTestCase.class);
		suite.addTestSuite(FormatPropertiesFactoryTestCase.class);
		return suite;
	}
	
	public static void main(String args[])
	{
		junit.textui.TestRunner.run(suite());
	}
}
