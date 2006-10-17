package com.vtls.opensource.vital.architecture;

import com.vtls.opensource.vital.architecture.Action;
import com.vtls.opensource.vital.architecture.ActionThread;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public abstract class ThreadedAction extends Action
{
   private static Map threadMap = null;
   protected volatile ActionThread thread = null;
   
   public void setUp()
   {
      super.setUp();

      if(threadMap == null)
         threadMap = new HashMap();

      thread = (ActionThread)threadMap.get(getId());
      if(thread == null)
      {
         thread = getActionThread();
         thread.init(request, response);
         threadMap.put(getId(), thread);
         thread.start();
      }
      else
         thread.init(request, response);
   }
   
	public HttpServletRequest getRequest()
	{
	   request = thread.getRequest();
	   request.setAttribute("m_status", thread.getStatusMessage());
	   request.setAttribute("m_title", thread.getTitle());
	   request.setAttribute("m_complete", (thread.isComplete()) ? "true" : "false");
		return request;
	}

	public HttpServletResponse getResponse()
	{
	   response = thread.getResponse();
		return response;
	}
	
	public void clearThread()
	{
	   thread = null;
      thread = getActionThread();
      thread.init(request, response);

      threadMap.put(getId(), thread);
      thread.start();
   }
	
   public String getId()
   {
      return session.getId();
   }
   
	public abstract ActionThread getActionThread();
}
