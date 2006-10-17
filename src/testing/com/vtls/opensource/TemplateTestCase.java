package testing.com.vtls.opensource;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

public class TemplateTestCase extends TestCase
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(TemplateTestCase.class);

	public TemplateTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testLifeCycle()
	{
		assertTrue(true);
	}
}
