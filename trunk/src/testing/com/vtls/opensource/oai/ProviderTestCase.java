package testing.com.vtls.opensource.oai;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import com.vtls.opensource.oai.Provider;

import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.IOException;

public class ProviderTestCase extends TestCase
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(ProviderTestCase.class);
   
   private Provider provider = null;

	public ProviderTestCase(String name)
	{
		super(name);
	}

	protected void setUp() throws Exception
	{
		this.provider = new Provider("file:src/testing/data/oai");
		assertNotNull(this.provider);
	}

	protected void tearDown()
	{
	}

	public void testLifeCycle()
	{
		assertTrue(true);
	}
	
	public void testListIdentifiers() throws Exception {
		//Will succeed.
		Element verb = this.provider.listIdentifiers(null, null, "oai_dc", null);
		assertNotNull(verb);
		
		String resumptionToken = this.provider.getResumptionToken();
		assertTrue("0bc68a77327613d9644966ea1b8a4ca5".equals(resumptionToken));
		
		//Resumption token.
		verb = this.provider.listIdentifiers(resumptionToken);
		assertNotNull(verb);
		
		//Will fail.
		verb = this.provider.listIdentifiers(null, null, "fail", null);
		assertNull(verb);
		assertTrue("cannotDisseminateFormat".equals(this.provider.getErrorCode()));
	}
	
	public void testListRecords() throws Exception {
		//Will succeed.
		Element verb = this.provider.listRecords(null, null, "oai_dc", null);
		assertNotNull(verb);
		
		String resumptionToken = this.provider.getResumptionToken();
		assertTrue("32046a51fe0117afcb101dfa825c576a".equals(resumptionToken));
		
		//Resumption token.
		verb = this.provider.listRecords(resumptionToken);
		assertNotNull(verb);
		
		//Will fail.
		verb = this.provider.listRecords(null, null, "fail", null);
		assertNull(verb);
		assertTrue("cannotDisseminateFormat".equals(this.provider.getErrorCode()));
	}
	
	public void testListMetadataFormats() throws Exception {
		Element verb = this.provider.listMetadataFormats();
		assertNotNull(verb);
		
		verb = this.provider.listMetadataFormats("oai:dossj.vtls.com:demo:12");
		assertNotNull(verb);
	}
	
	public void testListSets() throws Exception {
		//Will succeed.
		Element verb = this.provider.listSets();
		assertNotNull(verb);
	}
}
