package com.vtls.opensource.fedora;

import java.util.Date;
import java.util.Properties;
import java.text.SimpleDateFormat;

import com.vtls.opensource.rss.FeedEntry;

/**
 * This class is a basic, basic container for a Fedora object. We'll be
 * interested primarily in the {@link Properties}, but we'll want to implement
 * the {@link FeedEntry} methods.
 */
public class FedoraObjectProxy extends Properties implements FeedEntry
{
	// Format: 2005-04-04T18:10:23.696
	private static final SimpleDateFormat m_date_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
	
	/**
	 * Class construtor
	 */
   public FedoraObjectProxy()
   {
   }

   /**
    * Class constructor specifying a Fedora Object pid
    * @param _pid  a String of the Fedora Object PID
    */
   public FedoraObjectProxy(String _pid)
   {
      this();
      setProperty("pid", _pid);
   }

	///////////////////////////////////////////////////////////////////////////
	// Methods ////////////////////////////////////////////////////////////////
	
   /**
    * Gets the last modified date
    * @return  a String of the last modified date
    */
	public String getLastModifiedString()
	{
	   return getProperty("mDate");
	}

	/**
	 * Returns the properties kept by the parent class.
	 * @return A string representing this FedoraObjectProxy  instance
	 */
	public String toString()
	{
		// Returns the URL and the properties kept by the parent class.
		return "[FedoraObjectProxy]: " + super.toString();
	}

	/**
	 * Returns the relative path information
	 * @return A string of the relative URL 
	 */
   public String getPath()
   {
      // Modified from the full FedoraObjectProxy method of the same name.
      // This method returns the path information, not the complete URL.
      return "/get/" + getProperty("pid");
   }
   
   ///////////////////////////////////////////////////////////////////////////
   // FeedEntry //////////////////////////////////////////////////////////////
   /**
    * Gets the creator 
    * @return a string of the creator name
    */
	public String getCreator()
	{
	   return getProperty("creator");
	}
	
	/**
	 * Gets the description of the Fedora Object
	 * @return a string of the description
	 */
	public String getDescription()
	{
	   return getProperty("description");
	}
	
	/**
	 * Gets the Title
	 * @return a string of the title
	 */
	public String getTitle()
	{
	   return getProperty("title");
	}
	
	/**
	 * Gets the URL of the Fedora Object
	 * @return a string of the URL
	 */
	public String getURL()
	{
	   return getProperty("url");
	}

	/**
	 * Gets the last modified {@link Date} for the Fedora object
	 * @return a {@link Date} instance
	 */
	public Date getLastModifiedDate()
	{
		Date _return = null;

		if(getLastModifiedString() == null)
		{
			return new Date();
		}

		try
		{
			// Try to parse what we have into a legitimate Date using the member
			// set up for parsing Date objects in Fedora.
			_return = m_date_format.parse(getLastModifiedString());
		}
		catch(java.text.ParseException pe)
		{
			pe.printStackTrace();
		}
		
		// Return the Date object.
		return _return;
	}
}