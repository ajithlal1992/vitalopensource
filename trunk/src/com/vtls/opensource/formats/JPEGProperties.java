package com.vtls.opensource.formats;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.io.InputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.Directory;
import com.drew.metadata.exif.ExifDirectory;
/**
 * This class is a collection of properties for a valid JPEG file.
 */
public class JPEGProperties extends Properties implements FormatProperties
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(JPEGProperties.class);
   
   /**
    * Class constructor specifying the codestream. Reads the metadata
    * from the source stream and sets the tag values to the mapped properties.
    * @param _stream a {@link InputStream} of a JPEG codestream 
    * @throws IOException if an input or output error occurs
    */
   public JPEGProperties(InputStream _stream) throws IOException
   {
      // Source: http://drewnoakes.com/code/exif/sampleUsage.html
      Metadata metadata = null;
      try
      {
         metadata = JpegMetadataReader.readMetadata(_stream);
      }
      catch(com.drew.imaging.jpeg.JpegProcessingException e)
      {
         m_logger.error(e);
         return;
      }

      // Iterate through the metadata directories.
      Iterator directories = metadata.getDirectoryIterator();
      while(directories.hasNext())
      {
         Directory directory = (Directory)directories.next();

         if(directory instanceof ExifDirectory)
         {
            setProperty(FormatProperties.Format, "JPEG");

/*
    [junit]       0x0103|[Exif] Compression - JPEG (old-style) 
    [junit]       0x010f|[Exif] Make - SONY 
    [junit]       0x0110|[Exif] Model - CYBERSHOT 
    [junit]       0x011a|[Exif] X Resolution - 72 dots per inch 
    [junit]       0x0131|[Exif] Software - QuickTime 7.0.3 
    [junit]       0x013c|[Exif] Unknown tag (0x013c) - Mac OS X 10.4.3 
    [junit]       0x829a|[Exif] Exposure Time - 1/500 sec 
    [junit]       0x829d|[Exif] F-Number - F5.6 
    [junit]       0x8822|[Exif] Exposure Program - Program normal 
    [junit]       0x8827|[Exif] ISO Speed Ratings - 100 
    [junit]       0x9000|[Exif] Exif Version - 2.20 
    [junit]       0x9003|[Exif] Date/Time Original - 2005:12:08 03:30:20 
    [junit]       0x9004|[Exif] Date/Time Digitized - 2005:12:08 03:30:20 
    [junit]       0x9204|[Exif] Exposure Bias Value - 0 EV 
    [junit]       0x9205|[Exif] Max Aperture Value - F3.8 
    [junit]       0x9207|[Exif] Metering Mode - Spot 
    [junit]       0x9208|[Exif] Light Source - Unknown 
    [junit]       0x9209|[Exif] Flash - Flash did not fire 
    [junit]       0x920a|[Exif] Focal Length - 6.4 mm 
    [junit]       0xa000|[Exif] FlashPix Version - 1.00 
    [junit]       0xa001|[Exif] Color Space - sRGB 
    [junit]       0xa300|[Exif] File Source - Digital Still Camera (DSC) 
    [junit]       0xa301|[Exif] Scene Type - Directly photographed image 

*/
            try
            {
               if(directory.containsTag(ExifDirectory.TAG_EXIF_IMAGE_WIDTH))
               {
            	   setProperty(FormatProperties.Width, String.valueOf(directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_WIDTH)));
               }
               if(directory.containsTag(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT))
               {
            	   setProperty(FormatProperties.Height, String.valueOf(directory.getInt(ExifDirectory.TAG_EXIF_IMAGE_HEIGHT)));
               }
               if(directory.containsTag(ExifDirectory.TAG_SAMPLES_PER_PIXEL))
               {
            	   setProperty(FormatProperties.BitsPerPixel, String.valueOf(directory.getInt(ExifDirectory.TAG_SAMPLES_PER_PIXEL)));
               }

               StringBuffer dimensions = new StringBuffer();
               dimensions.append(this.getProperty(FormatProperties.Width)).append("x").append(this.getProperty(FormatProperties.Height));
               setProperty(FormatProperties.Dimensions, dimensions.toString());

               if("2".equals(directory.getString(ExifDirectory.TAG_RESOLUTION_UNIT)))
               {
            	   setProperty(FormatProperties.Resolution, directory.getString(ExifDirectory.TAG_X_RESOLUTION) + " dpi");
               }

               setPropertyForTagType(FormatProperties.Description, directory, ExifDirectory.TAG_IMAGE_DESCRIPTION);
               setPropertyForTagType(FormatProperties.Compression, directory, ExifDirectory.TAG_COMPRESSION);
               setPropertyForTagType(FormatProperties.Make, directory, ExifDirectory.TAG_MAKE);
               setPropertyForTagType(FormatProperties.Model, directory, ExifDirectory.TAG_MODEL);
               setPropertyForTagType(FormatProperties.Software, directory, ExifDirectory.TAG_SOFTWARE);
               setPropertyForTagType(FormatProperties.OperatingSystem, directory, 316);
               setPropertyForTagType(FormatProperties.ExposureTime, directory, ExifDirectory.TAG_EXPOSURE_TIME);
               setPropertyForTagType(FormatProperties.FNumber, directory, ExifDirectory.TAG_FNUMBER);
               setPropertyForTagType(FormatProperties.ExposureProgram, directory, ExifDirectory.TAG_EXPOSURE_PROGRAM);
               setPropertyForTagType(FormatProperties.ISOSpeed, directory, ExifDirectory.TAG_ISO_EQUIVALENT);
               setPropertyForTagType(FormatProperties.EXIFVersion, directory, ExifDirectory.TAG_EXIF_VERSION);
               setPropertyForTagType(FormatProperties.DateTime, directory, ExifDirectory.TAG_DATETIME_ORIGINAL);
               setPropertyForTagType(FormatProperties.ExposureBias, directory, ExifDirectory.TAG_EXPOSURE_BIAS);
               setPropertyForTagType(FormatProperties.MaxAperture, directory, ExifDirectory.TAG_MAX_APERTURE);
               setPropertyForTagType(FormatProperties.Metering, directory, ExifDirectory.TAG_METERING_MODE);
               setPropertyForTagType(FormatProperties.LightSource, directory, ExifDirectory.TAG_LIGHT_SOURCE);
               setPropertyForTagType(FormatProperties.Flash, directory, ExifDirectory.TAG_FLASH);
               setPropertyForTagType(FormatProperties.FocalLength, directory, ExifDirectory.TAG_FOCAL_LENGTH);
               setPropertyForTagType(FormatProperties.Colorspace, directory, ExifDirectory.TAG_COLOR_SPACE);
               setPropertyForTagType(FormatProperties.FileSource, directory, ExifDirectory.TAG_FILE_SOURCE);
               setPropertyForTagType(FormatProperties.SceneType, directory, ExifDirectory.TAG_SCENE_TYPE);
            }
            catch(com.drew.metadata.MetadataException e)
            {
               m_logger.error(e);
            }
         }
      }
   }
   
   /**
    * Base class for all Metadata directory types with supporting methods for setting and getting tag values
    * Set a tag values in a specific Metadata directory to a pre-defined property.
    * @param _property a string of the property name
    * @param _directory a metadata {@link Directory}
    * @param _tag_type  an int of the tag type identifier 
    * @throws com.drew.metadata.MetadataException
    */
   private void setPropertyForTagType(String _property, Directory _directory, int _tag_type) throws com.drew.metadata.MetadataException
   {
      if(_directory.containsTag(_tag_type))
      {
         String contents = _directory.getDescription(_tag_type).trim();
         if(!"".equals(contents))
         {
        	 setProperty(_property, contents);
         }
      }
   }
}
