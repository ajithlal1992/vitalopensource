package com.vtls.opensource.vital.architecture;

import java.lang.StringBuffer;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import com.vtls.opensource.utilities.URLEncoder;

/**
 * A class used to store a URI, initially designed for use in Velocity but
 * with a number of uses elsewhere.
 */
public class Link
{
   /** A map to store all parameters */
   private Map m_map = null;
   
   /** A string of the base target URI, same as the servlet path */
   private String m_base_target = null;
   
   /** A string including the protocol, server name and port number,*/
   private String m_container_url = null;
   
   /** A {@link Stack} storing the target path element */
   private Stack m_path_stack = null;

   /** A text for this Link */
   private String m_text = null;
   /** A boolean to mark if this Link is XHTML or not */
   private boolean m_generate_xhtml = true;
   
   /** A boolean to mark if this link is absolute or not */
   private boolean m_absolute = false;
  
   /** A string of the servlet path for this link */
   private String m_servlet_path = null;

   // NOTE ///////////////////////////////////////////////////////////////////
   //
   // Important: If you are adding any additional members, they must be added
   // to the clone(), etc. methods.
   //
   ///////////////////////////////////////////////////////////////////////////
   
   // Constants.
   private static final String INDEX_PAGE = "Index";
   
   /**
    * Class constructor
    */
   public Link()
   {
      m_map = new LinkedHashMap();
      m_path_stack = new Stack();
   }
   
   /**
    * Class constructor specifying the target URI
    * @param _target a string of the target URI
    */
   public Link(String _target)
   {
      this();
      setTarget(_target);
   }

   /**
    * Construct an instance of this class using a {@link HttpServletRequest}.
    * We'll be interested in the current path and passed in parameters.
    * @param _request An active {@link HttpServletRequest} object.
    */
   public Link(HttpServletRequest _request)
   {
      this();
      
      Enumeration enumeration = _request.getParameterNames();
      while(enumeration.hasMoreElements())
      {
         // TODO: Handle multiple values for an attribute.
         String attribute = (String)enumeration.nextElement();
         String value = _request.getParameter(attribute);
         setParameter(attribute, value);
      }
      
      String request_uri = _request.getRequestURI();
      m_servlet_path = _request.getServletPath();

      String url = _request.getRequestURL().toString();
      int position = url.indexOf(request_uri);
      if(position > 0)
      {
    	  m_container_url = url.substring(0, position);
      }
      
      // The base target allows users of this class to ignore the specific
      // name of the webapp when constructing URLs.
      int request_position = request_uri.indexOf(m_servlet_path);
      if(request_position >= 0)
      {
         m_base_target = request_uri.substring(0, request_position);
         setTarget(request_uri.substring(request_position));
      }
      else
      {
         setTarget(request_uri);
      }
   }
 
   ///////////////////////////////////////////////////////////////////////////
   // Parameter Methods //////////////////////////////////////////////////////
   
   /**
    * Gets the value of a specific parameter in the request
    * @param _attribute a specific parameter 
    * @return the value of the parameter
    */
   public String getParameter(String _attribute)
   {
      return (String)m_map.get(_attribute);
   }
   
   /**
    * Sets a name-value pair into the parameter container of this Link.
    * If the parameter exists already, the new value will replace the old one.
    * @param _attribute  a specific parameter
    * @param _value a specific value for this parameter
    * @return this Link with the updated parameter
    */
   public Link setParameter(String _attribute, String _value)
   {
      m_map.put(_attribute, _value);
      return this;
   }
   
   /**
    * Removes a specific parameter from this Link.
    * @param _attribute a specific parameter
    * @return this Link with the specific parameter removed
    */
   public Link removeParameter(String _attribute)
   {
      m_map.remove(_attribute);
      return this;
   }

   /**
    * Removes all parameters in this Link
    * @return this Link with no parameters
    */
   public Link clearParameters()
   {
      m_map.clear();
      return this;
   }

   /**
    * Removes all parameters with a specific pattern.
    * @param _pattern a string of a specific parameter pattern
    * @return this Link with all pattern-matched parameters removed
    */
   public Link clearParameters(String _pattern)
   {
	   Pattern pattern = Pattern.compile(_pattern);
	   Set set = new HashSet();
	   
	   // We can't iterate through a Map and remove objects, so we'll add any
	   // matching attributes to a Set and delete when we have them all.
      Iterator i = m_map.keySet().iterator();
      while(i.hasNext())
      {
         String attribute = (String)i.next();
		 if(pattern.matcher(new String(attribute)).find())
		 {
			 set.add(attribute);
		 }
      }

      // Iterate through the matches and remove.
      i = set.iterator();
      while(i.hasNext())
      {
         String attribute = (String)i.next();
         m_map.remove(attribute);
      }
      return this;
   }
   
   /**
    * Gets a string containing all parameters and values
    * @return the parameter string
    */
   public String getParameterString()
   {
      StringBuffer _return = new StringBuffer();

      Iterator i = m_map.keySet().iterator();
      while(i.hasNext())
      {
         if(_return.length() != 0)
         {
        	 _return.append((isXHTML()) ? "&amp;" : "&");
         }
            
         String attribute = (String)i.next();
         String value = URLEncoder.encode((String)m_map.get(attribute));
         _return.append(attribute).append("=").append(value);
      }
      return _return.toString();
   }
   
   ///////////////////////////////////////////////////////////////////////////
   // Formatting Methods /////////////////////////////////////////////////////
   /**
    * Check if this Link is XHTML or not.
    * @return true if this Link is XHTML, otherwise return false
    */
   public boolean isXHTML()
   {
      return m_generate_xhtml;
   }

   /**
    * Sets a XHTML state to this Link to mark whether it is 
    * a XHTML Link or not.
    * @param _state the XHTML state of this link
    * @return this Link with the XHTML state 
    */
   public Link setXHTML(boolean _state)
   {
      m_generate_xhtml = _state;
      return this;
   }
   
   /**
    * Returns true if this Link is a fully qualified link
    * @return true if this Link is an absolute link, otherwise false
    */
   public boolean isAbsolute()
   {
      return m_absolute;
   }

   /**
    * Sets a link to be an absolute link or not
    * @param _absolute a boolean to mark if this link is absolute or not
    * @return this Link with an absolute link state 
    */
   public Link setAbsolute(boolean _absolute)
   {
      m_absolute = _absolute;
      return this;
   }
   
   ///////////////////////////////////////////////////////////////////////////
   // Text Methods ///////////////////////////////////////////////////////////
   
   /**
    * Gets the text in this Link
    * @return a string of the text in this Link 
    */
   public String getText()
   {
      return m_text;
   }
   
   /**
    * Sets a specific text for this Link
    * @param _text a specific text to be set to this Link
    */
   public Link setText(String _text)
   {
      m_text = _text;
      return this;
   }

   ///////////////////////////////////////////////////////////////////////////
   // Base Target Methods ////////////////////////////////////////////////////
   /**
    * Gets the base target URI
    * @return a string of the base target URI
    */
   public String getBaseTarget()
   {
      return m_base_target;
   }
   
   /**
    * Sets a base target URI to this Link
    * @param _base_target the base target URI to be set
    * @return this Link with the new base target URI
    */
   public Link setBaseTarget(String _base_target)
   {
      m_base_target = _base_target;
      m_path_stack.insertElementAt(_base_target, 0);
      return this;
   }
   
   ///////////////////////////////////////////////////////////////////////////
   // Target Methods /////////////////////////////////////////////////////////

   /**
    * Sets the target URI
    * @param _target a string of the target URI
    * @return a {@link Link} with the target URI
    */
   public Link setPage(String _target)
   {
      if(m_servlet_path != null)
      {
    	  return setTarget(m_servlet_path + _target);
      }
      else
      {
    	  return setTarget(_target);
      }
   }
   
   /**
    * Sets the target URI and keep all path elements into 
    * the path stack.
    * @param _target a string of the target URI
    * @return a {@link Link} with the target URI
    */
   public Link setTarget(String _target)
   {
      m_path_stack.clear();
      
      if(_target.startsWith("/") && getBaseTarget() != null)
      {
    	  m_path_stack.push(getBaseTarget());
      }

      String[] path = _target.split("/");
      for(int i = 0; i < path.length; i++)
      {
         if(!"".equals(path[i]))
         {
        	 m_path_stack.push(path[i]);
         }
      }

      if(_target.endsWith("/"))
      {
    	  m_path_stack.push(INDEX_PAGE);
      }
         
      return this;
   }
   
   /**
    * Change the target URI
    * @param _target a string of a specific target URI
    * @return this Link with the modified target URI
    */
   public Link modifyTarget(String _target)
   {
      if(_target == null)
      {
    	  return this;
      }
         
      if(_target.startsWith("/"))
      {
    	  return setTarget(_target);
      }
         
      String[] path = _target.split("/");
      for(int i = 0; i < path.length; i++)
      {
         if(".".equals(path[i]));
         else if("..".equals(path[i]))
         {
        	 m_path_stack.pop();
         }
         else
         {
        	 m_path_stack.push(path[i]);
         }
      }
         
      return this;
   }
   
   /**
    * Gets the target URI with concatenating each path element 
    * and query string
    * @return a string of the target URI
    */
   public String getTarget()
   {
      StringBuffer _return = new StringBuffer();

      Iterator iterator = m_path_stack.iterator();
      while(iterator.hasNext())
      {
         String path = iterator.next().toString();
         _return.append(path);
         if(iterator.hasNext())
         {
        	 _return.append("/");
         }
      }

      String parameters = getParameterString();
      if(parameters.length() > 0)
      {
    	  _return.append("?").append(parameters);
      }

      return _return.toString();
   }

   ///////////////////////////////////////////////////////////////////////////
   // Object Methods /////////////////////////////////////////////////////////
 
   /**
    * Duplicate this Link
    * @return a duplicated instance of this Link 
    */
   private Link duplicate()
   {
      return (Link)clone();
   }

   /**
    * Copy all variables and values in this Link to a new 
    * Link instance, and return this cloned Link
    * @return an Object of the duplicated Link 
    */
   protected Object clone()
   {
      Link _return = null;
      try
      {
         _return = (Link)getClass().newInstance();
      }
      catch(Exception e)
      {
         _return = new Link();
      }
      
      // Clone the Map. (Casting here back to the subclass.)
      LinkedHashMap map = (LinkedHashMap)m_map;
      _return.m_map = (Map)map.clone();
      
      // Clone the Stack. (Casting here back to the subclass.)
      Stack path_stack = m_path_stack;
      _return.m_path_stack = (Stack)path_stack.clone();
     
      // Clone the remaining members.
      _return.m_text = m_text;
      _return.m_generate_xhtml = m_generate_xhtml;
      _return.m_base_target = m_base_target;
      _return.m_servlet_path = m_servlet_path;
     
      return _return;
   }
   
   /**
    * Gets a string of this Link including the whole path and query string 
    * @return a string of the Link 
    */
   public String toString()
   {
      StringBuffer _return = new StringBuffer();

      if(m_container_url != null && m_absolute)
      {
    	  _return.append(m_container_url);
      }
      _return.append(getTarget());
      
      return _return.toString();
   }

   /**
    * Returns a string with each part name and value in this Link for debugging
    * @return a debug information for this Link
    */
   public String debug()
   {
      StringBuffer _return = new StringBuffer();
      _return.append("Target: [").append(m_base_target).append("] (").append(m_base_target.hashCode()).append(")\n");
      
      _return.append(hashCode()).append("\n");
      _return.append("m_path_stack (").append(m_path_stack.hashCode()).append(")\n");

      Iterator iterator = m_path_stack.iterator();
      while(iterator.hasNext())
      {
         String path = iterator.next().toString();
         _return.append(path);
         _return.append(" (").append(path.hashCode()).append(")\n");
         if(iterator.hasNext())
         {
        	 _return.append("/");
         }
      }

      iterator = m_map.keySet().iterator();
      while(iterator.hasNext())
      {
         String attribute = (String)iterator.next();
         String value = (String)m_map.get(attribute);
         _return.append(attribute).append("=").append(value);
         _return.append(" (").append(attribute.hashCode()).append("=").append(value.hashCode()).append(")\n");
      }

      return _return.toString();
   }
   
   ///////////////////////////////////////////////////////////////////////////
   // Interactive ////////////////////////////////////////////////////////////
   
   /**
    * Sets a boolean value to mark if this Link is absolute or not
    * @param _absolute a boolean to mark if it is an absolute link
    * @return a duplicated Link with the updated absolute link state 
    */
   public Link changeAbsolute(boolean _absolute)
   {
      return duplicate().setAbsolute(_absolute);
   }
   
   /**
    * Removes all parameters.
    * @return a duplicated Link with all parameters removed.
    */
   public Link changeParameters()
   {
      return duplicate().clearParameters();
   }

   /**
    * Removes all parameters of a specific pattern.
    * @return a duplicated Link with all pattern-matched parameters removed 
    */
   public Link changeParameters(String _pattern)
   {
      return duplicate().clearParameters(_pattern);
   }

   /**
    * Removes a specific parameter
    * @return a duplicated Link with a specific parameter removed
    */
   public Link changeParameter(String _attribute)
   {
      return duplicate().removeParameter(_attribute);
   }
   
   /**
    * Set a parameter name-value pair to the link
    * @param _attribute a string of the parameter's name
    * @param _value a string of the parameter's value
    * @return a duplicated Link with the a parameter added or updated 
    */
   public Link changeParameter(String _attribute, String _value)
   {
      return duplicate().setParameter(_attribute, _value);
   }

   /**
    * Change the target path
    * @param _target a string of the new target  
    */
   public Link changeTarget(String _target)
   {
      return duplicate().modifyTarget(_target);
   }
   
   /**
    * Switch to another page by changing the tail target
    * @param _target a specific target page
    * @return a duplicated {@link Link} with a changed page target
    */
   public Link changePage(String _target)
   {
      if(m_servlet_path != null)
      {
    	  return duplicate().modifyTarget(m_servlet_path + _target);
      }
      else
      {
    	  return duplicate().modifyTarget(_target);
      }
   }
   
   /**
    * Change the XHTML state
    * @param _state a boolean to mark the URI is XHTML or not
    * @return a duplicated {@link Link} instance with updated XHTML state
    */
   public Link changeXHTML(boolean _state)
   {
      return duplicate().setXHTML(_state);
   }
   
   /**
    * Change the text for this Link.
    * @param _text a string of the text for this Link
    * @return a duplicated {@link Link} instance with changed text
    */
   public Link changeText(String _text)
   {
      return duplicate().setText(_text);
   }
   
   /**
    * Change the base target
    * @param _base_target a specific base target for this Link
    * @return a duplicated {@link Link} instance with updated base target
    */
   public Link changeBaseTarget(String _base_target)
   {
      return duplicate().setBaseTarget(_base_target);
   }
}