package com.vtls.opensource.formats;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import jj2000.j2k.fileformat.reader.FileFormatReader;
import jj2000.j2k.codestream.reader.HeaderDecoder;
import jj2000.j2k.codestream.HeaderInfo;
import jj2000.j2k.util.ParameterList;
import jj2000.j2k.io.RandomAccessIO;
import jj2000.j2k.util.ISRandomAccessIO;
import jj2000.j2k.decoder.DecoderSpecs;

/**
 * This class is a collection of properties for a file of a valid JPEG 2000 codestream.
 */
public class JPEG2000Properties extends Properties implements FormatProperties
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(JPEG2000Properties.class);
   
   /**
    * Class constructor specifying the source inputstream. Gets the useful
    * information from the header and decodes it, and feeds the mapped properties.
    * @param _stream a {@link InputStream} of a JPEG 2000 codestream 
    */
   public JPEG2000Properties(InputStream _stream) throws IOException
   {
      // Create an instance of the RandomAccessIO interface to manage the
      // JPEG2000 image stream.
      RandomAccessIO stream_io = new ISRandomAccessIO(_stream);

      // The FileFormatReader strips away the container for the codestreams.
      // If we're dealing with a JP2 image, we can just seek ahead.
      FileFormatReader format_reader = new FileFormatReader(stream_io);
      format_reader.readFileFormat();
      if(format_reader.JP2FFUsed)
      {
    	  stream_io.seek(format_reader.getFirstCodeStreamPos());
      }

      // The useful information is encoded in the header which will need 
      // decoding. The HeaderDecoder and the HeaderInfo instances will
      // have different knowledge about the image.
      HeaderDecoder decoder = null;
      HeaderInfo header_info = new HeaderInfo();
      try
      {
         decoder = new HeaderDecoder(stream_io, new ParameterList(), header_info);
      }
      catch(java.io.EOFException e)
      {
         return;
      }
      
      int components = decoder.getNumComps();
      setProperty(FormatProperties.Components, String.valueOf(components));
      setProperty(FormatProperties.Tiles, String.valueOf(header_info.siz.getNumTiles()));

      DecoderSpecs decoder_specs = decoder.getDecoderSpecs();
      int resolution_levels = Integer.parseInt(decoder_specs.dls.getDefault().toString());
      setProperty(FormatProperties.ResolutionLevels, String.valueOf((resolution_levels + 1)));
      setProperty(FormatProperties.QualityLevels, String.valueOf(decoder_specs.nls.getDefault()));

      if(components > 0)
      {
         setProperty(FormatProperties.Width, String.valueOf(header_info.siz.getCompImgWidth(0)));
         setProperty(FormatProperties.Height, String.valueOf(header_info.siz.getCompImgHeight(0)));
         setProperty(FormatProperties.BitDepth, String.valueOf(header_info.siz.getOrigBitDepth(0)));

         StringBuffer dimensions = new StringBuffer();
         dimensions.append(this.getProperty(FormatProperties.Width)).append("x").append(this.getProperty(FormatProperties.Height));
         setProperty(FormatProperties.Dimensions, dimensions.toString());
      }

      stream_io.close();
      // m_logger.debug(header_info.siz.toString());
   }
}
