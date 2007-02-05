package testing.com.vtls.opensource.utilities;

import java.net.URL;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import com.vtls.opensource.utilities.URLEncoder;

public class URLEncoderTestCase extends TestCase {
	 // Log4j instance.
    private static final Logger m_logger = Log4JLogger.getLogger(URLEncoderTestCase.class);
	
	protected void setUp() {
	}

	protected void tearDown() {
	}

	public void testLifeCycle() throws Exception {
		String unencoded = "http://chaos.vtls.com/cd/covers/Hail to the Thief - Radiohead (2003)199_f.jpg";
		String encoded = "http://chaos.vtls.com/cd/covers/Hail%20to%20the%20Thief%20-%20Radiohead%20(2003)199_f.jpg";
		URL unencodedURL = new URL(unencoded);
		System.err.println(unencodedURL);
		URL encodedURL = URLEncoder.encode(unencodedURL);
		System.err.println(encodedURL);
		assertEquals(encoded, encodedURL.toString());
	}
}
