package testing.com.vtls.opensource.utilities;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;

import com.vtls.opensource.utilities.DateUtilities;

public class DateUtilitiesTestCase extends TestCase
{
	 // Log4j instance.
    private static final Logger m_logger = Log4JLogger.getLogger(DateUtilitiesTestCase.class);
	
	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testLifeCycle() throws ParseException
	{
		assertNotNull(DateUtilities.getDateFromString("07/27/06","MM/dd/yyyy"));
		
		// Fri, Sep 15, '06
		m_logger.debug(DateUtilities.getFormattedDate(new Date(),"EEE, MMM d, ''yy"));
		// FriSep1506
		m_logger.debug(DateUtilities.getFormattedDate(new Date(),"EEEMMMdyy"));
	}
	
	public void testGMTtoLocal() /* and everything else */ {
		//Test EST (-4).
		Date gmt = DateUtilities.getDateFromString("06/06/2006,10:06:06.666","MM/dd/yyyy,hh:mm:ss.SSS");
		Date local = DateUtilities.convertGMTtoLocal(gmt);
		String local_string = DateUtilities.getFormattedDate(local, "MM/dd/yy,hh:mm:ss.SSS");
		assertEquals("06/06/06,06:06:06.666", local_string);
		
		//Test EDT (-5).
		gmt = DateUtilities.getDateFromString("11/11/2011,16:11:11.111","MM/dd/yyyy,hh:mm:ss.SSS");
		local = DateUtilities.convertGMTtoLocal(gmt);
		local_string = DateUtilities.getFormattedDate(local, "MM/dd/yy,hh:mm:ss.SSS");
		assertEquals("11/11/11,11:11:11.111", local_string);
	}
}
