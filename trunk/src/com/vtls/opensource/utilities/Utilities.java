package com.vtls.opensource.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 * A set of utility functions covering a few general 
 * input stream transformation and saving.
 */
public class Utilities
{
	/**
	 * Get an {@link InputStream} from a {@link URL} using the commons {@link HttpClient}.
	 * @param _url An appropriately-formatted {@link URL}.
	 * @return The {@link InputStream} for the {@link URL}.
	 * @throws {@link MalformedURLException}, {@link IOException}
	 */
	public static InputStream getInputStreamFromURL(URL _url) throws MalformedURLException, IOException
	{
	   InputStream _return = null;
	   		
		// Create an HttpClient.
		HttpClient client = new HttpClient();

		// Issue a GET HTTP method.
		HttpMethod method = new GetMethod(_url.toExternalForm());
		int status_code = client.executeMethod(method);

		if(status_code != 200)
		   return _return;

	   byte[] response = method.getResponseBody();
	   _return = new ByteArrayInputStream(response);

		return _return;
	}

	/**
	 * Get a String representation of an {@link InputStream}.
	 * @param _stream An {@link InputStream}.
	 * @return A {@link String}.
	 */
	public static String getStringFromInputStream(InputStream _stream) throws IOException
	{
      StringWriter _return = new StringWriter();
      BufferedReader reader = new BufferedReader(new InputStreamReader(_stream));

      int length = 0;
      char buffer[] = new char[1024];

      while((length = reader.read(buffer, 0, 1024 )) != -1)
         _return.write(buffer, 0, length);

      return _return.toString();
   }

	/**
	 * Get an {@link InputStream} from a {@link String} instance.
	 * @param _string A String.
	 * @return An InputStream.
	 */
	public static InputStream getInputStreamFromString(String _string)
	{
	   try
	   {
	   	return (new ByteArrayInputStream(_string.getBytes("UTF-8")));
	   }
	   catch(java.io.UnsupportedEncodingException e)
	   { 
	     return null;
	   }
	}

	/**
	 * Save an {@link Object} to a given filename.
	 * @param _object The Object to be saved.
	 * @param _file The target file.
	 * @return A String containing the name of the file.
	 */
   public static String saveObject(Object _object, File _file) throws IOException
   {
	   return Utilities.saveInputStream(Utilities.getInputStreamFromString(_object.toString()), _file);
   }

	/**
	 * Save an {@link Object} to a given filename.
	 * @param _input_stream The InputStream to be saved.
	 * @param _file The target file.
	 * @return A String containing the name of the file.
	 */
	public static String saveInputStream(InputStream _input_stream, File _file) throws IOException
	{
		BufferedInputStream input_stream = new BufferedInputStream(_input_stream);
		BufferedOutputStream output_stream = new BufferedOutputStream(new FileOutputStream(_file));

		int stream_byte;
		while((stream_byte = input_stream.read()) != -1)
			output_stream.write(stream_byte);

		output_stream.flush();
		input_stream.close();
		
		return _file.getAbsolutePath();
	}
}
