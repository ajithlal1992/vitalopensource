package testing.com.vtls.opensource.security;

import com.vtls.opensource.security.RSAKeyPair;

import java.security.KeyPair;
import junit.framework.TestCase;

public class RSAKeyPairTestCase extends TestCase
{
	private KeyPair key_pair;
	
	public RSAKeyPairTestCase(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		key_pair = RSAKeyPair.create();
	}

	protected void tearDown() {
	}

	public void testAssert() {
		assertTrue(true);
	}

	public static void main(String args[]) {
		junit.textui.TestRunner.run(RSAKeyPairTestCase.class);
	}
	
	public void testKeyPairCreation() {
		assertNotNull(key_pair);
	}
	
	public void testIO() throws Exception {
			//Write together as a pair
			RSAKeyPair.write(key_pair, "public.key.1", "private.key.1");
			//Read together as a pair
			assertNotNull(RSAKeyPair.read("public.key.1", "private.key.1"));
			//Write individually
			RSAKeyPair.write(key_pair.getPublic(), "public.key.2");
			RSAKeyPair.write(key_pair.getPrivate(), "private.key.2");
			//Read individually
			assertNotNull(RSAKeyPair.read("public.key.2"));
			assertNotNull(RSAKeyPair.read("private.key.2"));
	}
	
	public void testCipher() throws Exception {
		String message = "Hello, World!";
		String encrypted_message = RSAKeyPair.encrypt(message, key_pair.getPrivate());
		String decrypted_message = RSAKeyPair.decrypt(encrypted_message, key_pair.getPublic());
		assertEquals(message, decrypted_message);
	}
}
