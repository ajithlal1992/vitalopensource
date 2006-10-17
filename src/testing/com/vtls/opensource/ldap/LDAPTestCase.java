package testing.com.vtls.opensource.ldap;

import com.vtls.opensource.ldap.LDAP;

import java.util.Properties;

import junit.framework.TestCase;

public class LDAPTestCase extends TestCase {
	protected static String hostname = "authn.directory.vt.edu";
	protected static int port = 389;
	protected static boolean useTLS = true;
	
	protected static String baseDN = "ou=People,dc=vt,dc=edu";
	protected static String userField = "uupid";
	
	protected static String username = "wmcquain";
	protected static String password = null;
	
	LDAP ldap = null;
	
	public LDAPTestCase(String name)
	{
		super(name);
	}

	protected void setUp() throws Exception
	{
		//Use VT's library
		this.ldap = new LDAP(hostname, baseDN);
		//Set a custome user field.
		this.ldap.setUserField(userField);
		if (useTLS) {
			//Use TLS protocol.
			this.ldap.useTLS();
		}
	}

	protected void tearDown()
	{
	}

	public void testAssert()
	{
		assertTrue(true);
	}
	
	public void testAuthenticateUser() throws Exception {
		if (password != null) {
			//Try to authenticate.
			assertTrue(this.ldap.authenticateUser(username, password));
		} else {
			System.err.println("NULL password.  Skipping LDAP user authentication test.");
		}
	}
	
	public void testDescribeUser() throws Exception {
		Properties userProps = this.ldap.describeUser(username);
		System.out.println(userProps);
		assertTrue(username.equals(userProps.getProperty(userField)));
	}
}