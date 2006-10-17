package com.vtls.opensource.handle;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import net.handle.hdllib.HandleException;
import net.handle.hdllib.HandleResolver;
import net.handle.hdllib.HandleValue;

public class HandleUtilities
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(HandleUtilities.class);

   /**
    * Resolves a handle then does nothing with it.
    * @param _handle The handle to resolve.
    * @throws HandleException If something goes wrong.
    */
   public static void resolveHandle(String _handle) throws HandleException
   {
      HandleResolver resolver = new HandleResolver();
      HandleValue[] values = resolver.resolveHandle(_handle);
      
      for(int i = 0; i < values.length; i++)
      {
         m_logger.info(values[i].toString());
         m_logger.info(String.valueOf(values[i].getIndex()));
         m_logger.info(values[i].getTypeAsString());
         
         m_logger.info(new String(values[i].getData()));
         m_logger.info(values[i].getDataAsString());
      }
   }
}