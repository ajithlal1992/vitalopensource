package com.vtls.opensource.ldap;

import edu.vt.middleware.ldap.Ldap;
import edu.vt.middleware.ldap.LdapConfig;
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
	 * Do we allow a partial match on the DN?
	 */
	protected boolean allowPartialDNMatch = false;

	/**
	 * The field containing the user ID.
	 */
	protected String userField = "uid";
	
	/**
	 * Default constructor which prepares the configuration and authenticator.
	 * 
	 * @param _hostname The hostname of the LDAP server.
	 */
	public LDAP(String _hostname) {
		//Prepare the configuration.
		this.ldapConfig = new LdapConfig();
		//Set the hostname.
		this.ldapConfig.setHost(_hostname);
		//Prepare the authenticator.
		this.authenticator = new Authenticator(this.ldapConfig);
	}
	
	/**
	 * Sets the port to be the specified value.
	 * 
	 * @param _port The port to be used.
	 */
	public void setPort(int _port) {
		this.ldapConfig.setPort(Integer.toString(_port));
	}
	
	/**
	 * Sets the base DN.
	 * 
	 * @param _baseDN The base DN.
	 */
	public void setBaseDN(String _baseDN) {
		this.ldapConfig.setBase(_baseDN);
	}
	
	/**
	 * Sets the filter used to authorize a user.
	 * 
	 * @param _authorizationFilter The filter.
	 */
	public void setAuthorizationFilter(String _authorizationFilter) {
		this.authenticator.setAuthorizationFilter(_authorizationFilter);
	}
	
	/**
	 * Secures the connection using the SSL protocol.
	 */
	public void useSSL() {
		//Secure the connection.
		this.ldapConfig.useSsl(true);
	}
	
	/**
	 * Secures the communication using the TLS protocol.
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
		this.authenticator.setUserField(this.userField);
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
			return this.authenticator.authenticate(_username, _password);
		} catch (NamingException ne) {
			m_logger.error(ne);
			throw new Exception(ne);
		} catch (Exception e) {
			m_logger.error(e);
			throw e;
		}
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
			//Create an LDAP object.
			Ldap ldap = new Ldap(this.ldapConfig);
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
		} catch (NamingException ne) {
			m_logger.error(ne);
			throw new Exception(ne);
		} catch (Exception e) {
			m_logger.error(e);
			throw e;
		}
		return _return;
	}
}