package com.vtls.opensource.rss;

import java.util.Date;
import java.util.Properties;

/**
 * This class is meant as an example and test-case subject for the Feed
 * functionality. This serves as a pretty good example of how the 
 * {@link FeedEntry} methods need to be used.
 */
public class GenericFeedEntry extends Properties implements FeedEntry
{
   private Date m_modified_date = null;

   /**
    * Construct a new GenericFeedEntry with the basic information.
    * @param _description a string of the description for the feed entry
    * @param _title a string of the title for the feed entry
    * @param _url a string of the url for the feed entry    
    */
   public GenericFeedEntry(String _title, String _url, String _description)
   {
      setTitle(_title);
      setURL(_url);
      setDescription(_description);
   }

   /**
    * Construct a new GenericFeedEntry with the basic information and a Date.
    * @param _description a string of the description for the feed entry
    * @param _modified_date a string of the last modified date for the feed entry
    * @param _title a string of the title for the feed entry
    * @param _url a string of the url for the feed entry    
    */
   public GenericFeedEntry(String _title, String _url, String _description, Date _modified_date)
   {
      this(_title, _url, _description);
      setLastModifiedDate(_modified_date);
   }

   // Set methods. ///////////////////////////////////////////////////////////
   
   /**
    * Sets the creator property for this feed entry
    * @param _creator a string of the creator
    */
	public void setCreator(String _creator)
	{
	   setProperty("creator", _creator);
	}
	
	/**
	 * Sets the description property for this feed entry
     * @param _description a string of the description
     */
	public void setDescription(String _description)
	{
	   setProperty("description", _description);
	}
	
	/**
	 * Sets the title property for this feed entry
     * @param _title a string of the title
     */
	public void setTitle(String _title)
	{
	   setProperty("title", _title);
	}
	
	/**
	 * Sets the URL property for this feed entry
     * @param _url a string of the URL
     */
	public void setURL(String _url)
	{
	   setProperty("url", _url);
	}
	
	/**
	 * Sets the last modified date for this feed entry
     * @param _date an instance of {@link Date}
     */
	public void setLastModifiedDate(Date _date)
	{
	   m_modified_date = _date;
	}

   ///////////////////////////////////////////////////////////////////////////
   // FeedEntry //////////////////////////////////////////////////////////////

	/**
	 * Gets the value of the creator
	 * @return a string of the creator
	 */
	public String getCreator()
	{
	   return getProperty("creator");
	}
	/**
	 * Gets the value of the description
	 * @return a string of the description
	 */
	public String getDescription()
	{
	   return getProperty("description");
	}
	
	/**
	 * Gets the value of the title
	 * @return a string of the title
	 */
	public String getTitle()
	{
	   return getProperty("title");
	}
	
	/**
	 * Gets the URL for this feed entry
	 * @return a string of the URL
	 */
	public String getURL()
	{
	   return getProperty("url");
	}
	
	/**
	 * Gets the last modified date
	 * @return a last modified {@link Date} for this feed entry
	 */
	public Date getLastModifiedDate()
	{
	   return m_modified_date;
	}
}
