package com.vtls.opensource.fedora;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * An abstraction for the Fedora ResourceIndex. This is substantially trimmed
 * down from the alternate version in com.vtls.fedora.* but performs
 * very nearly the same purpose.
 */ 
public class FedoraResourceIndex
{
   private String m_host = null;
   private int m_port = 8080;
   	
   /**
    * Class constructor specifying the Fedora resource host and port number.
    * @param _host  A String of the host server name
    * @param _port  An int of the port number
    */
	public FedoraResourceIndex(String _host, int _port)
	{
	   // This constructor is largely made up -- the VITAL codebase has objects
	   // proxying the FedoraRepository, FedoraObjectProxy, etc. concepts.
	   m_host = _host;
	   m_port = _port;
	}
	
   /**
    * Returns the URL representing the ResourceIndex service URL for the given
    * query.
    * @param _itql An ITQL search query {@link String}.
    * @param _format A format {@link String}, one of: {CSV, Simple, Sparql, TSV}.
    * return A {@link URL}.
    */
   public URL getURLForQuery(String _itql, String _format) throws MalformedURLException
   {
      // Create the 'file' part of the URL from the given information.
      // TODO: This might require enhancement for the other parameters.
      StringBuffer path = new StringBuffer();
      try
      {
         path.append("/fedora/risearch?type=tuples&lang=itql&format=");
         path.append(_format);
         path.append("&distinct=on&dt=on&flush=true&limit=&query=");
         path.append(URLEncoder.encode(_itql, "UTF-8"));
      }
      catch(UnsupportedEncodingException uee)
      {
         uee.printStackTrace();
         // The 'UTF-8' encoding will always be supported, so this should
         // never happen.
      }
      
      // Return.
      URL _return = new URL("http", m_host, m_port, path.toString());
      return _return;
   }

   /**
    * Parses a given ITQL query with a SAX document handler.
    * @param _itql A valid ITQL query {@link String}.
    * @param _handler A {@link DefaultHandler} to be used in parsing the returned SPARQL XML document.
    */
	public void parse(String _itql, DefaultHandler _handler) throws IOException, ParserConfigurationException, SAXNotRecognizedException, SAXException
	{
		// Use the default (i.e. non-validating) parser.
		SAXParserFactory sax_parser_factory = SAXParserFactory.newInstance();
		sax_parser_factory.setValidating(false);
		
	   // Set extended features.
      sax_parser_factory.setFeature("http://xml.org/sax/features/validation", false);

		// Get the entire contents of the datastream (via an InputStream
		// from the URL) and SAX parse it with our custom handler.
		SAXParser sax_parser = sax_parser_factory.newSAXParser();

      // Create an InputSource and parse.
      
		InputSource input_source = new InputSource(getInputStreamFromURL(getURLForQuery(_itql, "Sparql")));
		sax_parser.parse(input_source, _handler);
	}

   /**
    * A utility function probably best suited elsewhere. (For the RSS demo,
    * this was imported from com.vtls.utilities.*.)
    * @param _url A {@link URL}.
    * @return An {@link InputStream} used to stream information from the URL.
    */
	private static InputStream getInputStreamFromURL(URL _url) throws IOException
	{
	   InputStream _return = null;
	   		
		// Create an HttpClient.
		HttpClient client = new HttpClient();

		// Issue a GET HTTP method.
		HttpMethod method = new GetMethod(_url.toExternalForm());
		int status_code = client.executeMethod(method);

		if(status_code != 200)
		{
			return _return;
		}

	   byte[] response = method.getResponseBody();
	   _return = new ByteArrayInputStream(response);

		return _return;
	}
}
