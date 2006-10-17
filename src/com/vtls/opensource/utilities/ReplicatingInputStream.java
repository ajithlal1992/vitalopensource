package com.vtls.opensource.utilities;

import java.io.InputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * This class performs the filtered replication for
 * a givens source input stream.
 */
public class ReplicatingInputStream extends FilterInputStream
{
   private ByteArrayOutputStream m_stream = null;

   private static final String ERROR_NULL_ARGUMENT = "The argument cannot be null.";
   
   /**
    * Class constructor specifying an {@link InputStream}
    * @param _stream  A source {@link InputStream} 
    */
   public ReplicatingInputStream(InputStream _stream)
   {
      super(_stream);

      if(_stream == null)
      {
    	  throw new IllegalArgumentException(ERROR_NULL_ARGUMENT);
      }

      m_stream = new ByteArrayOutputStream();
   }

   /**
    * Reads the next byte of data from the source InputStream. 
    * @return An int of the value byte from 0 to 255, or -1 if no
    *         no more byte to read.
    * @throws  IOException if the source data is not available
    */
   public int read() throws IOException
   {
      int _return = super.read();
      if(_return > -1)
      {
         m_stream.write(_return);
         m_stream.flush();
      }
      else
      {
    	  m_stream.close();
      }
      return _return;
   }
   
   /**
    * Reads up to a specific length of bytes from the source InputStream 
    * into an array of bytes. 
    * @param b  An byte array as a buffer into which the data is read.
    * @param off  An int of the start offset of the data.
    * @param len  The maximum number of bytes read. 
    * @return The total number of bytes read into the buffer, or -1 if 
    *         there is no more data in the source stream. 
    * @throws  IOException if the source data is not available
    */
   public int read(byte[] b, int off, int len) throws IOException
   {
      int _return = super.read(b, off, len);
      if(_return > 0)
      {
         m_stream.write(b, off, _return);
         m_stream.flush();
      }
      else
         m_stream.close();
      return _return;
   }

   /**
    * Always return false.?????
    * @return False
    */
   public boolean markSupported()
   {
      return false;
   }
   
   /**
    * Get this replicated input stream
    * @return the replicated input stream
    */
   public InputStream getInputStream()
   {
      return (new ByteArrayInputStream(m_stream.toByteArray()));
   }
}