package com.vtls.opensource.vital.architecture;

import java.util.HashMap;
import java.util.Map;

public class ApplicationMapSingleton extends HashMap
{
	private static ApplicationMapSingleton instance = null;
	
	private ApplicationMapSingleton()
	{
	   super(8);
	}
	
	public static synchronized ApplicationMapSingleton getInstance()
	{
      if(instance == null)
         instance = new ApplicationMapSingleton();
	    
      return instance;
	}
}
