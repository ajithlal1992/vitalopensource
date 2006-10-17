package com.vtls.opensource.ldap;

import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.LdapConfig;
import edu.vt.middleware.ldap.LdapPool;
import edu.vt.middleware.ldap.Authenticator;
import edu.vt.middleware.ldap.LdapConstants;
import edu.vt.middleware.ldap.LdapResult;
import edu.vt.middleware.ldap.LdapEntry;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;

import java.util.Properties;
import java.util.Iterator;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

/**
 * Connects to an LDAP server to authenticate and/or describe a user.
 * 
 * http://www.middleware.vt.edu/pubs/opensource/ldap/
 * 
 * @author Chris Hall
 */
public class LDAP {
	/**
	 * The logger.
	 */
	private static final Logger m_logger = Log4JLogger.getLogger(LDAP.class);
	
	/**
	 * Configures the LDAP.
	 */
	protected LdapConfig ldapConfig = null;
	
	/**
	 * Used to authenticate a user.
	 */
	protected Authenticator authenticator = null;
	
	/**
	 * Used to return objects which perform LDAP actions.
	 */
	protected LdapPool pool = null;

	/**
	 * The field containing the user ID.
	 */
	protected String userField = "uid";
	
	/**
	 * I actually don't know.  But this is what's recommended.
	 */
	protected static int maxSleeping = 8;
	
	/**
	 * The initial size of the pool.
	 */
	protected static int initSize = 4;
	
	/**
	 * Default constructor which prepares the config, authenticator and the pool.
	 * 
	 * @param _hostname The hostname of the LDAP server.
	 * @param _baseDN The base dn.
	 */
	public LDAP(String _hostname, String _baseDN) {
		this.ldapConfig = new LdapConfig(_hostname, _baseDN);
		//Prepare the authenticator.
		this.authenticator = new Authenticator(this.ldapConfig);
		//Prepare the object pool.
		this.pool = new LdapPool(this.ldapConfig, this.maxSleeping, this.initSize);
	}
	
	/**
	 * Secures the authentication connection using the TLS protocol.
	 * 
	 * @throws Exception If the connection cannot be secured.
	 */
	public void useTLS() throws Exception {
		try {
			//Secure the connection.
			this.authenticator.useTls(true);
		} catch (NamingException ne) {
			throw new Exception(ne);
		}
	}
	
	/**
	 * Sets the user field to a custom value.
	 * 
	 * @param _userField The user field.
	 */
	public void setUserField(String _userField) {
		//VT, for example, uses "uupid" instead of "uid".
		this.userField = _userField;
		this.authenticator.setUserField(userField);
	}
	
	/**
	 * Attempts to authenticate the user base on username and password.
	 * 
	 * @param _username The username.
	 * @param _password The password.
	 * 
	 * @throws Exception If something goes wrong with the connection.
	 */
	public boolean authenticateUser(String _username, String _password)
	throws Exception {
		try {
			return authenticator.authenticate(_username, _password);
		} catch (NamingException ne) {
			m_logger.error(ne);
			throw new Exception(ne);
		} catch (Exception e) {
			m_logger.error(e);
			throw e;
		}
	}
	
	/**
	 * Gets all the user attributes form the LDAP server and returns them as a Properties object.
	 * 
	 * @param _username The username.
	 * 
	 * @throws Exception If something goes wrong with the connection.
	 */
	public Properties describeUser(String _username)
	throws Exception {
		//Prepare the return object.
		Properties _return = new Properties();
		try {
			//Borrow an LDAP object.
			Ldap ldap = (Ldap) this.pool.borrowObject();
			//NULL check.
			if (ldap != null) {
				//Iterate through the results.
				Iterator results = ldap.search(this.userField + "=" + _username);
				while (results.hasNext()) {
					SearchResult result = (SearchResult) results.next();
					//Iterate through all the attributes.
					NamingEnumeration all_attributes = result.getAttributes().getAll();
					while (all_attributes.hasMore()) {
						Attribute currentAttribute = (Attribute) all_attributes.next();
						//Iterate through each attribute to make sure we catch duplicate IDs.
						NamingEnumeration attributes = currentAttribute.getAll();
						while (attributes.hasMore()) {
							Object object = attributes.next();
							//Need to type check before blindly putting then into the return.
							if (object instanceof String) {
								//It's fine if it's a String.
								String attribute = (String) object;
								if (_return.getProperty(currentAttribute.getID()) != null) {
									//Handle the multiple instances of the same attribute id.
									attribute = _return.getProperty(currentAttribute.getID()) + "|" + attribute;
								}
								_return.setProperty(currentAttribute.getID(), attribute);
							}
						}
					}
				}
				//Return it.
				this.pool.returnObject(ldap);
			}
		} catch (NamingException ne) {
			m_logger.error(ne);
			throw new Exception(ne);
		} catch (Exception e) {
			m_logger.error(e);
			throw e;
		}
		return _return;
	}
	
	/**
	 * Attempts to authenticate and describe the user base on username and password.
	 * 
	 * @param _username The username.
	 * @param _password The password.
	 * 
	 * @throws Exception If something goes wrong with the connection.
	 */
	public Properties authenticateAndDescribeUser(String _username, String _password)
	throws Exception {
		//Prepare the return.
		Properties _return = new Properties();
		//Default to not authenticated.
		_return.setProperty("user.authenticated", "false");
		//Authenticate.
		if (this.authenticateUser(_username, _password)) {
			//Describe the user.
			_return.putAll(this.describeUser(_username));
			_return.setProperty("user.authenticated", "true");
		}
		return _return;
	}
}