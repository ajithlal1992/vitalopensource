package com.vtls.opensource.networking;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * A class used to construct a URI link, initially designed for use in Velocity.
 */
public class LinkURI
{
   private Map m_map = null;
   private String m_target = null;
   private boolean m_is_xhtml = false;
   
   /**
    * Class constructor.
    */
   public LinkURI()
   {
      m_map = new LinkedHashMap();
   }
   
   /**
    * Contstruct an instance of this class using an HttpServlet request.
    * We'll be interested in the current path and passed in parameters.
    * @param _request An active {@link HttpServletRequest} object.
    */
   public LinkURI(HttpServletRequest _request)
   {
      this();
      setLocalTarget(_request.getRequestURI());
      
      Enumeration enumeration = _request.getParameterNames();
      while(enumeration.hasMoreElements())
      {
         String attribute = (String)enumeration.nextElement();
         // TODO: Need to handle multiple.
         String value = _request.getParameter(attribute);
         setLocalParameter(attribute, value);
      }
   }
   
   /**
    * Class constructor specifying the target URI
    * @param _target a string of the target URI
    */
   public LinkURI(String _target)
   {
      this();
      m_target = _target;
   }

   ///////////////////////////////////////////////////////////////////////////
   // LinkURI Methods ///////////////////////////////////////////////////////////
   /**
    * Returns true if the URI is XHTML.
    * @return true if the URI is XHTML, false otherwise.
    */
   public boolean isXHTML()
   {
      return m_is_xhtml;
   }
   
   /**
    * Set the URI's XHTML status.
    * @param _state a boolean to mark the URI is XHTML or not.
    * @return a duplicated {@link LinkURI} with XHTML state  
    */
   public LinkURI setXHTML(boolean _state)
   {
      return duplicate().setLocalXHTML(_state);
   }
   
   /**
    * Append a tail target to this URI's target.
    * @param _target a String of the tail target.
    * @return a duplicated {@link LinkURI} with appended target 
    */
   public LinkURI appendTarget(String _target)
   {
      return duplicate().setLocalTarget(m_target + _target);
   }
 
   /**
    * Set the URI's target.
    * @param _target a String of the target for this URI.
    * @return a duplicated {@link LinkURI} with a specific target 
    */
   public LinkURI setTarget(String _target)
   {
      return duplicate().setLocalTarget(_target);
   }
   
   /**
    * Set the URI's parameter.
    * @param _attribute a String of attribute name
    * @param _value a String of the attribute value
    * @return a duplicated {@link LinkURI} with a newly added parameter  
    */
   public LinkURI setParameter(String _attribute, String _value)
   {
      return duplicate().setLocalParameter(_attribute, _value);
   }
   
   /**
    * Remove the URI's parameter.
    * @param _attribute a String of parameter name
    * @return a duplicated {@link LinkURI} with a specific parameter removed 
    */
   public LinkURI removeParameter(String _attribute)
   {
      return duplicate().removeLocalParameter(_attribute);
   }

   /**
    * Get a query string of all parameters in name-value pairs
    * @return a query string containing all parameters 
    */
   public String getParameterString()
   {
      StringBuffer _return = new StringBuffer();

      Iterator i = m_map.keySet().iterator();
      while(i.hasNext())
      {
         if(_return.length() != 0)
         {
        	 _return.append((m_is_xhtml) ? "&amp;" : "&");
         }
            
         String attribute = (String)i.next();
         String value = (String)m_map.get(attribute);
         _return.append(attribute).append("=").append(value);
      }
      return _return.toString();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Local Methods //////////////////////////////////////////////////////////
   
   // TODO: Split this class into two separate classes, with a base class
   // containing a variation of the following four methods.
   
   /**
    * Set this XHTML status.
    * @param _state a boolean to mark the XHTML.
    * @return this LinkedURI with XHTML state  
    */
   private LinkURI setLocalXHTML(boolean _state)
   {
      m_is_xhtml = _state;
      return this;
   }
   
   /**
    * Set this XHTML status.
    * @param _state a boolean to mark the XHTML.
    * @return this LinkedURI with XHTML state  
    */
   private LinkURI setLocalTarget(String _target)
   {
      m_target = _target;
      return this;
   }
   
   /**
    * Set a parameter mapping to this parameter map container.
    * @param _attribute a String of attribute name
    * @param _value a String of the attribute value
    * @return this LinkedURI with a newly added parameter  
    */
   private LinkURI setLocalParameter(String _attribute, String _value)
   {
      m_map.put(_attribute, _value);
      return this;
   }
   
   /**
    * Remove a parameter from this LinkedURI.
    * @param _attribute a String of parameter name
    * @return this LinkedURI with a specific parameter removed 
    */
   private LinkURI removeLocalParameter(String _attribute)
   {
      m_map.remove(_attribute);
      return this;
   }
   
   /**
    * Duplicate this LinkedURI
    * @return a duplicated {@link LinkedURI} of this instance
    */
   private LinkURI duplicate()
   {
      return (LinkURI)clone();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Object /////////////////////////////////////////////////////////////////
   /**
    * Returns a string representing this URI, containing the target URI and 
    * parameters as query string
    * @return a string of the URI
    */
    public String toString()
   {
      StringBuffer _return = new StringBuffer();
      
     if(m_target != null)
     {
    	 _return.append(m_target);
     }

      String parameters = getParameterString();
      if(parameters.length() > 0)
      {
    	  _return.append("?").append(parameters);
      }

      return _return.toString();
   }

   /**
    * Clone this URI.
    * @return a duplicated instance of {@link LinkURI}
    */
   protected Object clone()
   {
      LinkURI _return = null;
      try
      {
         _return = (LinkURI)getClass().newInstance();
      }
      catch(Exception e)
      {
         _return = new LinkURI();
      }
      
      // Clone the Map. (Casting here back to the subclass.)
      LinkedHashMap local_map = (LinkedHashMap)m_map;
      _return.m_map = (Map)local_map.clone();
      
      // Clone the remaining members.
      _return.m_target = m_target;
      _return.m_is_xhtml = m_is_xhtml;
      
      return _return;
   }
}