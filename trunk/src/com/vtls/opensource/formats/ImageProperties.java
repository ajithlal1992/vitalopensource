package com.vtls.opensource.formats;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import org.devlib.schmidt.imageinfo.ImageInfo;

/**
 * This class is a collection of properties for a valid Image file.
 */
public class ImageProperties extends Properties implements FormatProperties
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(ImageProperties.class);
   
   /**
    * Class constructor specifying the source codestream. Gets the 
    * useful information from the source stream and sets the values 
    * to the mapped properties.
    * @param _stream a {@link InputStream} of a Image codestream 
    * @throws IOException if an input or output error occurs
    */
   public ImageProperties(InputStream _stream) throws IOException
   {
      ImageInfo ii = new ImageInfo();
      ii.setInput(_stream);
      ii.setDetermineImageNumber(true);

      if(ii.check())
      {
         setProperty(FormatProperties.Format, ii.getFormatName());
         setProperty(FormatProperties.Width, String.valueOf(ii.getWidth()));
         setProperty(FormatProperties.Height, String.valueOf(ii.getHeight()));
         setProperty(FormatProperties.BitsPerPixel, String.valueOf(ii.getBitsPerPixel()));
         setProperty(FormatProperties.NumberOfImages, String.valueOf(ii.getNumberOfImages()));
         setProperty(FormatProperties.Progressive, (ii.isProgressive()) ? "true" : " false");
         setProperty(FormatProperties.MIMEType, ii.getMimeType());

         if(ii.getPhysicalWidthDpi() > 0 && ii.getPhysicalHeightDpi() > 0)
         {
            setProperty(FormatProperties.Resolution, String.valueOf(ii.getPhysicalWidthDpi()));
            StringBuffer dimensions = new StringBuffer();
            dimensions.append(String.valueOf(ii.getPhysicalWidthInch())).append("\" x ");
            dimensions.append(String.valueOf(ii.getPhysicalHeightInch())).append("\"");
            setProperty(FormatProperties.PhysicalDimensions, dimensions.toString());
         }

         StringBuffer description = new StringBuffer();
         description.append(this.getProperty(FormatProperties.Width)).append("x").append(this.getProperty(FormatProperties.Height));
         setProperty(FormatProperties.Description, description.toString());
         setProperty(FormatProperties.Dimensions, description.toString());
      }
      
      /*
         ImageIOImageSource source = new ImageIOImageSource(_stream);
         BufferedImage image = source.getImage();
         this.processImage(image);
      */
   }

   /**
    * Class constructor specifying a {@link BufferedImage}. Gets the 
    * useful information from BufferedImage and sets the values 
    * to the mapped properties.
    * @param _image a {@link BufferedImage}
    * @throws IOException if an input or output error occurs
    */
   public ImageProperties(BufferedImage _image) throws IOException
   {
      processImage(_image);
   }
   
   /** 
    * Processes a BufferedImage and sets the useful information
    * to the mapped properties.
    * @param _image a {@link BufferedImage}
    */
   protected void processImage(BufferedImage _image)
   {
      if(_image == null)
      {
    	  return;
      }
      
      setProperty(FormatProperties.Width, String.valueOf(_image.getWidth()));
      setProperty(FormatProperties.Height, String.valueOf(_image.getHeight()));

      ColorModel color_model = _image.getColorModel();
      setProperty(FormatProperties.BitsPerPixel, String.valueOf(color_model.getPixelSize()));
      setProperty(FormatProperties.Components, String.valueOf(color_model.getNumComponents()));
   }
}
