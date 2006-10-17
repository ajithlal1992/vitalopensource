package testing.com.vtls.opensource.handle;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;
import com.vtls.opensource.handle.HandleUtilities;
import net.handle.hdllib.HandleException;

public class HandleUtilitiesTestCase extends TestCase
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(HandleUtilitiesTestCase.class);

	public HandleUtilitiesTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testLifeCycle() throws HandleException
	{
	   HandleUtilities.resolveHandle("1808/126");
	}
}
