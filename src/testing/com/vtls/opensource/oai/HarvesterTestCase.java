package testing.com.vtls.opensource.oai;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import com.vtls.opensource.oai.Provider;
import com.vtls.opensource.oai.Harvester;
import com.vtls.opensource.oai.handlers.DefaultHandler;

import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Properties;

public class HarvesterTestCase extends TestCase
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(HarvesterTestCase.class);
   
   private Provider provider = null;
   private Harvester harvester = null;
   private DefaultHandler handler = null;

	public HarvesterTestCase(String name)
	{
		super(name);
	}

	protected void setUp() throws Exception
	{
		this.provider = new Provider("file:src/testing/data/oai");
		assertNotNull(this.provider);
		
		this.handler = new DefaultHandler();
		this.handler.initialize(new Properties(), new FileOutputStream("/dev/null"));
		
		this.harvester = new Harvester(this.handler, this.provider);
	}

	protected void tearDown()
	{
	}

	public void testLifeCycle()
	{
		assertTrue(true);
	}
	
	public void testHarvest() throws Exception {		
		boolean harvest = this.harvester.harvest(null, null, "oai_dc", null);
		assertTrue(harvest);
		
		assertTrue(handler.getProperty("last.identifer").equals("oai:dossj.vtls.com:vital:155"));
		assertTrue(handler.getProperty("last.datestamp").equals("2006-06-06T13:07:12Z"));
	}
}
