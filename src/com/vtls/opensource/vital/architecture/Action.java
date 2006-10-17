package com.vtls.opensource.vital.architecture;

import java.util.Map;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.vtls.opensource.utilities.PropertiesSingleton;

/**
 * An abstraction of a 'page' in the framework. Here, we abstract away any
 * absolute dependencies on request method and assume we'll be relying on
 * content that can be handled appropriately. (HTML, VTL, etc.)
 *
 * This is a ripoff of the 'Action' in Struts, but adapted in concept by
 * others. (This model just seems to work well. Ideally, we'd like to explore
 * the same concept in the Portlets framework.)
 */
public abstract class Action
{
	private HttpServlet m_servlet = null;
	private Map application = null;

	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected HttpSession session;
	protected PropertiesSingleton properties = null;

	/**
	 * Class constructor
	 */
	protected Action()
	{
	}

	/**
	 * Initialization method. This is split out separately so that we could
	 * reference this method after getting an Action instance from its package.
	 * @param _servlet a {@link HttpServlet} instance
	 * @param _request a {@link HttpServletRequest} instance
	 * @param _response a {@link HttpServletResponse} instance
	 */
	public void init(HttpServlet _servlet, HttpServletRequest _request, HttpServletResponse _response)
	{
		m_servlet = _servlet;

		session = _request.getSession();
		request = _request;
		response = _response;
		properties = PropertiesSingleton.getInstance();
	}

	/**
	 * 
	 */
	protected void setApplicationMap(final Map _application)
	{
		application = _application;
	}

	/**
	 * 
	 */
	protected Map getApplicationMap()
	{
		return application;
	}

	/**
	 * 
	 */
	protected void setAttribute(final String _attribute, final Object _value)
	{
		if(_attribute == null || application == null)
			throw new RuntimeException("Action.setAttribute() requires non-null context.");

		application.put(_attribute, _value);
	}

	/**
	 * 
	 */
	protected boolean hasAttribute(final String _attribute)
	{
		if(application == null)
			return false;

		return application.containsKey(_attribute);
	}

	/**
	 * 
	 */
	protected Object getAttribute(final String _attribute)
	{
		if(application == null)
			return null;

		return application.get(_attribute);
	}

	/**
	 * Gets the {@link HttpServlet}
	 * @return a {@link HttpServlet} 
	 */
	protected HttpServlet getServlet()
	{
		return m_servlet;
	}

	/**
	 * Initialize any Action-specific members. This method will be called
	 * before any other exposed method in the Action.
	 */
	public void setUp()
	{
	}

	abstract public HttpServletRequest getRequest();
	abstract public HttpServletResponse getResponse();
	abstract public void execute();
	abstract public String getView();

	/**
	 * This method allows instances to define that the browser should be
	 * redirected elsewhere
	 * @return null if there is no redirect url
	 */
	public String getRedirect()
	{
		return null;
	}

	/**
	 * Clean up any objects we want to explicitly destroy. This method will be
	 * called after the Action is processed.
	 */
	public void tearDown()
	{
	}
}