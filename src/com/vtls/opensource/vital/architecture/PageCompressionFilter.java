package com.vtls.opensource.vital.architecture;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.GZIPOutputStream;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig; 
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Credit: http://www.orionserver.com/tutorials/filters/
 */
public class PageCompressionFilter implements Filter
{
   private FilterConfig filterConfig; 
   
   /**
	 * Initialzation method.
	 * @param _filterConfig A {@link FilterConfig} instance.
	 * @throws ServletException 
    */
   public void init(FilterConfig _filterConfig) throws ServletException
   {
      filterConfig = _filterConfig;
   }
   
	/**
	 * Filter the user request.
	 * @param _request a {@link ServletRequest} instance
	 * @param _response a {@link ServletResponse} instance
	 * @param _chain a {@link FilterChain} instance 
	 */
   public void doFilter(final ServletRequest _request, final ServletResponse _response, FilterChain _chain) throws IOException, ServletException
   {
      HttpServletResponse response = (HttpServletResponse)_response;
      HttpServletRequest request = (HttpServletRequest)_request;
      
      GenericResponseWrapper responseWrapper = new GenericResponseWrapper(response);
      _chain.doFilter(request, responseWrapper);
      
      OutputStream out = response.getOutputStream();
      if(!isCached(responseWrapper) && !isIncluded(request) && accepts(request, "gzip"))
      {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
         GZIPOutputStream compressedStream = new GZIPOutputStream(outputStream);
         compressedStream.write(responseWrapper.getData());
         compressedStream.flush();
         compressedStream.close();
         response.setHeader("Content-Encoding", "gzip");
         out.write(outputStream.toByteArray());
         response.setContentLength(outputStream.size());
      }
      else
      {
         out.write(responseWrapper.getData());
      }
      out.flush();
      out.close();
   }

   protected boolean isIncluded(ServletRequest _request)
   {
      return (_request.getAttribute("javax.servlet.include.request_uri") != null);
   }

   protected boolean isCached(GenericResponseWrapper _responseWrapper)
   {
      return(_responseWrapper.getData().length <= 0);
   }

   protected boolean accepts(HttpServletRequest _request, String _encoding)
   {
      return headerContains(_request, "Accept-Encoding", _encoding);
   }

   protected boolean headerContains(final HttpServletRequest _request, final String _header, final String _value)
   {
      Enumeration headerValues = _request.getHeaders(_header);
      while(headerValues.hasMoreElements())
      {
         String headerValue = (String)headerValues.nextElement();
         if(headerValue.indexOf(_value) >= 0)
         {
            return true;
         }
      }
      return false;
   }

   /**
     * Destroy the filter and cleans up any remaining resources.
     */
    public void destroy()
    {
       filterConfig = null;
    }
}



