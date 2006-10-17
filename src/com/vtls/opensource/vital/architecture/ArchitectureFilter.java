package com.vtls.opensource.vital.architecture;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vtls.opensource.utilities.PreservingHashMap;
import com.vtls.opensource.utilities.PropertiesSingleton;

/**
 * A servlet {@link Filter} that presets attributes in the HttpServletRequest
 * instance for common objects required by the framework.
 */
public class ArchitectureFilter implements Filter
{
   private ServletContext m_context = null;
      
    /**
	 * Initialzation method. Called by the web container to indicate to a 
	 * filter that it is being placed into service
	 * @param _filter_config a {@link FilterConfig} instance used by the servlet container
	 * 						 to pass information to a filter during initializaiton
	 * @throws ServletException 
     */
	public void init(FilterConfig _filter_config) throws ServletException
	{
      m_context = _filter_config.getServletContext();

      try
      {
         // Load a PropertiesSingleton to be shared across all Actions.
         String properties_filename = _filter_config.getInitParameter("vital.properties");
         if(properties_filename != null)
         {
            PropertiesSingleton properties = PropertiesSingleton.getInstance();
            properties.load(new FileInputStream(m_context.getRealPath(properties_filename)));
            properties.setProperty("servlet.path", _filter_config.getServletContext().getRealPath("/"));
         }
      }
      catch(Exception e)
      {
         throw new ServletException(e);
      }
	}
	
	/**
	 * Called by the container when a request/response pair is passed through.
	 * The FilterChain passed in to this method allows the Filter to pass on the request
	 * and response to the next entity in the chain.
	 * @param _request a {@link ServletRequest} instance
	 * @param _response a {@link ServletResponse} instance
	 * @param _chain a {@link FilterChain} instance 
	 */
	public void doFilter(ServletRequest _request, ServletResponse _response, FilterChain _chain) throws ServletException, IOException
   {
      HttpServletRequest http_request = (HttpServletRequest)_request;
      HttpServletResponse http_response = (HttpServletResponse)_response;

      // Set the character encoding. (We should never get a UEE with UTF-8.)
      try
      {
         http_request.setCharacterEncoding("UTF-8");
         http_response.setCharacterEncoding("UTF-8");
      }
      catch(UnsupportedEncodingException uee)
      {
         throw new ServletException(uee);
      }

      /*
      // Set Response headers to prevent browser page caching.
      if(http_request.getProtocol().equals("HTTP/1.1"))
         http_response.setHeader("Cache-Control", "no-cache");
      else if(http_request.getProtocol().equals("HTTP/1.0"))
         http_response.setHeader("Pragma", "no-cache");
      
      http_response.setDateHeader("Expires", 0);
      */

 	  // Map out the query parameters.
	  PreservingHashMap parameter_map = new PreservingHashMap();
      Enumeration parameters = _request.getParameterNames();
      while(parameters.hasMoreElements())
      {
         String attribute = (String)parameters.nextElement();
         String[] values = _request.getParameterValues(attribute);

         for(int i=0; i< values.length; i++)
         {
        	 parameter_map.put(attribute, values[i]);
         }
      }
		_request.setAttribute("m_parameters", parameter_map);

      // Add attributes to the Request.
      _request.setAttribute("m_request", http_request);
      // _request.setAttribute("m_application", http_request.getContextPath());
      // _request.setAttribute("m_assets", http_request.getContextPath() + "/assets");
      _request.setAttribute("m_properties", PropertiesSingleton.getInstance());

      // If we forwarded then we don't continue the filters
      if(forward(http_request, http_response))
      {
    	  return;
      }

      // Send request and response to the next Filter
      _chain.doFilter(_request, _response);
   }
	
   private boolean forward(HttpServletRequest _request, HttpServletResponse _response) throws ServletException, IOException
   {
      return false;
   }

   private void dispatch(HttpServletRequest _request, HttpServletResponse _response, String _target) throws ServletException, IOException
   {
      RequestDispatcher dispatcher = _request.getRequestDispatcher(_target);
      dispatcher.forward(_request, _response);
   }

   private void redirect(HttpServletRequest _request, HttpServletResponse _response, String _target) throws ServletException, IOException
   {
      _response.sendRedirect(_request.getContextPath() + _target);
   }
   
   /**
    * Destroy the filter and cleans up any resources that are
    * being held
    */
   // Used for Filter interface.
   public void destroy()
   {
   }
}