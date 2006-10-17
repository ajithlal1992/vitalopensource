package com.vtls.actions.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import com.vtls.opensource.rss.RSSFeed;
import com.vtls.opensource.rss.FeedEntry;
import com.vtls.opensource.fedora.FedoraResourceIndex;
import com.vtls.opensource.fedora.FedoraSPARQLHandler;

import com.vtls.opensource.vital.architecture.Action;

/**
 * Specialized Action for RSS support.
 * @author VTLS Inc.
 */
public class RSSAction extends Action
{
	private FedoraResourceIndex m_resource_index = null;
	private RSSFeed m_feed = null;
	private Date m_last_modified = null;
  
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
	   // Set the content type.
		boolean is_debug_mode = (request.getParameter("debug") != null);
		response.setContentType((is_debug_mode) ? "text/plain" : "text/xml");

		try
		{
   		// Get a handle to the Writer.
   		PrintWriter out = response.getWriter();
	   
         // TODO: Obviously, a nice enhancement would be to break this out.
   	   FedoraSPARQLHandler handler = new FedoraSPARQLHandler();
   	   final String query = "select $object $mDate " +
            "subquery ( select $title from <#ri> where $object <dc:title> $title order by $title limit 1 ) " +
            "subquery ( select $creator from <#ri> where $object <dc:creator> $creator limit 1 ) " +
            "subquery ( select $description from <#ri> where $object <dc:description> $description limit 1 ) " +
            "from <#ri> " +
            "where $object <rdf:type> <fedora-model:FedoraObject> " +
            "and $object <fedora-view:lastModifiedDate> $mDate " +
            "order by $mDate desc " +
            "limit 64";

         // Parse the results with our custom handler.
         m_resource_index.parse(query, handler);
      
         // Initialize our RSS feed.
         m_feed = new RSSFeed(properties.getProperty("vital.rss.feedTitle"), properties.getProperty("vital.rss.feedURL"),
            properties.getProperty("vital.rss.feedDescription"), properties.getProperty("vital.rss.feedLanguage"));

         // Iterate through the results and add each (FedoraObjectProxy) to the Feed.
         Iterator i = handler.getList().iterator();
         
         // We'll also pay attention to the "If-Modified-Since" header and
         // only add more recent items.
         long last_cache_time = request.getDateHeader("If-Modified-Since");
         
         // Handle the first instance. (Special case: we want the Date.)
         if(i.hasNext())
         {
            // Save the Date of this object (This is the most recent.)
            FeedEntry object = (FeedEntry)i.next();
            m_last_modified = object.getLastModifiedDate();

            // Add to the feed.
            if(last_cache_time <= 0 || m_last_modified.getTime() > last_cache_time)
               m_feed.addFeedEntry(object);
         }
      
         // Handle the rest of the list.
         while(i.hasNext())
         {
            // Each of these is a FedoraObjectProxy, which implements FeedEntry, so
            // we don't have to do anything more than the following.
            FeedEntry object = (FeedEntry)i.next();

            if(last_cache_time <= 0 || object.getLastModifiedDate().getTime() > last_cache_time)
               m_feed.addFeedEntry(object);
         }

        // Output the Feed contents. (Look at the constructor for the actual
         // work that was done to load the Feed itself.)
		   out.println(m_feed.toString());
		   out.close();
		}
		catch(Exception e)
		{
			throw new RuntimeException("[" + e.getClass().getName() + "] " + e.getMessage());
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
}