package com.vtls.actions;

import com.vtls.opensource.vital.architecture.Action;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestAction extends Action
{
   public void setUp()
   {
      super.setUp();
      
	   if(!this.hasAttribute("Fonzie"))
	   {
	      this.setAttribute("Fonzie", "Wokka Wokka");
      }
   }
   
	public HttpServletRequest getRequest()
	{
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
		return "WEB-INF/actions/IndexAction.vm";
	}
}
