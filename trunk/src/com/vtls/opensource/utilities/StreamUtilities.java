package com.vtls.opensource.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * A collection of utility functions for getting input stream
 * from different sources. 
 */
public class StreamUtilities
{
	/**
	 * Get an InputStream from a URL or a URL with properties.
	 * @param _url A {@link URL}
	 * @return An {@link InputStream} representing the content from the URL 
	 * @throws IOException 
	 */
	public static InputStream getInputStreamFromURL(URL _url) throws IOException
	{
		InputStream _return = null;

		// Create an HttpClient.
		HttpClient client = new HttpClient();

		// Issue a GET HTTP method.
		HttpMethod method = new GetMethod(_url.toExternalForm());
		client.getParams().setConnectionManagerTimeout(8000);
		int status_code = client.executeMethod(method);

		if(status_code != 200)
		{
			method.releaseConnection();
			return _return;
		}

		byte[] response = method.getResponseBody();
		_return = new ByteArrayInputStream(response);

		method.releaseConnection();
		return _return;
	}

	/**
	 * Get an input stream from a specific named resource.
	 * @param _resource_name  The name of the resource
	 * @return An {@link InputStream} representing the resource
	 * @throws IOException 
	 */	
	public static InputStream getInputStreamFromResource(String _resource_name) throws IOException
	{
		ClassLoader class_loader = StreamUtilities.class.getClassLoader();
		return class_loader.getResourceAsStream(_resource_name);
	}

	/**
	 * Get a string from a source input stream.
	 * @param _stream  The source {@link InputStream}
	 * @return A string representing the resource
	 * @throws IOException 
	 */
	public static String getStringFromInputStream(InputStream _stream) throws IOException
	{
		StringBuffer _return = new StringBuffer();

		if(_stream == null)
			throw new IllegalArgumentException("The InputStream cannot be null.");

		BufferedReader reader = new BufferedReader(new InputStreamReader(_stream));

		String active_line = reader.readLine();
		while(active_line != null)
		{
			_return.append(active_line);
			_return.append(System.getProperty("line.separator"));
			active_line = reader.readLine();
		}
		return _return.toString();
	}

	/**
	 * Returns a byte array of an {@link InputStream}.
	 * @param _stream The {@link InputStream} to get a byte array of.
	 * @return The byte array representing the {@link InputStream}.
	 * @throws IOException If an error occurs working the the {@link InputStream}.
	 */
	public static byte[] getByteArrayFromInputStream(InputStream _stream) throws IOException
	{
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		StreamUtilities.stream(_stream, outputStream);
		_stream.close();
		outputStream.close();
		return outputStream.toByteArray();
	}

	/**
	 * Streams an {@link InputStream} to an {@link OutputStream}.
	 * @param _input The {@link InputStream}.
	 * @param _output The {@link OutputStream}.
	 * @throws IOException If any errors occur reading from or writing to the streams.
	 */
	public static void stream(InputStream _input, OutputStream _output) throws IOException
	{
		byte[] buffer = new byte[2048];
		int readLength = 0;

		while((readLength = _input.read(buffer)) != -1)
			_output.write(buffer, 0, readLength);
	}			

	/**
	 * Get an input stream from a source String.
	 * @param _string  The resource string
	 * @return an {@link InputStream} representing the resource
	 * @throws IOException 
	 */
	// Get an InputStream from a String.
	public static InputStream getInputStreamFromString(String _string) throws IOException
	{
		return (new ByteArrayInputStream(_string.getBytes("UTF-8")));
	}

	/**
	 * Save a source file content to an input stream  
	 * @param _input  an input stream used for saving the file content
	 * @param _file a source file
	 * @throws IOException
	 */
	// Source: http://www.jguru.com/faq/view.jsp?EID=26996
	public static void saveInputStream(InputStream _input, File _file) throws IOException
	{
		BufferedOutputStream output_stream = new BufferedOutputStream(new FileOutputStream(_file));
		BufferedInputStream input_stream = new BufferedInputStream(_input);

		int stream_byte;
		while ((stream_byte = input_stream.read()) != -1)
		{
			output_stream.write(stream_byte);
		}

		output_stream.flush();
		output_stream.close();
		input_stream.close();
	}
}