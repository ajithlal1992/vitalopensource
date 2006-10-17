package com.vtls.actions;

import com.vtls.opensource.vital.architecture.Action;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexAction extends Action
{
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
