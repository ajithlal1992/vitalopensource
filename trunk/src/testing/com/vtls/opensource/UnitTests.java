package testing.com.vtls.opensource;

import testing.com.vtls.opensource.TemplateTestCase;
import testing.com.vtls.opensource.cql.CQLLuceneBridgeTestCase;
import testing.com.vtls.opensource.fedora.FedoraResourceIndexTestCase;
import testing.com.vtls.opensource.rss.RSSFeedTestCase;

import testing.com.vtls.opensource.experimental.VelocityDVSLTestCase;
import testing.com.vtls.opensource.experimental.KeywordExtractionTestCase;
import testing.com.vtls.opensource.experimental.LDAPTestCase;
import testing.com.vtls.opensource.security.RSAKeyPairTestCase;
import testing.com.vtls.opensource.security.SHA1DigesterTestCase;

import testing.com.vtls.opensource.text.HTMLTextSourceTestCase;
import testing.com.vtls.opensource.text.PDFTextSourceTestCase;
import testing.com.vtls.opensource.text.RTFTextSourceTestCase;
import testing.com.vtls.opensource.text.TextSourceTestCase;
import testing.com.vtls.opensource.text.WordTextSourceTestCase;
import testing.com.vtls.opensource.text.TextSourceFactoryTestCase;
import testing.com.vtls.opensource.text.xhtml.MicrosoftWordXHTMLSourceTestCase;

import testing.com.vtls.opensource.vital.architecture.ArchitectureFilterTestCase;

import testing.com.vtls.opensource.services.XFireTestCase;

import testing.com.vtls.opensource.oai.ProviderTestCase;
import testing.com.vtls.opensource.oai.HarvesterTestCase;
//import testing.com.vtls.opensource.ldap.LDAPAuthenticatorTestCase;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Test this package with the imported TestCases.
 */
public class UnitTests extends TestCase
{
	public UnitTests(String name)
	{
		super(name);
	}

	public static Test suite()
	{
		TestSuite suite = new TestSuite(TemplateTestCase.class);
		suite.addTest(new TestSuite(XFireTestCase.class));
		suite.addTest(new TestSuite(ArchitectureFilterTestCase.class));
		suite.addTest(new TestSuite(KeywordExtractionTestCase.class));

		// com.vtls.opensource.experimental
		suite.addTest(new TestSuite(VelocityDVSLTestCase.class));

		// com.vtls.opensource.cql
		suite.addTest(new TestSuite(CQLLuceneBridgeTestCase.class));

		// com.vtls.opensource.fedora
		suite.addTest(new TestSuite(FedoraResourceIndexTestCase.class));

		// com.vtls.opensource.rss
		suite.addTest(new TestSuite(RSSFeedTestCase.class));

		// com.vtls.opensource.text
		suite.addTest(new TestSuite(HTMLTextSourceTestCase.class));
		suite.addTest(new TestSuite(PDFTextSourceTestCase.class));
		suite.addTest(new TestSuite(RTFTextSourceTestCase.class));
		suite.addTest(new TestSuite(TextSourceTestCase.class));
		suite.addTest(new TestSuite(WordTextSourceTestCase.class));
		suite.addTest(new TestSuite(TextSourceFactoryTestCase.class));
		suite.addTest(new TestSuite(MicrosoftWordXHTMLSourceTestCase.class));

		// com.vtls.opensource.security
		suite.addTest(new TestSuite(RSAKeyPairTestCase.class));
		suite.addTest(new TestSuite(SHA1DigesterTestCase.class));

		// com.vtls.opensource.oai
		suite.addTest(new TestSuite(ProviderTestCase.class)); 
		suite.addTest(new TestSuite(HarvesterTestCase.class)); 
		
//		// com.com.vtls.opensource.experimental
//		suite.addTest(new TestSuite(LDAPTestCase.class));
		
		// com.vtls.opensource.ldap
		suite.addTest(new TestSuite(testing.com.vtls.opensource.ldap.LDAPTestCase.class));
		
		return suite;
	}
}
