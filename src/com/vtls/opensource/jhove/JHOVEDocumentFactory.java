package com.vtls.opensource.jhove;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import edu.harvard.hul.ois.jhove.App;
import edu.harvard.hul.ois.jhove.ErrorMessage;
import edu.harvard.hul.ois.jhove.JhoveBase;
import edu.harvard.hul.ois.jhove.JhoveException;
import edu.harvard.hul.ois.jhove.Module;
import edu.harvard.hul.ois.jhove.OutputHandler;
import edu.harvard.hul.ois.jhove.RepInfo;
import edu.harvard.hul.ois.jhove.handler.XmlHandler;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * This class manufactures JHOVE Document objects.
 * Implemented as a singleton.
 */
public class JHOVEDocumentFactory
{
	// Log4j instance.
	private static final Logger m_logger = Log4JLogger.getLogger(JHOVEDocumentFactory.class);

	// Manage this class as a singleton.
	private static JHOVEDocumentFactory m_instance = null;

	private List m_module_list = null;

	// JHOVE API
	private JhoveBase m_jhove = null;
	private App m_application = null;

	// JHOVE Application Options
	private static final String ApplicationName = "Jhove";
	private static final int [] ApplicationDate = {2006, 6, 2};
	private static final String Release = "1.1";
	private static final String Usage = "";
	private static final String Rights = "Copyright 2004-2006 by the President " +
	"and Fellows of Harvard College. Released under the GNU Lesser General " + 
	"Public License.";

	private static final String[] Modules = {
		"edu.harvard.hul.ois.jhove.module.AiffModule",
		"edu.harvard.hul.ois.jhove.module.WaveModule",
		"edu.harvard.hul.ois.jhove.module.PdfModule",
		"edu.harvard.hul.ois.jhove.module.Jpeg2000Module",
		"edu.harvard.hul.ois.jhove.module.JpegModule",
		"edu.harvard.hul.ois.jhove.module.GifModule",
		"edu.harvard.hul.ois.jhove.module.TiffModule",
		"edu.harvard.hul.ois.jhove.module.XmlModule",
		"edu.harvard.hul.ois.jhove.module.HtmlModule",
		"edu.harvard.hul.ois.jhove.module.AsciiModule",
	"edu.harvard.hul.ois.jhove.module.Utf8Module"};

	// Removed:  "edu.harvard.hul.ois.jhove.module.BytestreamModule" 

	private static final String EXCEPTION_JAVA_VERSION = "Java 1.4.x is required for the JHOVE API.";

	/**
	 * Private constructor.  Initializes the logger and loads modules.
	 */
	private JHOVEDocumentFactory()
	{
		String version = System.getProperty("java.vm.version");
		if(version.compareTo("1.4.0") < 0)
			throw new RuntimeException(EXCEPTION_JAVA_VERSION);

		m_module_list = new LinkedList();

		// Set the log level for the JHOVE package.
		java.util.logging.Logger.getLogger("edu.harvard.hul.ois.jhove").setLevel(java.util.logging.Level.SEVERE);

		// Set up a placeholder JhoveBase instance.
		App m_application = new App(ApplicationName, Release, ApplicationDate, Usage, Rights);
		try
		{
			// Create base JHOVE object, We won't actually use this class but a
			// quirk of the architecture requires that it be passed to the Modules.
			m_jhove = new JhoveBase();
			m_jhove.setLogLevel("SEVERE");
			m_jhove.setChecksumFlag(false);
			m_jhove.setShowRawFlag(false);
			m_jhove.setSignatureFlag(false);
		}
		catch(JhoveException e)
		{
			throw new RuntimeException(e);
		}

		// Initialize the collection of Modules.
		for(int i = 0; i < Modules.length; i++)
		{
			try
			{
				Class module_class = Class.forName(Modules[i]);
				Module module = (Module)module_class.newInstance();
				module.init(null);
				module.setDefaultParams(new LinkedList());
				m_module_list.add(module);
			}
			catch(ClassNotFoundException e)
			{
				m_logger.error("Cannot initialize module: " + Modules[i], e);
			}
			catch(InstantiationException e)
			{
				m_logger.error("Cannot initialize module: " + Modules[i], e);
			}
			catch(Exception e)
			{
				m_logger.error(e);
			}
		}
	}

	/**
	 * Get a singleton instance of this class.
	 * @return the singleton instance of the JHOVEDocumentFactory 
	 */
	public static JHOVEDocumentFactory getInstance()
	{
		if(m_instance == null)
		{
			m_instance = new JHOVEDocumentFactory();
		}
		return m_instance;
	}

	/**
	 * Get a JHOVE Document from a resource string
	 * @param _resource  a resource string
	 * @return Document  a JDOM Document
	 * @throws IOException
	 * @throws JDOMException
	 */
	public Document getDocument(String _resource) throws IOException, JDOMException
	{
		SAXBuilder builder = new SAXBuilder();

		// Create String array for Jhove.
		String[] resources = { _resource };

		// Create a PrintWriter we can extract a String from.
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(bytes, true);

		OutputHandler output_handler = m_jhove.getHandler("xml");
		output_handler.setWriter(writer);

		try
		{
			// Dispatch the JhoveBase object with a new application context.
			m_jhove.dispatch(new App(ApplicationName, Release, ApplicationDate, Usage, Rights),
					m_jhove.getModule(null), m_jhove.getHandler(null), output_handler,
					null, resources);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}


		ByteArrayInputStream stream = new ByteArrayInputStream(bytes.toByteArray());
		Document _return = builder.build(stream);

		writer.close();
		bytes.close();

		return _return;
	}

	/**
	 * Get a JHOVE Document from a URL source
	 * @param _url  a resource URL
	 * @return a JDOM Document
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public Document getDocument(URL _url) throws IOException, JDOMException
	{
		// Create an HttpClient.
		HttpClient client = new HttpClient();

		// Issue a GET HTTP method.
		HttpMethod method = new GetMethod(_url.toExternalForm());
		int status_code = client.executeMethod(method);

		if(status_code != 200)
		{
			RepInfo representation = new RepInfo(_url.toExternalForm());
			representation.setMessage(new ErrorMessage("Status code returned [" + String.valueOf(status_code) + "]."));
			representation.setWellFormed(RepInfo.FALSE);
			return getDocumentFromRepresentation(representation);
		}

		byte[] response = method.getResponseBody();
		return this.getDocument(new ByteArrayInputStream(response), _url.toExternalForm());
	}	

	/**
	 * Get a JHOVE Document from a {@link URL} source
	 * @param _uri  a resource URL
	 * @param _stream an input stream code
	 * @return a JDOM Document
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public Document getDocument(InputStream _stream, String _uri) throws IOException, JDOMException
	{
		RepInfo representation = new RepInfo(_uri);

		File file = File.createTempFile("vtls-jhove-", "");
		file.deleteOnExit();

		BufferedOutputStream output_stream = new BufferedOutputStream(new FileOutputStream(file));
		BufferedInputStream input_stream = new BufferedInputStream(_stream);

		int stream_byte;
		while ((stream_byte = input_stream.read()) != -1)
		{
			output_stream.write(stream_byte);
		}

		output_stream.flush();
		output_stream.close();
		input_stream.close();

		representation.setSize(file.length());
		representation.setLastModified(new Date());
		populateRepresentation(representation, file);

		file.delete();

		return getDocumentFromRepresentation(representation);
	}	

	/**
	 * Get a JHOVE Document from a {@link File}
	 * @param file  a resource {@link File}
	 * @return a JDOM Document
	 * @throws IOException 
	 * @throws JDOMException 
	 */
	public Document getDocument(File file) throws IOException, JDOMException
	{
		// Create the RepInfo instance.
		RepInfo representation = new RepInfo(file.toString());
		if(!file.exists())
		{
			representation.setMessage(new ErrorMessage("Content not found."));
			representation.setWellFormed(RepInfo.FALSE);
		}
		else if(!file.isFile() || !file.canRead()) 
		{
			representation.setMessage(new ErrorMessage("Content cannot be read."));
			representation.setWellFormed(RepInfo.FALSE);
		}
		else
		{
			representation.setSize(file.length());
			representation.setLastModified(new Date(file.lastModified()));
			populateRepresentation(representation, file);
		}
		return getDocumentFromRepresentation(representation);
	}

	///////////////////////////////////////////////////////////////////////////
	// Private Methods ////////////////////////////////////////////////////////

	/**
	 * Populate the representation information for JHOVE Document from a specific File.
	 * @param representation a RepInfo 
	 * @param file  a File containing the representation information
	 * @throws IOException
	 */
	private void populateRepresentation(RepInfo representation, File file) throws IOException
	{
		// Iterate through the modules and process.
		Iterator iterator = m_module_list.iterator();
		while(iterator.hasNext())
		{
			Module module = (Module)iterator.next();
			module.setBase(m_jhove);
			module.setVerbosity(Module.MINIMUM_VERBOSITY);

			// m_logger.info(module.toString());
			RepInfo persistent = (RepInfo)representation.clone();

			if(module.hasFeature("edu.harvard.hul.ois.jhove.canValidate"))
			{
				try
				{
					module.applyDefaultParams();
					if(module.isRandomAccess())
					{
						RandomAccessFile ra_file = new RandomAccessFile(file, "r");
						module.parse(ra_file, persistent);
						ra_file.close();
					}
					else
					{
						InputStream stream = new FileInputStream(file);
						int parse = module.parse(stream, persistent, 0);
						while(parse != 0)
						{
							stream.close();
							stream = new FileInputStream(file);
							parse = module.parse(stream, persistent, parse);
						}
						stream.close();
					}

					if(persistent.getWellFormed() == RepInfo.TRUE)
						representation.copy(persistent);
					else
						representation.setSigMatch(persistent.getSigMatch());
				}
				catch(Exception e)
				{
					continue;
				}
			}
		}
	}

	/**
	 * Get a JHOVE Document from a representation information.
	 * @param _representation a representation information for a JHOVE Document
	 * @return a JHOVE Document
	 * @throws IOException
	 * @throws JDOMException
	 */
	private Document getDocumentFromRepresentation(RepInfo _representation) throws IOException, JDOMException
	{
		// Create a PrintWriter we can extract a String from.
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(bytes, true);

		// Get a JHOVE XML OutputHandler we can use to handle the output.
		OutputHandler output_handler = getOutputHandler();
		output_handler.setWriter(writer);

		// Process the RepInfo instance with JHOVE's OutputHandler (in our case,
		// the XmlHandler).
		output_handler.setApp(new App(ApplicationName, Release, ApplicationDate, Usage, Rights));
		output_handler.setBase(m_jhove);

		// Header, Content, Footer.
		output_handler.showHeader();
		_representation.show(output_handler);
		output_handler.showFooter();

		// Create an InputStream from the contents of the XML handler and
		// return a JDOM Document.
		ByteArrayInputStream stream = new ByteArrayInputStream(bytes.toByteArray());

		SAXBuilder builder = new SAXBuilder();
		Document _return = builder.build(stream);

		writer.close();
		bytes.close();

		return _return;
	}
	/**
	 * Get a JHOVE XML OutputHandler to handle the output.
	 * @return an OutputHandler 
	 */
	private OutputHandler getOutputHandler()
	{
		OutputHandler _return = new XmlHandler();
		try
		{
			_return.setDefaultParams(new java.util.ArrayList());
			_return.setApp(m_application);
			_return.setBase(m_jhove);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return _return;
	}
}
