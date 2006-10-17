package com.vtls.opensource.vital.architecture;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.Math;
import java.lang.StringBuffer;
import java.util.Properties;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.Collection;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import com.vtls.opensource.utilities.HttpSessionTrackerSingleton;

/**
 * A common {@link HttpServlet} class that parses the requested URL and
 * delivers the Action object matching the package name. For example, if
 * the path is http://localhost:8080/vtls/opensource/mail/Send then
 * the servlet will try to locate (by default) the Action object with the
 * package name 'com.vtls.actions.opensource.mail.SendAction'.
 */
public final class ArchitectureServlet extends HttpServlet
{
	private static final Logger m_logger = Log4JLogger.getLogger(ArchitectureServlet.class);
	
	private ServletConfig m_config = null;
	private HttpSessionTrackerSingleton m_sessions = null;
	private ApplicationMapSingleton m_application = null;

	private static String m_action_package = "com.vtls.actions";

	// Define the names of the session attributes used by this servlet.   
	private static final String SESSION_USER_PACKAGE = "user_package";
	private static final String SESSION_USER_CLASS = "user_class";

	// Define the names of the request attributes used by this servlet.
	private static final String REQUEST_ACTION_PARAMETERS = "m_action_parameters";
	private static final String REQUEST_CLASS = "m_class";
	private static final String REQUEST_LINK = "m_request_link";
	private static final String REQUEST_LOGGED_IN = "m_logged_in";
	private static final String REQUEST_SESSIONS = "m_sessions";
	private static final String REQUEST_TIMING = "m_timing";
	private static final String REQUEST_TIMESTAMP = "m_timestamp";
	private static final String REQUEST_INTENDED_ACTION = "vital.action.original";
	private static final String REQUEST_CONTAINER_URL = "m_container_url";
	private static final String REQUEST_SERVLET_URL = "m_servlet_url";
	private static final String REQUEST_SERVER_URL = "m_server_url";

	/**
	 * Initialzation method. 
	 * @param _config a {@link ServletConfig} instance from the servlet container
	 * @throws ServletException
	 */
	public void init(ServletConfig _config) throws ServletException
	{
		super.init(_config);
		m_config = _config;
		m_action_package = _config.getInitParameter("actions");
		m_sessions = HttpSessionTrackerSingleton.getInstance();
		m_application = ApplicationMapSingleton.getInstance();
	}

	/**
	 * Allows the servlet to handle the HTTP POST requests. 
	 * @param request a {@link HttpServletRequest} instance
	 * @param response a {@link HttpServletResponse} instance
	 * @throws IOException
	 * @throws ServletException
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		handleRequest(request, response);
	}

	/**
	 * Allows the servlet to handle the HTTP GET requests.
	 * @param request a {@link HttpServletRequest} instance
	 * @param response a {@link HttpServletResponse} instance
	 * @throws IOException
	 * @throws ServletException 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		handleRequest(request, response);
	}

	/**
	 * Find an Action object and delegate requests to the Action methods.
	 * @param request An HttpServletRequest instance.
	 * @param response An HttpServletResponse instance.
	 * @throws ServletException
	 * @throws IOException
	 */
	private void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// Note: Handled in the ArchitectureFilter.
		// request.setCharacterEncoding("UTF-8");
		// response.setCharacterEncoding("UTF-8");

		// Set Response headers to prevent browser page caching.
		if(request.getProtocol().equals("HTTP/1.1"))
		{
			response.setHeader("Cache-Control", "no-cache");
		}
		else if(request.getProtocol().equals("HTTP/1.0"))
		{
			response.setHeader("Pragma", "no-cache");
		}
		response.setDateHeader("Expires", 0);

		// If the query string contains an unmodified '&amp;', change and redirect.
		if(request.getQueryString() != null && !"".equals(request.getQueryString()) &&
				request.getQueryString().indexOf("&amp;") >= 0)
		{
			StringBuffer request_url = request.getRequestURL().append("?").append(request.getQueryString());
			response.sendRedirect(request_url.toString().replaceAll("&amp;", "&"));
			return;
		}

		// Load the appropriate Action from the request URI.
		Action action = getActionFromRequest(request);

		// Get the Session instance.
		HttpSession session = request.getSession();

		// Determine if we need to forward to an authenticating action.
		StringBuffer generic_package_name = new StringBuffer();
		if(action == null)
		{
			// Start the package name with 'com.vtls.actions'.
			if(m_config.getInitParameter("genericAction") == null)
			{
				generic_package_name.append(m_action_package).append(".").append("GenericAction");
			}
			else
			{
				generic_package_name.append(m_config.getInitParameter("genericAction"));
			}

			action = getActionFromPackageName(generic_package_name.toString());
			if(action == null)
			{
				throw new RuntimeException(new ClassNotFoundException(generic_package_name.toString()));
			}
		}

		// Get the package name. (Just 'com.vtls.actions' in the simple case.)
		String package_name = (String)request.getAttribute(REQUEST_INTENDED_ACTION);
		if(package_name == null)
		{
			package_name = getPackageName(action.getClass());
		}

		if(package_name != null && m_config.getInitParameter(package_name) != null)
		{
			// If there's no SESSION_USER_PACKAGE in the session, then use the login
			// Action we defined in our 'web.xml' configuration.
			if(session.getAttribute(SESSION_USER_PACKAGE) == null || !(package_name.startsWith((String)session.getAttribute(SESSION_USER_PACKAGE))))
			{
				action = getActionFromPackageName(m_config.getInitParameter(package_name));
			}
		}

		// Set the number of Sessions who have live sessions on the same Action.
		request.setAttribute(REQUEST_SESSIONS, String.valueOf(getCommonSessions(session, action)));
		request.setAttribute(REQUEST_CLASS, action.getClass().getName());

		// Add the base URL to the Request instance.
		String url = request.getRequestURL().toString();
		int position = url.indexOf(request.getRequestURI());
		if(position > 0)
		{
			request.setAttribute(REQUEST_CONTAINER_URL, url.substring(0, position) + request.getContextPath());
			request.setAttribute(REQUEST_SERVLET_URL, url.substring(0, position) + request.getContextPath() + request.getServletPath());
			request.setAttribute(REQUEST_SERVER_URL, url.substring(0, position));
		}

		// Begin processing the action.
		long timing_start = System.currentTimeMillis();
		// Store it in the session as the timestamp of the request.
		//request.setAttribute(REQUEST_TIMESTAMP, new Long(timing_start));
		session.setAttribute(REQUEST_TIMESTAMP, new Long(timing_start));

		// Perform Action processing/procedures
		action.init(this, request, response);
		action.setApplicationMap(m_application);
		action.setUp();

		try {
			// Get any changes to the HttpServletRequest.
			request = action.getRequest();
			request.setAttribute(REQUEST_LINK, new Link(request));

			// Check for 'com.vtls.actions.whatever' set to 'true', indicating a successful login.
			if(session.getAttribute(SESSION_USER_PACKAGE) != null && (package_name != null && package_name.startsWith((String)session.getAttribute(SESSION_USER_PACKAGE))))
			{
				request.setAttribute(REQUEST_LOGGED_IN, "true");
			}
			else if(action.getClass().getName().equals(generic_package_name))
			{
				// HACK: For prototyped pages, assume that any login is sufficient.
				if(session.getAttribute(SESSION_USER_PACKAGE) != null)
				{
					request.setAttribute(REQUEST_LOGGED_IN, "true");
				}
			}
			else
			{
				request.setAttribute(REQUEST_LOGGED_IN, "false");
			}

			// Get any changes to the HttpServletResponse.
			response = action.getResponse();

			// Process the Action.
			action.execute();
		} catch (Exception e) {
			String stackTrace = e.getMessage() + System.getProperty("line.separator");
			StackTraceElement[] elements = e.getStackTrace();

			for (int i=0; i<elements.length; i++) {
				StackTraceElement line = elements[i];
				stackTrace += line.toString() + System.getProperty("line.separator");
			}
			
			request.setAttribute("m_exceptionStackTrace", stackTrace);
			
			m_logger.error(e);

			throw new RuntimeException(e);
		}

		// Get view/redirect values from the Action.
		String view = action.getView();
		String redirect = action.getRedirect();

		// Add timing information to the Request object.
		request.setAttribute(REQUEST_TIMING, new Long(System.currentTimeMillis() - timing_start));

		// Add Application variables into the Request object.
		m_application.putAll(action.getApplicationMap());
		Iterator applicationIterator = m_application.keySet().iterator();
		while(applicationIterator.hasNext())
		{
			String attribute = (String)applicationIterator.next();
			request.setAttribute(attribute, m_application.get(attribute));
		}

		// Handle any overridden getRedirect() method in our Action.
		if(redirect != null)
		{
			if(redirect.indexOf("://") >= 0)
			{
				response.sendRedirect(redirect);
			}
			else
			{
				// Sample: http://localhost:8080/vtls/vital/package/
				// Split the request URI to get the 'vital' part of the URI.
				String[] request_tokens = request.getRequestURI().split("/");

				StringBuffer target = new StringBuffer().append(request.getContextPath()).append("/");
				if(request_tokens.length >= 2)
				{
					target.append(request_tokens[2]).append("/");
				}
				target.append(redirect);
				response.sendRedirect(target.toString());
			}
		}
		// If we have a valid view, forward the request/response.
		else if(view != null)
		{
			try
			{
				RequestDispatcher dispatcher;
				dispatcher = request.getRequestDispatcher("/" + view);
				dispatcher.forward(request, response);
			}
			catch(java.lang.IllegalStateException e)
			{
			}
		}
		// Otherwise, flush and close the response.
		else
		{
			// Be sure to close your PrintWriter/OutputStream in the Action if
			// you see the following error thrown by Tomcat. Oh, and 'getView'
			// must return null.
			//
			//   java.lang.IllegalStateException: getWriter() has already been
			//   called for this response
			//
			try
			{
				OutputStream out = response.getOutputStream();
				out.close();
			}
			catch(java.lang.IllegalStateException e)
			{
			}
		}

		// Cleanup the Action.
		action.tearDown();
	}

	/**
	 * Process the request URI and return the 'best' {@link Action}
	 * corresponding to the path.
	 * @param request An {@link HttpServletRequest} instance.
	 * @throws ServletException
	 */
	private Action getActionFromRequest(HttpServletRequest request) throws ServletException
	{
		// Split the request URI.
		if(request.getPathInfo() == null)
		{
			return null;
		}

		String[] request_tokens = request.getPathInfo().split("/");

		// Store the complete class reference with a StringBuffer.
		StringBuffer package_name = new StringBuffer();

		// Start the package name with 'com.vtls.actions'.
		package_name.append(m_action_package).append(".");

		// We'll also want to store the 'extra' parameters.
		Vector vector = new Vector();

		if(request.getPathInfo().endsWith("/"))
		{
			// Skip the '/vtls/vital' portion of the URI and append.
			for(int i = 0; i < request_tokens.length; i++)
			{
				package_name.append(request_tokens[i]).append(".");
			}

			// Append the default class name. This should really
			// be 'IndexAction', but we'll append that below.
			package_name.append("Index"); 
		}
		else
		{
			// Sample: http://localhost:8080/vtls/vital/package/
			// Sample: http://localhost:8080/vtls/vital/package/Action
			// Sample: http://localhost:8080/vtls/vital/package/Action/extra/extra

			// Skip the '/vtls/vital' portion of the URI and append. This time,
			// we'll ignore the final token, in this case, our class name.
			for(int i = 1; i < request_tokens.length; i++)
			{
				// If we don't exceed the single level of packages allowed, then
				// append to the package name.
				if(i < Math.min(1, request_tokens.length - 1))
				{
					package_name.append(request_tokens[i]).append(".");
				}

				// There's a donut-hole here since we're skipping the Action name.
				// Only Actions beyond the 'com.vtls.actions' package are allowed
				// to have extra parameters.

				// Add the extra parameters to the vector.
				if(i > 1)
				{
					vector.add(request_tokens[i]);
				}
			}

			// Append the Action name from the final token.
			package_name.append(request_tokens[Math.min(1, request_tokens.length - 1)]);
		}

		// Return the Action. Here, we use Vector of extra attributes to allow
		// the framework to go deeper within a directory tree.
		Action _return = getActionFromPackageName(package_name.toString() + "Action");
		while(_return == null && !vector.isEmpty())
		{
			String first = (String)vector.remove(0);
			package_name.append(".").append(first);
			_return = getActionFromPackageName(package_name.toString() + "Action");

			// Save the Action for troubleshooting purposes.
			request.setAttribute(REQUEST_INTENDED_ACTION, package_name.toString() + "Action");
		}

		// Add the Vector to the Request object.
		request.setAttribute(REQUEST_ACTION_PARAMETERS, vector);

		return _return;
	}

	/**
	 * Get the package name of the provided {@link Class} as a {@link String}.
	 * @param _class A {@link Class} instance.
	 * @return a string of the package name
	 */
	public static String getPackageName(Class _class)
	{
		if(_class == null)
		{
			return null;
		}

		String qualified_name = _class.getName();
		int class_position = qualified_name.lastIndexOf(".");
		return (class_position < 0) ? null : qualified_name.substring (0, class_position);
	}

	/**
	 * Get an {@link Action} instance from its {@link String} representation.
	 * @param _name A package name corresponding to an Action.
	 * @return An instance of an {@link Action}.
	 * @throws ServletException
	 */
	public Action getActionFromPackageName(String _name) throws ServletException
	{
		Action _return = null;
		try
		{
			// Get the appropriate class from our package name.
			Class instance = Class.forName(_name);
			_return = (Action)instance.newInstance();
		}
		catch(InstantiationException e)
		{
			throw new RuntimeException(e);
		}
		catch(IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
		catch (ClassNotFoundException e)
		{
			// throw new RuntimeException(e);
		}
		return _return;
	}

	/**
	 * Get the number of {@link HttpSession}s visiting the same Action. This method also
	 * stores and cleans up after the session instances prior to returning.
	 * @param _session The active {@link HttpSession}.
	 * @param _action The active {@link Action}.
	 * @return The number of sessions viewing the same Action.
	 */
	private int getCommonSessions(HttpSession _session, Action _action)
	{
		String class_name = (String)_session.getAttribute(SESSION_USER_CLASS);

		if(class_name != null)
		{
			// Troll the List for invalid sessions and delete all occurences of
			// the _session.getId() in the class List.

			List class_sessions = (List)m_sessions.get(class_name);
			List invalid_sessions = new LinkedList();

			if(class_sessions != null)
			{
				Iterator iterator = class_sessions.iterator();
				while(iterator.hasNext())
				{
					HttpSession list_session = (HttpSession)iterator.next();
					try
					{
						// Try to trigger an IllegalStateException if we're dealing
						// with an invalid session.
						boolean is_new_session = list_session.isNew();

						if(list_session.getId() != null && list_session.getId().equals(_session.getId()))
						{
							invalid_sessions.add(list_session);
						}
					}
					catch(IllegalStateException ise)
					{
						invalid_sessions.add(list_session);
					}
				}

				// Reuse the iterator and delete the queued up objects.
				iterator = invalid_sessions.iterator();
				while(iterator.hasNext())
				{
					HttpSession list_session = (HttpSession)iterator.next();
					class_sessions.remove(list_session);
				}
			}
		}

		// Add the number of other sessions on the same page to the
		// HttpRequest object.
		List action_sessions = (List)m_sessions.get(_action.getClass().getName());
		int _return = (action_sessions != null) ? action_sessions.size() : 0;

		// Set the user class in the session. We'll use it to determine people
		// 'looking' at the same Action by adding it to the PreservingHashMap.
		_session.setAttribute(SESSION_USER_CLASS, _action.getClass().getName());
		m_sessions.put(_action.getClass().getName(), _session);

		return _return;
	}
}
