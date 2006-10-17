package com.vtls.opensource.rss;

import java.util.List; 
import java.util.LinkedList; 

/**
 * A base class for our Feed implementations. We'd like to keep the
 * feed format (RSS, Atom) separate from the model.
 */
public abstract class Feed
{ 
   protected List m_entries = new LinkedList(); 

   private String m_feed_title = null;
   private String m_feed_url = null;
   private String m_feed_description = null;
   
   /**
    * Construct the class with information about the feed.
    * @param _description a string of the description for this feed
    * @param _title a string of the title for this feed
    * @param _url a string of the url for this feed    
    */
   public Feed(String _title, String _url, String _description)
   { 
      m_feed_title = _title;
      m_feed_url = _url;
      m_feed_description = _description;
   } 

   /**
    * Add a FeedEntry to this feed.
    * @param _entry A {@link FeedEntry} object.
    */
   public void addFeedEntry(FeedEntry _entry) 
   { 
      m_entries.add(_entry);
   }
   
   /**
    * Get the title we specified for this feed.
    * @return The feed title.
    */
   public String getTitle()
   {
      return m_feed_title;
   }

   /**
    * Get the URL we specified for this feed.
    * @return The feed URL.
    */
   public String getURL()
   {
      return m_feed_url;
   }

   /**
    * Get the description we specified for this feed.
    * @return The feed description.
    */
   public String getDescription()
   {
      return m_feed_description;
   }

   ///////////////////////////////////////////////////////////////////////////
   // TODO: setTitle, setTitle, setDescription?
} 
