package testing.com.vtls.opensource.ldap;

import com.vtls.opensource.ldap.LDAP;

import java.util.Properties;

import junit.framework.TestCase;

public class LDAPTestCase extends TestCase {
	protected static String hostname = "soliloquy.vtls.com";
	protected static int port = 389;
	protected static boolean useTLS = false;
	
	protected static String baseDN = "o=QC,dc=vtls,dc=com";
	protected static String userField = "cn";
	
	protected static String username = "stubstadr";
	protected static String password = "ilovevital";
	protected static String filter = "(telephoneNumber=x3419)";
	
	LDAP ldap = null;
	
	public LDAPTestCase(String name)
	{
		super(name);
	}

	protected void setUp() throws Exception
	{
		//Use VT's library
		this.ldap = new LDAP(hostname);
		this.ldap.setBaseDN(baseDN);
		this.ldap.setPort(port);
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
			if (filter != null) {
				this.ldap.setAuthorizationFilter(filter);
			}
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