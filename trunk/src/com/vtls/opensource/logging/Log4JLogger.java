package com.vtls.opensource.logging;

import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;

//////////////////////////////////////////////////////////////////////////////
// Usage /////////////////////////////////////////////////////////////////////
/*

import com.vtls.opensource.logging.Log4JLogger;
import org.apache.log4j.Logger;

   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(MyObject.class);

*/
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////

/**
 * A subclass of {@link org.apache.log4j.Logger} that pre-configures a
 * Logger instance in the {@link #getLogger} method.
 *
 * <b>Usage:</b>
 *    <code>import com.vtls.logging.Log4JLogger;</code>
 *    <code>// Log4j instance.</code>
 *    <code>private static final Logger m_logger = Log4JLogger.getLogger(this.getClass());</code>
 * <b>Problems?</b> Try <code>-Dlog4j.debug</code>
 */
 public class Log4JLogger
{
   // Keep track of snapshot activity.
   private static long m_last_event = 0;
   
   /**
    * Shorthand for getLogger(_class.getName()).
    * @param _class the Class in the running application
    */
   public static Logger getLogger(Class _class)
   {
      return Log4JLogger.getLogger(_class.getName());
   }
   
   /** 
    * Retrieve a logger named according to the value of the name parameter.
    * @param _name a string of a specific logger name
    * @return an instance of {@link Logger} 
    */
   public static Logger getLogger(String _name)
   {
      // Configure the Logger with our configurations of choice.
      Logger _return = Logger.getLogger(_name);
      
      // Check for a passed-in property, which we'll use if we can.
      if(System.getProperty("log4j.configuration") != null)
      {
         DOMConfigurator.configureAndWatch(System.getProperty("log4j.configuration"));
      }
      else
      {
         // Otherwise, we'll use a default Log4j XML configuration file.
         // We can get this safely as an InputStream.
         InputStream stream = null;
         
         // Try to find the resource.
         try
         {
            stream = ClassLoader.getSystemResourceAsStream("logging/log4j.xml");

            // If we have a valid stream, configure the Logger appropriately.
            if(stream != null)
            {
               DOMConfigurator configurator = new DOMConfigurator();
               configurator.doConfigure(stream, LogManager.getLoggerRepository());
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }

         // If everything has failed to this point, go all-out generic.
         if (stream == null)
      		BasicConfigurator.configure();
      }
      
      return _return;
   }
   
   /**
    * Get the number of milliseconds that have elapsed since the last event.
    * @return a long number of the milliseconds
    */
   public static long markTimer()
   {
      long milliseconds = System.currentTimeMillis();
      if(m_last_event > 0)
      {
         final Logger logger = getLogger(Log4JLogger.class);
         logger.debug(Long.toString((milliseconds - m_last_event)/(long)1000.0) + " seconds since last event.");
         return (milliseconds - m_last_event);
      }
      return 0;
   }

   /**
    * Returns the previous millisecond value of the timer.
    * @return a long number of the millisecond
    */
   public static long resetTimer()
   {
      long _return = m_last_event;
      m_last_event = 0;
      return _return;
   }
}