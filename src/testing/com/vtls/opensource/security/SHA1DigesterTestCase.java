package testing.com.vtls.opensource.security;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;

import com.vtls.opensource.security.SHA1MessageDigester;

import junit.framework.TestCase;

public class SHA1DigesterTestCase extends TestCase
{
	public SHA1DigesterTestCase(String name) {
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown() {
	}

	public void testAssert() {
		assertTrue(true);
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(SHA1DigesterTestCase.class);
	}
	
	public void testHash() {
		try {
			String message = "Hello, World!";
			String hash = "aa9f2a6772942557ab5355d76af442f8f65e1";
			String result = SHA1MessageDigester.digestToHexString(new ByteArrayInputStream(message.getBytes("UTF-8")));
			assertEquals(hash, result);
			
			hash = "82e4c3eb40d95adc579a335466aeabe3f72e7c56";
			result = SHA1MessageDigester.digestToHexString(new FileInputStream("src/testing/data/sample-large.mov"));
			assertEquals(hash, result);
		} catch (Exception e) {
			System.out.println(e.toString());
			fail();
		}
	}
}
