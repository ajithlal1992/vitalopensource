package com.vtls.opensource.utilities;

import com.vtls.opensource.xml.URIResolver;

import com.vtls.opensource.logging.Log4JLogger;
import com.vtls.opensource.utilities.TinyJDOMCreationHandler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.jdom.input.SAXBuilder;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.output.DOMOutputter;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.xpath.XPath;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * A collection of utility functions for the XML JDOM Document. 
 */
public class XMLUtilities
{
	// Log4j instance.
	private static final Logger m_logger = Log4JLogger.getLogger(XMLUtilities.class);

	/**
	 * Builds a JDOM {@link Document} from a pre-existing DOM org.w3c.dom.Document.
	 * @param _document  A pre-existing DOM org.w3c.dom.Document.
	 * @return  A JDOM {@link Document}
	 */
	public static Document getDocument(org.w3c.dom.Document _document)
	{
		DOMBuilder builder = new DOMBuilder();
		return builder.build(_document);
	}

	/**
	 * Get a JDOM {@link Document} from a XML format String.
	 * @param _xml  A XML format String containing the document content
	 * @return A JDOM {@link Document}
	 * @throws IOException  If an input or output error occurs
	 * @throws JDOMException  If a JDOM error occurs  
	 */
	public static Document getDocument(String _xml) throws JDOMException, IOException
	{
		return getDocument(StreamUtilities.getInputStreamFromString(_xml));
	}

	/**
	 * Get a JDOM {@link Document} from an {@link InputStream}.
	 * @param _stream  An {@link InputStream} containing the document content
	 * @return A JDOM {@link Document}
	 * @throws IOException  If an input or output error occurs
	 * @throws JDOMException  If a JDOM error occurs  
	 */
	public static Document getDocument(InputStream _stream) throws JDOMException, IOException
	{
		SAXBuilder builder = new SAXBuilder();
		builder.setEntityResolver(new NullEntityResolver());
		return builder.build(_stream);
	}

	/**
	 * Get a size-limited and node-limited JDOM {@link Document} 
	 * from an {@link InputStream}.
	 * @param _stream  An {@link InputStream} containing the document content
	 * @return A JDOM {@link Document}
	 * @throws IOException  If an input or output error occurs
	 * @throws JDOMException  If a JDOM error occurs  
	 * @throws SAXException If a SAX error or warning occurs
	 */
	public static Document getSimpleDocument(InputStream _stream) throws JDOMException, IOException, SAXException
	{
		TinyJDOMCreationHandler handler = new TinyJDOMCreationHandler();

		SAXParserFactory sax_parser_factory = SAXParserFactory.newInstance();
		sax_parser_factory.setValidating(false);

		try
		{
			// Set extended features.
			sax_parser_factory.setFeature("http://xml.org/sax/features/validation", false);

			// Get the entire contents of the datastream (via an InputStream
			// from the URL) and SAX parse it with our custom handler.
			SAXParser sax_parser = sax_parser_factory.newSAXParser();

			// Create an InputSource and parse.
			InputSource input_source = new InputSource(_stream);
			sax_parser.parse(input_source, handler);

			// Get the Document.
			return handler.getDocument();
		}
		catch(javax.xml.parsers.ParserConfigurationException e)
		{
			m_logger.error(e);
		}

		return null;
	}

	/**
	 * Get a JDOM {@link Document} from a specific resource.
	 * @param _resource_name  The name of the resource
	 * @return A JDOM {@link Document}
	 * @throws IOException  If an input or output error occurs
	 * @throws JDOMException  If a JDOM error occurs  
	 */
	public static Document getDocumentFromResource(String _resource_name) throws JDOMException, IOException
	{
		ClassLoader class_loader = XMLUtilities.class.getClassLoader();
		InputSource source = new InputSource(class_loader.getResourceAsStream(_resource_name));

		SAXBuilder builder = new SAXBuilder("org.apache.xerces.parsers.SAXParser");
		return builder.build(source);
	}

	/**
	 * Get an UTF-8 encoded {@link InputStream} from a JDOM {@link Document}
	 * @param _document  A specific JDOM {@link Document}
	 * @return An UTF8 encoded {@link InputStream} of the Document content
	 */
	public static InputStream getInputStream(Document _document)
	{
		InputStream _return = null;

		try
		{
			byte[] bytes = XMLUtilities.toString(_document).getBytes("UTF-8");
			_return = new ByteArrayInputStream(bytes);
		}
		catch(Exception e)
		{
		}
		finally
		{
			return _return;
		}
	}

	/**
	 * Get an UTF-8 encoded {@link InputStream} from a JDOM {@link Element}
	 * @param _element  A specific JDOM {@link Element}
	 * @return An UTF8 encoded {@link InputStream} representing the Element
	 */
	public static InputStream getInputStream(Element _element)
	{
		Document document = new Document((Element)_element.clone());
		return XMLUtilities.getInputStream(document);
	}

	/**
	 * Get a UTF-8 encoded string representing a JDOM {@link Document}.
	 * @param _document A specific JDOM {@link Document}
	 * @return A UTF8 encoded string
	 */
	public static String toString(Document _document)
	{
		StringWriter _return = new StringWriter();
		try
		{
			Format format = Format.getPrettyFormat();
			format.setEncoding("UTF-8");
			XMLOutputter outputter = new XMLOutputter(format);
			outputter.output(_document, _return);
		}
		catch(IOException e)
		{
			m_logger.error(e);
		}
		return _return.toString();
	}

	/**
	 * Get a UTF8 encoded string representing a JDOM {@link Element}.
	 * @param _element A specific JDOM {@link Element}
	 * @return A UTF-8 encoded string
	 */
	public static String toString(Element _element)
	{
		Document document = new Document((Element)_element.clone());
		return XMLUtilities.toString(document);
	}

	/**
	 * Save a JDOM {@link Document} content in UTF-8 encoded {@link File}.  Shortcut to full save with UTF-8 encoding.
	 * @param _document A specific JDOM {@link Document}
	 * @param _file A {@link File} object the Document saves to 
	 * @throws IOException If input or output error occurs
	 */
	public static void save(Document _document, File _file) throws IOException
	{
		XMLUtilities.save(_document, _file, "UTF-8");
	}

	/**
	 * Save a JDOM {@link Document} content in {@link File} using specified encoding.
	 * @param _document A specific JDOM {@link Document}
	 * @param _file A {@link File} object the Document saves to 
	 * @param _encoding The encoding of the file.
	 * @throws IOException If input or output error occurs
	 */
	public static void save(Document _document, File _file, String _encoding) throws IOException
	{
		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(_file), _encoding);
		Format format = Format.getPrettyFormat();
		format.setEncoding(_encoding);
		XMLOutputter outputter = new XMLOutputter(format);
		outputter.output(_document, writer);
		writer.close();
	}

	/**
	 * Get a org.w3c.dom.Document from a JDOM {@link Document}.
	 * @param _document  A specific JDOM {@link Document}
	 * @throws JDOMException  If a JDOM error occurs
	 */
	public static org.w3c.dom.Document getDOMDocument(Document _document) throws JDOMException
	{
		DOMOutputter outputter = new DOMOutputter();
		return outputter.output(_document);
	}

	/**
	 * Transform the content of a JDOM {@link Document} to a xsl-specified 
	 * {@link StringBuffer} with an {@link ErrorListener} for the transformation
	 * @param _document  A specific JDOM {@link Document}
	 * @param _stream  An {@link InputStream} representing the XSL
	 * @param _listener  An {@link ErrorListener} of the during transformation
	 * @return  A {@link StringBuffer} representing the JDOM Document
	 * @throws JDOMException  If a JDOM error occurs 
	 */
	public static StringBuffer transform(Document _document, InputStream _stream, ErrorListener _listener) throws JDOMException, IOException, TransformerException, TransformerConfigurationException
	{
		return transform(_document, _stream, _listener, null);
	}

	/**
	 * Transform the content of a JDOM {@link Document} to a xsl-specified 
	 * {@link StringBuffer} with an {@link ErrorListener} for the transformation
	 * @param _document  A specific JDOM {@link Document}
	 * @param _stream  An {@link InputStream} representing the XSL
	 * @param _listener  An {@link ErrorListener} of the during transformation
	 * @param _resolver  A {@link URIResolver} to handle includes.
	 * @return  A {@link StringBuffer} representing the JDOM Document
	 * @throws JDOMException  If a JDOM error occurs 
	 */
	public static StringBuffer transform(Document _document, InputStream _stream, ErrorListener _listener, URIResolver _resolver) throws JDOMException, IOException, TransformerException, TransformerConfigurationException
	{
		TransformerFactory factory = TransformerFactory.newInstance();
		if(_listener != null)
		{
			factory.setErrorListener(_listener);
		}

		if(_resolver != null)
		{
			factory.setURIResolver(_resolver);
		}

		org.w3c.dom.Document dom_document = XMLUtilities.getDOMDocument(_document);

		Source source = new javax.xml.transform.dom.DOMSource(dom_document);
		StreamSource xsl_source = new StreamSource(_stream);

		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);

		Transformer transformer = factory.newTransformer(xsl_source);
		transformer.transform(source, result);
		return writer.getBuffer();
	}

	/**
	 * Transform the content of a JDOM {@link Document} to a xsl-specified 
	 * {@link StringBuffer}
	 * @param _document  A specific JDOM {@link Document}
	 * @param _stream  An {@link InputStream} representing the XSL
	 * @return  A {@link StringBuffer} representing the JDOM Document
	 * @throws JDOMException  If a JDOM error occurs 
	 */  
	public static StringBuffer transform(Document _document, InputStream _stream) throws JDOMException, IOException, TransformerException, TransformerConfigurationException
	{
		return XMLUtilities.transform(_document, _stream, null, null);
	}

	/**
	 * Transform the content of a JDOM {@link Document} to a xsl-specified 
	 * {@link StringBuffer}
	 * @param _document  A specific JDOM {@link Document}
	 * @param _stream  An {@link InputStream} representing the XSL
	 * @param _resolver  A {@link URIResolver} to handle includes.
	 * @return  A {@link StringBuffer} representing the JDOM Document
	 * @throws JDOMException  If a JDOM error occurs 
	 */  
	public static StringBuffer transform(Document _document, InputStream _stream, URIResolver _resolver) throws JDOMException, IOException, TransformerException, TransformerConfigurationException
	{
		return XMLUtilities.transform(_document, _stream, null, _resolver);
	}

	/**
	 * Get a {@link List} of values by a wrapped Xpath
	 * @param _context  the node to use as context for evaluating the XPath expression.
	 * @param _xpath  A string of a specific XPath expression
	 * @return  A {@link List} of values of the wrapped xpath
	 * @throws JDOMException  If a JDOM error occurs 
	 */
	public static List getValuesByXPath(java.lang.Object _context, String _xpath) throws JDOMException
	{
		List _return = new LinkedList();

		List nodes = XPath.selectNodes(_context, _xpath);
		Iterator iterator = nodes.iterator();
		while(iterator.hasNext())
		{
			Object element = iterator.next();
			if(element instanceof Element)
			{
				_return.add(((Element)element).getText());
			}
			else if(element instanceof Attribute)
			{
				_return.add(((Attribute)element).getValue());
			}
		}
		return _return;
	}

	/**
	 * Evaluates an XPath expression and returns the {@link List} of selected nodes.
	 * @param _context  the node to use as context for evaluating the XPath expression.
	 * @param _xpath  A string of a specific XPath expression
	 * @return  A {@link List} of selected nodes
	 * @throws JDOMException  If a JDOM error occurs 
	 */
	public static List getNodesByXPath(java.lang.Object _context, String _xpath) throws JDOMException
	{
		return XPath.selectNodes(_context, _xpath);
	}

	/**
	 * Evaluates the wrapped XPath expression and returns the first entry in the 
	 * list of selected nodes. 
	 * @param _context  the node to use as context for evaluating the XPath expression.
	 * @param _xpath  A string of a specific XPath expression
	 * @return  An Object of the single selected node
	 * @throws JDOMException  If a JDOM error occurs 
	 */
	public static java.lang.Object getNodeByXPath(java.lang.Object _context, String _xpath) throws JDOMException
	{
		return XPath.selectSingleNode(_context, _xpath);
	}


	/**
	 * Evaluates the wrapped XPath expression and returns the value of the 
	 * first selected node.
	 * @param _context  the node to use as context for evaluating the XPath expression.
	 * @param _xpath  A string of a specific XPath expression
	 * @return  An String of the value
	 */
	public static String getValueByXPath(java.lang.Object _context, String _xpath)
	{
		try
		{
			Object element = XMLUtilities.getNodeByXPath(_context, _xpath);
			if(element == null)
			{
				return null;
			}

			if(element instanceof Element)
			{
				return ((Element)element).getText();
			}
			else if(element instanceof Attribute)
			{
				return ((Attribute)element).getValue();
			}
		}
		catch(JDOMException e)
		{
			m_logger.error(e);
		}
		return null;
	}


	/**
	 * Set a specific value to an entry represented by a wrapped XPath 
	 * expression. 
	 * @param _context  The node to use as context for evaluating the
	 *                  XPath expression.
	 * @param _xpath  A string of a specific XPath expression
	 * @param _value  A specific value to be set to the selected entry
	 */   
	public static void setValueByXPath(java.lang.Object _context, String _xpath, String _value)
	{
		try
		{
			Object element = XMLUtilities.getNodeByXPath(_context, _xpath);
			if(element == null)
			{
				return;
			}

			if(element instanceof Element)
			{
				((Element)element).setText(_value);
			}
			else if(element instanceof Attribute)
			{
				((Attribute)element).setValue(_value);
			}
		}
		catch(JDOMException e)
		{
			m_logger.error(e);
		}
	}

	/**
	 * Get the URI of the namespace for a JDOM {@link Document}
	 * @param _document A specific JDOM {@link Document}
	 * @return  A String representing the namespace URI
	 * @throws JDOMException  If a JDOM error occurs
	 */
	public static String getNamespaceURI(Document _document) throws JDOMException
	{
		Element root = _document.getRootElement();
		Namespace namespace = root.getNamespace();
		return namespace.getURI();
	}

	/**
	 * Get the name of the root element in a JDOM {@link Document}
	 * @param _document A specific JDOM {@link Document}
	 * @return  A string representing the name of the root element
	 * @throws JDOMException  If a JDOM error occurs
	 */
	public static String getRootElementName(Document _document) throws JDOMException
	{
		Element root = _document.getRootElement();
		return root.getName();
	}

	///////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////

	/**
	 * Capsulate namespace prefix and URI to the XPath,
	 * evaluate the wrapped XPath expression and returns the first 
	 * entry in the list of selected nodes. 
	 * @param _document  the JDOM {@link Document}
	 * @param _xpath  A string of a specific XPath expression
	 * @param prefix  A string of the namespace prefix
	 * @param uri  A string if the namespace URI
	 * @return  An Object of the single selected node
	 * @throws JDOMException  If a JDOM error occurs 
	 */
	public static java.lang.Object getNodeByXPath(Document _document, String _xpath, String prefix, String uri) throws JDOMException
	{
		XPath xpath;
		if (_xpath.contains("//")) {
			if (prefix != null) {
				xpath = XPath.newInstance(_xpath);//.replaceAll("//", "//" + prefix + ":"));
			} else {
				xpath = XPath.newInstance(_xpath);
			}
			xpath.addNamespace(Namespace.getNamespace(prefix, uri));
			System.out.println(xpath);
			return XPath.selectSingleNode(_document, _xpath);
		} else if (_xpath.contains("/")) {
			if (prefix != null) {
				xpath = XPath.newInstance(_xpath);//.replaceAll("/", "/" + prefix + ":"));
				return XPath.selectSingleNode(_document, _xpath);//.replaceAll("/", "/" + prefix + ":"));
			} else {
				//Null prefix's require some special work:
				String new_xpath = "";
				StringTokenizer tokenizer = new StringTokenizer(_xpath, "/");
				while (tokenizer.hasMoreTokens()) {
					new_xpath += "/*[namespace-uri()=\"" + uri + "\" and local-name()=\"" + tokenizer.nextToken() + "\"]";
				}
				return XPath.selectSingleNode(_document, new_xpath);
			}
		}
		return null;	   
	}

	/**
	 * Capsulate namespace to the XPath, evaluate the wrapped XPath expression 
	 * and returns the first entry in the list of selected nodes. 
	 * @param _document  the JDOM {@link Document}
	 * @param _xpath  A string of a specific XPath expression
	 * @param _namespace A {@link Namespace} of the xml Document
	 * @return  An Object of the single selected node
	 * @throws JDOMException  If a JDOM error occurs 
	 */
	public static java.lang.Object getNodeByXPath(Document _document, String _xpath, Namespace _namespace) throws JDOMException
	{
		return getNodeByXPath(_document, _xpath, _namespace.getPrefix(), _namespace.getURI());
	}

	/**
	 * Capsulate namespace prefix and URI to the XPath,
	 * evaluate the wrapped XPath expression and returns the value 
	 * of the first entry in the list of selected nodes. 
	 * @param _document  the JDOM {@link Document}
	 * @param _xpath  A string of a specific XPath expression
	 * @param prefix  A string of the namespace prefix
	 * @param uri  A string if the namespace URI
	 * @return  An String of the value
	 */
	public static String getValueByXPath(Document _document, String _xpath, String prefix, String uri)
	{
		try
		{
			Object element = XMLUtilities.getNodeByXPath(_document, _xpath, prefix, uri);
			if(element == null)
				return null;

			if(element instanceof Element)
				return ((Element)element).getText();
			else if(element instanceof Attribute)
				return ((Attribute)element).getValue();
		}
		catch(JDOMException e)
		{
			m_logger.error(e);
		}
		return null;
	}

	/**
	 * Capsulate namespace to the XPath, evaluate the wrapped XPath expression 
	 * and returns the first entry in the list of selected nodes. 
	 * @param _context the JDOM {@link Document} as an {@link Object}.
	 * @param _xpath  A string of a specific XPath expression
	 * @param _namespace A {@link Namespace} of the xml Document
	 * @return  An Object of the single selected node
	 * @throws JDOMException  If a JDOM error occurs 
	 */
	public static String getValueByXPath(Object _context, String _xpath, Namespace _namespace) 
	{
		try
		{
			XPath xpath = XPath.newInstance(_xpath);
			xpath.addNamespace(_namespace);

			Object element = xpath.selectSingleNode(_context);
			if(element == null)
				return null;

			if(element instanceof Element)
				return ((Element)element).getText();
			else if(element instanceof Attribute)
				return ((Attribute)element).getValue();
		}
		catch(JDOMException e)
		{
			m_logger.error(e);
		}
		return null;
	}

	/**
	 * Set a specific value to an entry refered by a 
	 * namespace prefix and URI capsulated Xpath.
	 * @param _document  the JDOM {@link Document}
	 * @param _xpath  A string of a specific XPath expression
	 * @param prefix  A string of the namespace prefix
	 * @param uri  A string if the namespace URI
	 * @param _value  A specific value to be set to the entry
	 * @throws JDOMException  If a JDOM error occurs 
	 */
	public static void setValueByXPath(Document _document, String _xpath, String prefix, String uri, String _value)
	{
		try
		{
			Object element = XMLUtilities.getNodeByXPath(_document, _xpath, prefix, uri);
			if(element == null)
				return;

			if(element instanceof Element)
				((Element)element).setText(_value);
			else if(element instanceof Attribute)
				((Attribute)element).setValue(_value);
		}
		catch(JDOMException e)
		{
			m_logger.error(e);
		}
	}

	/**
	 * Set a specific value to an entry refered by a 
	 * namespace capsulated Xpath.
	 * @param _document  the JDOM {@link Document}
	 * @param _xpath  A string of a specific XPath expression
	 * @param _namespace A {@link Namespace} of the xml Document
	 * @param _value  A specific value to be set to the entry
	 * @throws JDOMException  If a JDOM error occurs 
	 */
	public static void setValueByXPath(Document _document, String _xpath, Namespace _namespace, String _value) 
	{
		setValueByXPath(_document, _xpath, _namespace.getPrefix(), _namespace.getURI(), _value);
	}

	/**
	 * Returns a string of a JDOM {@link Element} in the raw format
	 * @param _element a JDOM {@link Element}
	 * @return a string representing the Element's content
	 */
	public static String getContent(Element _element)
	{
		StringWriter writer = new StringWriter();
		Document document = new Document((Element)_element.clone());

		// Use the predefined raw Format to process the Document.
		// In the default use, this will give us '<tag>content</tag>'.
		try
		{
			Format format = Format.getRawFormat().setOmitDeclaration(true);
			XMLOutputter outputter = new XMLOutputter(format);
			outputter.output(document, writer);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}

		String _return = writer.toString();
		int name_length = _element.getName().length();

		// Trim off the <tag></tag> from the ends of the String and return.
		if(_return.length() >= (name_length * 2) + 5)
			return _return.substring(name_length + 2, _return.length() - (name_length + 5));
		return _return;
	}
}