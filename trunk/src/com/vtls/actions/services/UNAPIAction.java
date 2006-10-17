package com.vtls.actions.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.vtls.opensource.fedora.FedoraObjectProxy;
import com.vtls.opensource.fedora.FedoraResourceIndex;
import com.vtls.opensource.fedora.FedoraSPARQLHandler;

import com.vtls.opensource.unapi.UNAPI;
import com.vtls.opensource.unapi.UNAPIFormat;

import com.vtls.opensource.vital.architecture.Action;

/**
 * Specialized Action for UNAPI support.
 * @author VTLS Inc.
 */
public class UNAPIAction extends Action
{
	private FedoraResourceIndex m_resource_index = null;
  
   public void setUp()
   {
   	// Initialize the FedoraResourceIndex object we'll use.
      String hostname = properties.getProperty("fedora.hostname");
      String port = properties.getProperty("fedora.port");
   	m_resource_index = new FedoraResourceIndex(hostname, Integer.parseInt(port));
   }
   
	public HttpServletRequest getRequest()
	{
		return request;
	}

	public HttpServletResponse getResponse()
	{
	   // Set the content type.
		boolean is_debug_mode = (request.getParameter("debug") != null);
		response.setContentType((is_debug_mode) ? "text/plain" : "text/xml");

		return response;
	}
	
	public void execute()
	{
      try
      {
	      if(request.getParameter("uri") != null)
	         doGetURI();
	      else
	         doGetFormats();
      }
      catch(IOException e)
      { 
         throw new RuntimeException(e);
      }
	}

	public String getView()
	{
		return null;
	}
	
	public String getRedirect()
	{
		return null;
	}
	
   public void tearDown()
   {
      m_resource_index = null;
   }
   
   ///////////////////////////////////////////////////////////////////////////
   // Local Methods //////////////////////////////////////////////////////////

	public void doGetFormats() throws IOException
	{
      // Create the new UNAPI instance.
	   UNAPI m_unapi = new UNAPI();

	   UNAPIFormat dc_format = new UNAPIFormat("oai_dc", "application/xml");
	   dc_format.setAttribute(UNAPIFormat.NamespaceURI, "http://www.openarchives.org/OAI/2.0/oai_dc/");
	   dc_format.setAttribute(UNAPIFormat.SchemaLocation, "http://www.openarchives.org/OAI/2.0/oai_dc.xsd");
	   m_unapi.setFormat(dc_format);

		// Get a handle to the Writer.
		PrintWriter out = response.getWriter();
	   out.println(m_unapi.toString());         
	   out.close();
   }
   
	public void doGetURI() throws IOException
	{
      // Create the new UNAPI instance.
	   UNAPI m_unapi = new UNAPI();

		boolean is_debug_mode = (request.getParameter("debug") != null);

      // TODO: Obviously, a nice enhancement would be to break this out.
	   FedoraSPARQLHandler handler = new FedoraSPARQLHandler();

	   String query = "select $object $mime from <#ri>" +
         " where <URI> <info:fedora/fedora-system:def/view#hasDatastream> $object" +
         " and $object <info:fedora/fedora-system:def/view#mimeType> $mime" +
         " order by $object " +
         " limit 64";
         
      // Replace 'URI' above with the requested URI.
      query = query.replaceAll("URI", request.getParameter("uri"));

      try
      {
         // Parse the results with our custom handler.
         m_resource_index.parse(query, handler);

         // Iterate through the results.
         List query_list = handler.getList();
         Iterator i = query_list.iterator();

         if(query_list.size() == 0)
         {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, request.getParameter("uri"));
            return;
         }
         
         // Handle the rest of the list.
         while(i.hasNext())
         {
            // Each of these is a FedoraObjectProxy...
            FedoraObjectProxy object = (FedoraObjectProxy)i.next();

            String datastream = object.getProperty("object");
            Pattern pattern = Pattern.compile("/([A-Za-z0-9]+)$");

            Matcher matcher = pattern.matcher(datastream);
            if(matcher.find())
            {
               datastream = matcher.group(1);
               String mime_type = object.getProperty("mime");
          	   UNAPIFormat format = new UNAPIFormat(datastream, mime_type);
         	   m_unapi.setFormat(format);
            }
         }
         
   		// Get a handle to the Writer.
   		PrintWriter out = response.getWriter();

   	   if(request.getParameter("format") != null)
   	   {
   	      StringBuffer datastream_url = new StringBuffer().append("http://");
   	      datastream_url.append(properties.getProperty("fedora.hostname")).append(":");
   	      datastream_url.append(properties.getProperty("fedora.port"));
   	      datastream_url.append("/fedora/get/").append(request.getParameter("uri").substring(12));
   	      datastream_url.append("/").append(request.getParameter("format"));
   	      
            m_unapi.setFormatRedirect(request.getParameter("format"), datastream_url.toString());
            
            // Redirect if we can have a match with the stored formats.
            if(m_unapi.getFormatRedirect() == null)
               response.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "UMT");
            else
               response.sendRedirect(datastream_url.toString());
         }
         else
         {
            response.setStatus(HttpServletResponse.SC_MULTIPLE_CHOICES);
   	      out.println(m_unapi.toString());         
         }
         
	      out.close();
		}
		catch(Exception e)
		{
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "[" + e.getClass().getName() + "] " + e.getMessage());
		}
	}
}