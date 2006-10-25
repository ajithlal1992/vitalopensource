package com.vtls.opensource.oai;

import com.vtls.opensource.oai.Harvester;
import com.vtls.opensource.oai.Provider;
import com.vtls.opensource.oai.handlers.Handler;
import com.vtls.opensource.oai.handlers.DefaultHandler;
import com.vtls.opensource.utilities.XMLUtilities;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.InputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;

import java.util.Date;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Iterator;
import java.util.Enumeration;

import org.jdom.JDOMException;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

/**
 * An OAI Harvester designed to interact with Fedora and Virtua via the VORTEX Provider.
 * It acts as a transport for MARC records between Fedora and Virtua via the VORTEX Provider.
 * It can also harvest from external sites and load the metadata into Fedora.
 * 
 * @author Chris Hall
 * @verion 1.0
 */
public class HarvesterApplication {
	/**
	 * The logger.
	 */
	private static final Logger m_logger = Log4JLogger.getLogger(HarvesterApplication.class);
	
	/**
	 * The default filename for the properties file.
	 */
	private static final String DEFAULT_PROPERTIES_FILENAME = "harvester.properties";

	/**
	 * Parses the commandline and initializes the Provider, Handler, and Harvester.  Then runs the harvest.
	 * 
	 * @param args The commandline arguments.
	 */
	public static void main(String[] args) {
		try {
			String properties_filename = DEFAULT_PROPERTIES_FILENAME;
			if (args.length == 1) {
				properties_filename = args[0];
			} else if (args.length > 1) {
				//Too many parameters.
				System.out.println("Too many parameters!\n");
				properties_filename = "help";
			}
			
			if (properties_filename.equals("help")) {
				System.out.println("To run with the default configuration file, [harvester.properties], use:\n\tsh harvester.sh");
				System.out.println("To run with a specified configuration file, use:\n\tsh harvester.sh filename");
				return;
			}

    		//Load the properties file.
			Properties properties = loadProperties(properties_filename);
			
            //Get all the properties for the harvest.
			String baseURL = getRequiredProperty(properties, "provider.baseURL");
			String metadataPrefix = getRequiredProperty(properties, "provider.metadataPrefix");
			String setSpec = properties.getProperty("provider.setSpec");
			String from = properties.getProperty("provider.from");
			String until = properties.getProperty("provider.until");
			
			//Create the provider.
			Provider provider = new Provider(baseURL);
			
			//Check for a previous harvest.
			String previous = properties.getProperty("harvest.previous");			
			if (previous != null) {
				//Reset the from and until.
				from = previous;
				until = null;
			}
			
			//Format the dates.
			from = provider.format_date(from);
			until = provider.format_date(until);
			
			//Get the handler.
			Handler handler = getHandler(properties);
			
			//Create the harvester.
			Harvester harvester = new Harvester(handler, provider);
			
			boolean success = false;
			//Harvest.
			if (harvester.harvest(from,	until, metadataPrefix, setSpec)) {
				//Havest succeeded.
				success = true;
			} else {
				//Harvest failed.  Figure out why.
				String errorCode = provider.getErrorCode();
				if ("badArgument".equals(errorCode)) {
					//The request includes illegal arguments, is missing required arguments, includes a repeated argument, or values for arguments have an illegal syntax.
				} else if ("badResumptionToken".equals(errorCode)) {
					//The value of the resumptionToken argument is invalid or expired.
				} else if ("badVerb".equals(errorCode)) {
					//Value of the verb argument is not a legal OAI-PMH verb, the verb argument is missing, or the verb argument is repeated.
				} else if ("cannotDisseminateFormat".equals(errorCode)) {
					//The metadata format identified by the value given for the metadataPrefix argument is not supported by the item or by the repository.
				} else if ("idDoesNotExist".equals(errorCode)) {
					//The value of the identifier argument is unknown or illegal in this repository.
				} else if ("noMetadataFormats".equals(errorCode)) {
					//There are no metadata formats available for the specified item.
				} else if ("noRecordsMatch".equals(errorCode)) {
					//The combination of the values of the from, until, set and metadataPrefix arguments results in an empty list.
					System.out.println("No records to harvest.");
					success = true;
				} else if ("noSetHierarchy".equals(errorCode)) {
					//The repository does not support sets.
				}
			}
			
			if (success) {
				//Record this harvest.
				String responseDate = provider.getProperty("responseDate");
//				String last_datestamp = handler.getProperty("last.datestamp");
				
				//Generate GMT.
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				format.setTimeZone(new SimpleTimeZone(0, "GMT"));
				String gmt = format.format(new Date());
				
				//Replace the date and hour with the current GMT date and hour.
				if (gmt.substring(0, 11).equals(responseDate.substring(0, 11))) {
					responseDate = gmt.substring(0, 13) + responseDate.substring(13); 
				} else {
//					if (handler instanceof Fedora2VirtuaHandler) {
//						//Hack to fix Fedora's OAI Provider clock issue.
//						responseDate = last_datestamp;
//					} else {
						responseDate = gmt;
//					}
				}
				
				//Save the responseDate.
				recordProperty(properties_filename, "harvest.previous", responseDate);
				System.out.println("Finished.");
			} else {
				//Log error.
				m_logger.error("Error: " + provider.getErrorCode() + "-" + provider.getErrorMessage());
			}
		} catch (IOException ioe) {
			m_logger.error(ioe);
			return;
		} catch (JDOMException jde) {
			m_logger.error(jde);
			return;
		} catch (NumberFormatException nfe) {
			m_logger.error(nfe);
			return;
		} catch (NoSuchFieldException nsfe) {
			m_logger.error(nsfe);
			return;
		} catch (Exception e) {
			m_logger.error(e);
			StringWriter sw = new StringWriter();
	        PrintWriter pw = new PrintWriter(sw, true);
	        e.printStackTrace(pw);
	        pw.flush();
	        sw.flush();
	        System.out.println(sw.toString());
			return;
		}
	}
	
	/**
	 * Initializes and returns a Handler object.
	 * 
	 * @param target_config The XML config Document for the Handler.
	 * @param type What type of harvest are we to perform.
	 * 
	 * @return An intialized Handler object.
	 */
	public static Handler getHandler(Properties properties)
			throws FileNotFoundException, Exception {		
		//Make sure the directories exist.
		String output_filename = getRequiredProperty(properties, "output.filename");
		if (output_filename.contains(System.getProperty("file.separator"))) {
			new File(output_filename.substring(0, output_filename.lastIndexOf(System.getProperty("file.separator")))).mkdirs();
		}
		//Initialize the output stream.
		OutputStream output = new FileOutputStream(output_filename);
		
		//Try and get/return the handler.
		try {
			//Get the appropriate class from our handler name.
			Class instance = Class.forName(getRequiredProperty(properties, "handler.class"));
			//Create a new instance of it.
			DefaultHandler handler = (DefaultHandler) instance.newInstance();
			//Initialize it.
			handler.initialize(properties, output);
			//Return it.
			return handler;
		} catch (InstantiationException ie) {
			m_logger.error(ie);
			throw new RuntimeException(ie);
		} catch (IllegalAccessException iae) {
			m_logger.error(iae);
			throw new RuntimeException(iae);
		} catch (ClassNotFoundException cnfe) {
			m_logger.error(cnfe);
			throw new RuntimeException(cnfe);
		}
	}
	
	private static Properties loadProperties(String filename)
	throws IOException {
		System.out.println("Loading configuration file [" + filename + "]...");
        InputStream properties_stream = new FileInputStream(filename);
        if (properties_stream == null) {
            throw new IOException("Error loading configuration file [" + filename + "].");
        }
        Properties properties = new Properties();
        properties.load(properties_stream);
        properties_stream.close();
        Enumeration properties_enum = properties.propertyNames();
        while (properties_enum.hasMoreElements()) {
        	String property = (String) properties_enum.nextElement();
        	String value = properties.getProperty(property).trim();
        	if ("".equals(value)) {
        		properties.remove(property);
        	} else {
        		properties.setProperty(property, value);
        	}
        }
        return properties;
	}
	
	/**
	 * Makes sure a property exists and is not blank before returning it.
	 * 
	 * @param properties The properties loaded from file.
	 * @param property The name of the property to return.
	 * 
	 * @return The value of the property.
	 * 
	 * @throws Exception If the property is null or empty.
	 */
	private static String getRequiredProperty(Properties properties, String property)
			throws Exception {
		String value = properties.getProperty(property);
		if (value == null) {
			throw new Exception("Missing required property: " + property);
		}
		return value.trim();
	}
	
	/**
	 * Finds and changes, or adds, a property with a value to the properties file.
	 * 
	 * @param filename The name of the properties file.
	 * @param property The name of the property.
	 * @param value The value of the property.
	 * 
	 * @throws IOException If something goes wrong when accessing the file.
	 */
	private static void recordProperty(String filename, String property, String value)
			throws IOException {
		boolean found = false;
		List lines = new LinkedList();
		
		File file = new File(filename);
		FileReader reader = new FileReader(file);
		BufferedReader buffered_reader = new BufferedReader(reader);

		String line = null;
		while ((line = buffered_reader.readLine()) != null) {
			if (line.startsWith(property)) {
				line = property + " = " + value;
				found = true;
			}
			lines.add(line);
		}
		buffered_reader.close();
		reader.close();
		
		if (!found) {
			lines.add("\n\n# Could not find property in file.  Adding here:\n#\n");
			lines.add(property + " = " + value);
		}
		
//		file.delete();
		
		file = new File(filename);
		FileWriter writer = new FileWriter(file, false);
		BufferedWriter buffered_writer = new BufferedWriter(writer);
		
		Iterator line_iterator = lines.iterator();
		while (line_iterator.hasNext()) {
			buffered_writer.write((String) line_iterator.next());
			buffered_writer.newLine();
		}
		
		buffered_writer.close();
		writer.close();
	}
}
