package com.vtls.actions;

import com.vtls.opensource.vital.architecture.Action;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class GenericAction extends Action
{
   String m_filename = null;
	
	public HttpServletRequest getRequest()
	{
      String[] request_tokens = request.getRequestURI().split("/");
      StringBuffer target_filename = new StringBuffer().append("/actions");
      
      for(int i = 3; i < request_tokens.length; i++)
         target_filename.append("/").append(request_tokens[i]);
      
      // Handle the ending as appropriate.
      if(request.getRequestURI().endsWith("/"))
         target_filename.append("/IndexAction.vm");
      else
         target_filename.append("Action.vm");

      m_filename = target_filename.toString();

      request.setAttribute("m_filename", "WEB-INF" + m_filename);
		return request;
	}

	public HttpServletResponse getResponse()
	{
		return response;
	}

	public void execute()
	{
	}

	public String getView()
	{
      // Return the path to the Velocity template if it exists.
      File file = new File(getServlet().getServletContext().getRealPath("WEB-INF") + m_filename.toString());
      if(file.exists())
         return "WEB-INF" + m_filename;

	   return "WEB-INF/actions/IndexAction.vm";
	}
}
