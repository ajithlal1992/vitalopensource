package com.vtls.opensource.text;

import java.io.IOException;

/**
 * An interface defining the common {@link #getText()} method required by all
 * {@link TextSource} classes.
 */
public interface Text
{
	/**
	 * Gets the text from text source
	 * @return a string of the source text 
	 * @throws IOException if an input or output error occurs
	 */
	public String getText() throws IOException;
}
