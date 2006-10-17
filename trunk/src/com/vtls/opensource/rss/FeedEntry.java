package com.vtls.opensource.rss;

import java.util.Date;

/**
 * This interface defines the methods required for a class to publish its
 * information to a feed.
 */
public interface FeedEntry
{
	/**
	 * Get the creator for this FeedEntry
	 * @return a string of the creator 
	 */
	public String getCreator();
	
	/**
	 * Get the creator for this FeedEntry
	 * @return a string of the creator 
	 */
	public String getDescription();
	/**
	 * Get the title for this FeedEntry
	 * @return a string of the title 
	 */
	public String getTitle();
	/**
	 * Get the URL for this FeedEntry
	 * @return a string of the URL 
	 */
	public String getURL();
	/**
	 * Get the last modified date for this FeedEntry
	 * @return an instance of the {@link Date}
	 */
	public Date getLastModifiedDate();
}
