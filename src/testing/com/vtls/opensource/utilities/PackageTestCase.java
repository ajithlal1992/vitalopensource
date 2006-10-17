package testing.com.vtls.opensource.utilities;

import junit.framework.Test;
import junit.framework.TestSuite;

public class PackageTestCase
{
	public static Test suite()
	{
		TestSuite suite = new TestSuite("Utilities Tests");
		//suite.addTestSuite(XMLUtilitiesTestCase.class);
		suite.addTestSuite(DateUtilitiesTestCase.class);
		//suite.addTestSuite(PagingIteratorTestCase.class);
		return suite;
	}
	
	public static void main(String args[]) throws java.text.ParseException
	{
		junit.textui.TestRunner.run(suite());
	}
}
