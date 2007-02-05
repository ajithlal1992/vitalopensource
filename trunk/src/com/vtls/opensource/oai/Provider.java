package com.vtls.opensource.oai;

//import java.io.ByteArrayInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.DOMOutputter;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;

import org.apache.xerces.jaxp.validation.XMLSchemaFactory;

import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import javax.xml.transform.stream.StreamSource;
import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;

import org.xml.sax.SAXException;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

/**
 * A JDOM implementation of an OAI-PMH Provider "proxy".
 * This class merely provides a light API to a provider.
 * 
 *  @author Chris Hall
 *  @version 1.0
 */

public class Provider extends Properties {
	/**
	 * The logger.
	 */
	protected static final Logger m_logger = Log4JLogger.getLogger(Provider.class);
	
	/**
	 * Connects to the remote provider and returns its response as a Document.
	 */
	protected SAXBuilder builder = null;
	
	/**
	 * Converts the JDOM Document to a W3C DOM Document for validation.
	 */
	protected DOMOutputter dom_outputter = null;
	
	/**
	 * The namespace of the response
	 */
	protected Namespace namespace = null;
	
	/**
	 * The Document of the actual response.
	 */
	protected Document response = null;
	
	/**
	 * The 'verb' Element of the response.
	 */
	protected Element verb = null;
	
	/**
	 * The 'error' Element of the response.
	 */
	protected Element error = null;
	
	/**
	 * Performs the validation of the response.
	 */
	protected Validator validator = null;
	
	/**
	 * Used to parse Dates with date granularity.
	 */
	protected static String BASE_GRANULARITY = "yyyy-MM-dd";
	
	/**
	 * Used to parse Dates with second granularity.
	 */
	protected static String FINE_GRANULARITY = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	
	/**
	 * Calls the main constructor with a false validation flag.
	 * 
	 * @param _baseURL The baseURL of the remote provider.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Provider(String _baseURL)
			throws JDOMException, IOException {
		this(_baseURL, false);
	}
	
	/**
	 * Constructor.  Creates the builders, 'Identify's the remote provider, and initializes internal Properties.
	 * 
	 * @param _baseURL The baseURL of the remote provider.
	 * @param _validation Whether or not we are to validate the responses.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document or setting up of the validation environment fails.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Provider(String _baseURL, boolean _validation)
			throws JDOMException, IOException {
		//Store the baseURL
		this.setProperty("baseURL", _baseURL);
		//Set the validate flag.
		this.setProperty("validate", (_validation? "true": "false"));
		//Make the builder.
		this.builder = new SAXBuilder();
		
		//Set up the validator?
		if ("true".equals(this.getProperty("validate"))) {
			//Prepare the DOM converter.
			this.dom_outputter = new DOMOutputter();
		    try {
		    	//Load the stylesheet.
		    	StreamSource stylesheet = new StreamSource(this.getClass().getClassLoader().getResourceAsStream("oai/OAI-PMH.xsd"));
		    	//Create the schema.
		    	Schema schema = new XMLSchemaFactory().newSchema(stylesheet);
		    	//Get a validator.
			    this.validator = schema.newValidator();
		    } catch (SAXException se) {
		    	//Initialization failed.  Log...
		    	this.m_logger.error(se);
		    	//...and complan.
		    	throw new JDOMException(se.getMessage());
		    }
		}
		
		//Initialize the Provider proxy.
		this.initialize();		
	}
	
	/**
	 * 'Identify's the remote provider and stores all relevent information as Properties.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	protected void initialize()
			throws JDOMException, IOException {
		this.identify();
		
		this.setProperty("repositoryName", this.verb.getChild("repositoryName", this.namespace).getValue());
		this.setProperty("protocolVersion", this.verb.getChild("protocolVersion", this.namespace).getValue());
		
		//Can be more than 1 adminEmail.  What to do?
		this.setProperty("adminEmail", this.verb.getChild("adminEmail", this.namespace).getValue());
		
		this.setProperty("earliestDatestamp", this.verb.getChild("earliestDatestamp", this.namespace).getValue());
		this.setProperty("deletedRecord", this.verb.getChild("deletedRecord", this.namespace).getValue());
		this.setProperty("granularity", this.verb.getChild("granularity", this.namespace).getValue());
		
		//Granularity format doesn't match Java's formatting syntax.
		String format = this.getProperty("granularity");
		format = format.replaceAll("Y", "y");
		format = format.replaceAll("D", "d");
		format = format.replaceAll("h", "H");
		format = format.replaceAll("T", "'T'");
		format = format.replaceAll("Z", "'Z'");
		//Store the proper syntax to format dates.
		this.setProperty("granularity.format", format);
		
		//Can be any number of compressions
		if (this.verb.getChild("compression", this.namespace) != null) {
			this.setProperty("compression", this.verb.getChild("compression", this.namespace).getValue());
		}
		
		//Store any descriptions.
		Iterator description_iterator = this.verb.getChildren("description", this.namespace).iterator();
		while (description_iterator.hasNext()) {
			Element description = (Element) description_iterator.next();
			Namespace description_namespace = description.getNamespace();
			if ("oai-identifier".equals(description.getName())) {
				this.setProperty("oai-scheme", description.getChild("scheme", description_namespace).getValue());
				this.setProperty("oai-repositoryIdentifier", description.getChild("repositoryIdentifier", description_namespace).getValue());
				this.setProperty("oai-delimiter", description.getChild("delimiter", description_namespace).getValue());
			}
		}
	}
	
	/**
	 * Asks the provider for a specific record.
	 * 
	 * @param _identifier The Identifier of the record.
	 * @param _metadataPrefix What record metadata to ask for.
	 * 
	 * @return The 'verb' Element of the response.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Element getRecord(String _identifier, String _metadataPrefix)
			throws JDOMException, IOException {
		StringBuffer parameters = new StringBuffer();
		parameters.append("&identifier=" + _identifier);
		parameters.append("&metadataPrefix=" + _metadataPrefix);
		this.sendRequest("GetRecord", parameters);
		return this.getVerb();
	}
	
	/**
	 * Asks the remote provider to Identify itself.
	 * 
	 * @return The 'verb' Element of the response.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Element identify()
			throws JDOMException, IOException {
		this.sendRequest("Identify");		
		return this.getVerb();
	}
	
	/**
	 * Asks the remote provider to continue listing identifiers if it's already begun to do so.
	 * 
	 * @return The 'verb' Element of the response or null if there is no resumptionToken.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Element listIdentifiers()
			throws JDOMException, IOException {
		if (this.more()) {
			this.listIdentifiers(this.getProperty("resumptionToken"));
			return this.getVerb();
		} else {
			return null;
		}
	}
	
	/**
	 * Asks the remote provider to continue listing identifiers using a specified resumptiontoken.
	 * 
	 * @param _resumptionToken The resumptionToken to use.
	 * 
	 * @return The 'verb' Element of the response.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Element listIdentifiers(String _resumptionToken)
			throws JDOMException, IOException {
		StringBuffer parameters = new StringBuffer();
		parameters.append("&resumptionToken=" + _resumptionToken);
		this.sendRequest("ListIdentifiers", parameters);
		return this.getVerb();
	}
	
	/**
	 * Asks the remote provider to start listing identifiers using specified parameters.
	 * 
	 * @param _from From when in time to start listing.
	 * @param _until Until when in time to stop listing.
	 * @param _metadataPrefix What metadata to list. (REQUIRED)
	 * @param _set From what set to list.
	 * 
	 * @return The 'verb' Element of the response.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Element listIdentifiers(String _from, String _until, String _metadataPrefix, String _set)
			throws JDOMException, IOException {
		StringBuffer parameters = new StringBuffer();
		parameters.append("&metadataPrefix=" + _metadataPrefix);
		if ((_from != null) && (_from.length() != 0)) {
			parameters.append("&from=" + _from);
		}
		if ((_until != null) && (_until.length() != 0)) {
			parameters.append("&until=" + _until);
		}
		if ((_set != null) && (_set.length() != 0)) {
			parameters.append("&set=" + _set);
		}
		this.sendRequest("ListIdentifiers", parameters);
		return this.getVerb();
	}
	
	/**
	 * Asks the remote provider to list all metadata formats it can provide.
	 * 
	 * @return The 'verb' Element of the response.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Element listMetadataFormats()
			throws JDOMException, IOException {
		this.sendRequest("ListMetadataFormats");
		return this.getVerb();
	}
	
	/**
	 * Asks the remote provider to list all metadata formats it can provide from a specific record.
	 * 
	 * @param _identifier The identifier of the record.
	 * 
	 * @return The 'verb' Element of the response.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Element listMetadataFormats(String _identifier)
			throws JDOMException, IOException {
		StringBuffer parameters = new StringBuffer();
		parameters.append("&identifier=" + _identifier);
		this.sendRequest("ListMetadataFormats", parameters);
		return this.getVerb();
	}
	
	/**
	 * Asks the remote provider to continue listing records if it's already begun to do so.
	 * 
	 * @return The 'verb' Element of the response or null if there is no resumptionToken.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Element listRecords()
			throws JDOMException, IOException {
		if (this.more()) {
			this.listRecords(this.getProperty("resumptionToken"));
			return this.getVerb();
		} else {
			return null;
		}
	}
	
	/**
	 * Asks the remote provider to continue listing records using a specified resumptiontoken.
	 * 
	 * @param _resumptionToken The resumptionToken to use.
	 * 
	 * @return The 'verb' Element of the response.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Element listRecords(String _resumptionToken)
			throws JDOMException, IOException {
		StringBuffer parameters = new StringBuffer();
		parameters.append("&resumptionToken=" + _resumptionToken);
		this.sendRequest("ListRecords", parameters);
		return this.getVerb();
	}
	
	/**
	 * Asks the remote provider to start listing records using specified parameters.
	 * 
	 * @param _from From when in time to start listing.
	 * @param _until Until when in time to stop listing.
	 * @param _metadataPrefix What metadata to list. (REQUIRED)
	 * @param _set From what set to list.
	 * 
	 * @return The 'verb' Element of the response.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Element listRecords(String _from, String _until, String _metadataPrefix, String _set)
			throws JDOMException, IOException {
		StringBuffer parameters = new StringBuffer();
		parameters.append("&metadataPrefix=" + _metadataPrefix);
		if ((_from != null) && (_from.length() != 0)) {
			parameters.append("&from=" + _from);
		}
		if ((_until != null) && (_until.length() != 0)) {
			parameters.append("&until=" + _until);
		}
		if ((_set != null) && (_set.length() != 0)) {
			parameters.append("&set=" + _set);
		}
		this.sendRequest("ListRecords", parameters);
		return this.getVerb();
	}
	
	/**
	 * Asks the remote provider to list all sets which it provides.
	 * 
	 * @return The 'verb' Element of the response.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Element listSets()
			throws JDOMException, IOException {
		if (this.more()) {
			this.listSets(this.getProperty("resumptionToken"));
		} else {
			this.sendRequest("ListSets");
		}
		return this.getVerb();
	}
	
	/**
	 * Asks the remote provider to continue listing all sets which it provides.
	 * 
	 * @param _resumptionToken The resumptionToken to use.
	 * 
	 * @return The 'verb' Element of the response.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	public Element listSets(String _resumptionToken)
			throws JDOMException, IOException {
		StringBuffer parameters = new StringBuffer();
		parameters.append("&resumptionToken=" + _resumptionToken);
		this.sendRequest("ListSets", parameters);
		return this.getVerb();
	}
	
	/**
	 * Forwards a no-parameter request with blank parameters to the 'real' sendRequest().
	 * 
	 * @param _verb The verb to send to the remote provider.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	protected void sendRequest(String _verb)
			throws JDOMException, IOException {
		this.sendRequest(_verb, new StringBuffer());
	}
	
	/**
	 * Sends off requests to the provider which it then processes.
	 * 
	 * @param _verb The verb to send to the remote provider.
	 * @param _parameters Contains any parameters.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	protected void sendRequest(String _verb, StringBuffer _parameters)
			throws JDOMException, IOException {
		//Start building the URL String.
		String url = this.getProperty("baseURL");
		//Check to see if we are delaing with a local file.
		if (this.getProperty("baseURL").startsWith("file:")) {
			//Strip anything non-alphanumeric from the parameters.
			String parameters = _parameters.toString().replaceAll("\\W", "").replaceAll("_", "");
			if (!this.getProperty("baseURL").endsWith(System.getProperty("file.separator"))) {
				url = url + System.getProperty("file.separator");
			}
			url = url + _verb + parameters + ".xml";
		} else {
			url = url + "?verb=" + _verb + _parameters.toString();
		}
		this.response = builder.build(url);
		this.processResponse(_verb);
	}
	
	/**
	 * Processes a request.
	 * 
	 * @param _verb The current verb in question.
	 * 
	 * @throws JDOMException If the response doesn't come back as a well-formed XML Document.
	 * @throws IOException If something goes wrong getting the response.
	 */
	protected void processResponse(String _verb)
			throws JDOMException, IOException {
		//Grab the namespace for further processing.
		this.namespace = this.response.getRootElement().getNamespace();
		
		//Should we validate?
		if ("true".equals(this.getProperty("validate"))) {
		    try {
		        this.validator.validate(new DOMSource(this.dom_outputter.output(this.response)));
		    } catch (IllegalArgumentException iae) {
		    	this.m_logger.error(iae);
		    	this.m_logger.debug(this.response);
		    	throw new JDOMException(iae.getMessage());
		    } catch (SAXException se) {
		    	this.m_logger.error("Validation of response failed.");
		    	this.m_logger.error(se);
		    	this.m_logger.debug(this.response);
		    	throw new JDOMException(se.getMessage());
		    }
		}
		
		//Store the important parts of the message.
		this.verb = this.response.getRootElement().getChild(_verb, namespace);
		this.error = this.response.getRootElement().getChild("error", namespace);
		this.setProperty("responseDate", this.response.getRootElement().getChild("responseDate", this.namespace).getValue());
		
		//Extract the error.
		if (this.error != null) {
			this.setProperty("error.code", this.error.getAttributeValue("code"));
			this.setProperty("error.message", this.error.getValue());
		} else {
			this.remove("error.code");
			this.remove("error.message");
		}
		
		//Extract the resumptionToken and any of its attributes.
		if ((this.verb != null) && (this.verb.getChild("resumptionToken", this.namespace) != null)) {
			Element resumptionToken = this.verb.getChild("resumptionToken", this.namespace);
			this.setProperty("resumptionToken", URLEncoder.encode(resumptionToken.getValue(), "UTF-8"));
			if (resumptionToken.getAttributeValue("expirationDate") != null) {
				this.setProperty("expirationDate", resumptionToken.getAttributeValue("expirationDate"));
			} else {
				this.remove("expirationDate");
			}
			if (resumptionToken.getAttributeValue("cursor") != null) {
				this.setProperty("cursor", resumptionToken.getAttributeValue("cursor"));
			} else {
				this.remove("cursor");
			}
		} else {
			this.remove("resumptionToken");
		}
	}
	
	/**
	 * Returns the response Document of the previous request.
	 * 
	 * @return The response Document of the previous request.
	 */
	public Document getResponse() {
		return this.response;
	}
	
	/**
	 * Returns a copy of the verb Element of the previous request.
	 * 
	 * @return A copy of the verb Element of the previous request.
	 */
	public Element getVerb() {
		return ((this.verb != null)? (Element) this.verb.clone(): null);
	}
	
	/**
	 * Returns the Namespace of the response Document.
	 * 
	 * @return The Namespace of the response Document.
	 */
	public Namespace getNamespace() {
		return this.namespace;
	}
	
	/**
	 * Returns an error state: true of false.
	 * 
	 * @return true if there is an error, false if not.
	 */
	public boolean hasError() {
		return (this.error != null);
	}
	
	/**
	 * Returns the current error code.
	 * 
	 * @return The current error code.
	 */
	public String getErrorCode() {
		return this.getProperty("error.code");
	}
	
	/**
	 * Returns the current error message.
	 * 
	 * @return The current error message.
	 */
	public String getErrorMessage() {
		return this.getProperty("error.message");
	}
	
	/**
	 * Returns the current resumptionToken.
	 * 
	 * @return The current resumptionToken.
	 */
	public String getResumptionToken() {
		return this.getProperty("resumptionToken");
	}
	
	/**
	 * Returns the existance of more responses available from the remote provider.
	 * 
	 * @return Whether or not the resumptionToken is not null and empty
	 */
	public boolean more() {
		return ((this.getProperty("resumptionToken") != null) && (this.getProperty("resumptionToken").length() != 0));
	}
	
	/**
	 * Returns a date as a String which conforms to the Provider's granularity.
	 * 
	 * @param date_string The date String to be formatted.
	 * 
	 * @return A properly formatted date String.
	 */
	public String format_date(String date_string)
			throws ParseException {
		if (date_string != null) {
			SimpleDateFormat date_format = null;
			if (date_string.endsWith("Z")) {
				date_format = new SimpleDateFormat(FINE_GRANULARITY);
			} else {
				date_format = new SimpleDateFormat(BASE_GRANULARITY);
			}
			Date parsed_date = date_format.parse(date_string);
			return new SimpleDateFormat(this.getProperty("granularity.format")).format(parsed_date);
		} else {
			return null;
		}
	}
}