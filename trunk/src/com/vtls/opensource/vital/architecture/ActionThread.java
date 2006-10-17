package com.vtls.opensource.vital.architecture;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class ActionThread extends Thread implements Runnable
{
	protected HttpServletRequest request = null;
	protected HttpServletResponse response = null;
	protected HttpSession session = null;

	/**
	 * Stores common servlet objects.
	 * @param _request the {@link HttpServletRequest}.
	 * @param _response the {@link HttpServletResponse}.
	 */
	public void init(HttpServletRequest _request, HttpServletResponse _response)
	{
		request = _request;
		response = _response;
		session = _request.getSession();
	}

	/**
	 * Returns the {@link HttpServletRequest}.
	 */
	public abstract HttpServletRequest getRequest();

	/**
	 * Returns the {@link HttpServletResponse}.
	 */
	public abstract HttpServletResponse getResponse();

	/**
	 * Returns the title of the action.
	 */
	public abstract String getTitle();

	/**
	 * Returns the priority of the action. 
	 */
	public abstract int getActionPriority();

	/**
	 * Returns the status message.
	 */
	public abstract String getStatusMessage();

	/**
	 * Returns 
	 */
	public abstract boolean isComplete();
}