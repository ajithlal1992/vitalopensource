package com.vtls.opensource.oai.handlers;

import org.jdom.Element;

/**
 * This object is handed a record Element which it rips apart.
 * 
 * @author Chris Hall
 */
public interface Handler {
	/**
	 * Performs any pre-harvest initialization.
	 */
	public boolean initial();
	
	/**
	 * Informs self of a new request.
	 */
	public boolean start();
	
	/**
	 * Rips apart the record Element.
	 */
	public boolean handle(Element _record);
	
	/**
	 * Informs self of end of request.
	 */
	public boolean end();
	
	/**
	 * Performs any post-harvest finalization.
	 */
	public boolean finish();
}