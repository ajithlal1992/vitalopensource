package com.vtls.opensource.rss;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;

import org.apache.ecs.xml.XML;

/**
 * This class is designed to abstract the publishing of an RSS feed away from
 * the data required to create it. One can imagine a set of these for updated
 * versions of the RSS standard, or, more likely, rearranging the elements to
 * conform to the Atom standard.
 * @todo Create an example Atom variant of this class.
 */
public class RSSFeed extends Feed
{
   private String m_feed_language = null;
   
   /**
    * Create a new RSSFeed instance with the required information. For RSS,
    * we'll add language. (Note: this is actually also common to Atom, but
    * not to earlier versions.)
    * @param _description a string of the description for this RSSFeed
    * @param _language a string of the language for this RSSFeed
    * @param _title a string of the title for this RSSFeed
    * @param _url a string of the url for this RSSFeed  
    */
   public RSSFeed(String _title, String _url, String _description, String _language)
   {
      super(_title, _url, _description);
      m_feed_language = _language;
   }

   /**
    * The 'toString()' method does most of the heavy lifting in this class.
    * We'll create an XML document to store our feed and then return the
    * String representation. (Feel free to insert your lingua franca of
    * choice. I would have preferred using org.w3c.dom.Document, but String
    * works just as well for an example.)
    * @return A {@link String} representation of the XML.
    */
   public String toString()
   {
      XML document = new XML("rss");
      document.setPrettyPrint(true);

      document.addAttribute("version", "2.0");
      document.addAttribute("xmlns:dc", "http://purl.org/dc/elements/1.1/");
 
      document.addElement(getChannelXML());

      return document.toString();
   }
      
   /**
    * Returns an XML element containing the representation for the RSS
    * 'channel' block.
    * @return An {@link XML} instance.
    */
   private XML getChannelXML()
   {
      XML channel = new XML("channel");
      channel.setPrettyPrint(true);

      channel.addElement(new XML("title").addElement(getTitle()));
      channel.addElement(new XML("link").addElement(getURL()));
      channel.addElement(new XML("description").addElement(getDescription()));
      channel.addElement(new XML("language").addElement(m_feed_language));

      SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

      for(Iterator i = m_entries.iterator(); i.hasNext();)
      {
         FeedEntry entry = (FeedEntry)i.next();

         XML item = new XML("item");

         // Title.
         item.addElement(new XML("title").addElement(entry.getTitle()));

         // Target URL.
         if(entry.getURL() != null)
         {
        	 item.addElement(new XML("link").addElement(entry.getURL()));
         }

         // Description.
         item.addElement(new XML("description").addElement(entry.getDescription()));

         // Creator.
         if(entry.getCreator() != null)
         {
        	 item.addElement(new XML("dc:creator").addElement(entry.getCreator()));
         }
         
         // Date.
         Date modified_date = entry.getLastModifiedDate();
         if(modified_date != null)
         {
        	 item.addElement(new XML("dc:date").addElement(date_format.format(modified_date)));
         }

         channel.addElement(item);
      }

      return channel;
   }
}