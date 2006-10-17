package com.vtls.opensource.utilities;

import java.util.Properties;

public class PropertiesSingleton extends Properties
{
	private static PropertiesSingleton m_instance = null;
	
	/**
	 * Private, empty constructor.
	 */
	private PropertiesSingleton()
	{
	}
	
	public static synchronized PropertiesSingleton getInstance()
	{
      if(m_instance == null)
      {
    	  m_instance = new PropertiesSingleton();
      }
	    
      return m_instance;
	}
}
