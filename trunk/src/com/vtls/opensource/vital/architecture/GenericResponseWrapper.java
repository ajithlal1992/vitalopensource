package com.vtls.opensource.vital.architecture;
     
import javax.servlet.ServletOutputStream; 
import javax.servlet.http.HttpServletResponse; 
import javax.servlet.http.HttpServletResponseWrapper; 
import java.io.ByteArrayOutputStream; 
import java.io.PrintWriter; 

/**
 * Credit: http://www.orionserver.com/tutorials/filters/
 */
public class GenericResponseWrapper extends HttpServletResponseWrapper
{ 
   private ByteArrayOutputStream output; 
   private int contentLength; 
   private String contentType; 

   public GenericResponseWrapper(HttpServletResponse response)
   { 
      super(response); 
      output = new ByteArrayOutputStream(); 
   } 
   
   public byte[] getData()
   { 
      return output.toByteArray(); 
   } 
   
   public ServletOutputStream getOutputStream()
   { 
      return new FilterServletOutputStream(output); 
   } 
   
   public void setContentLength(int length)
   { 
      this.contentLength = length; 
      super.setContentLength(length); 
   } 
   
   public int getContentLength()
   { 
      return contentLength; 
   } 
   
   public void setContentType(String type)
   { 
      this.contentType = type; 
      super.setContentType(type); 
   } 
   
   public String getContentType()
   { 
      return contentType; 
   } 
   
   public PrintWriter getWriter()
   { 
      return new PrintWriter(getOutputStream(), true); 
   } 
} 
