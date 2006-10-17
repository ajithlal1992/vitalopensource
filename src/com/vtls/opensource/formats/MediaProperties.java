package com.vtls.opensource.formats;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

import javax.media.Manager;
import javax.media.Processor;
import javax.media.MediaLocator;
import javax.media.NoProcessorException;
import javax.media.control.TrackControl;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;

import jmapps.util.StateHelper;

/**
 * This class is a collection of properties for Media files, i.e., 
 * video and audio files.
 */
public class MediaProperties extends Properties implements FormatProperties
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(MediaProperties.class);

   /**
    * Class constructor specifying the source codestream and the file extention
    * and sets the useful information to the mapped properties
    * @param _extension the extension of thte source file
    * @param _stream the codestream of the source file
    * @throws IOException if an input or output error occurs
    */
   public MediaProperties(InputStream _stream, String _extension) throws IOException
   {
        File file = File.createTempFile("vtls-video-", "." + _extension);
        file.deleteOnExit();

		BufferedOutputStream output_stream = new BufferedOutputStream(new FileOutputStream(file));
		BufferedInputStream input_stream = new BufferedInputStream(_stream);

		int stream_byte;
		while ((stream_byte = input_stream.read()) != -1)
		{
			output_stream.write(stream_byte);
		}

		output_stream.flush();
		output_stream.close();
		input_stream.close();

        processVideoFile(file);

        file.delete();
   }
   
   /**
    * Class constructor specifying the Media {@link File} and 
    * sets the useful information to the mapped properties
    * @param _file a media {@link File}
    * @throws IOException if an input or output error occurs
    */
   public MediaProperties(File _file) throws IOException
   {
      processVideoFile(_file);
   }
   
   /**
    * Gets the useful information of the given media file and 
    * sets the mapped properties.
    * @param _file a given media {@link File}
    * @throws IOException
    */
   protected void processVideoFile(File _file) throws IOException
   {
      setProperty(FormatProperties.Size, String.valueOf(_file.length()));

      MediaLocator media_locator = new MediaLocator(_file.toURL());
      Processor processor = null;

      try
      {
         // Configure the processor.
         processor = Manager.createProcessor(media_locator);
         processor.realize();
      }
      catch (NoProcessorException npe)
      {
         return;
      }
      
      if(processor == null)
      {
    	  return;
      }

      StateHelper state_helper = new StateHelper(processor);
      if(!state_helper.configure(4000))
      {
    	  return;
      }

      TrackControl track = getVideoTrack(processor);
      if(track != null)
      {
         if(track.getFormat() instanceof VideoFormat)
         {
   	      VideoFormat video_format = (VideoFormat)track.getFormat();
	      
   	      StringBuffer dimensions = new StringBuffer();
   	      dimensions.append(video_format.getSize().width + "x" + video_format.getSize().height);

   	      StringBuffer description = new StringBuffer();
   	      description.append(video_format.getEncoding().toUpperCase());
   	      description.append(", " + dimensions.toString());
   	      description.append(", " + Math.ceil(video_format.getFrameRate()) + "fps");

   	      setProperty(FormatProperties.Description, description.toString());
   	      setProperty(FormatProperties.Dimensions, dimensions.toString());
   	      setProperty(FormatProperties.Encoding, video_format.getEncoding().toString());
   	      setProperty(FormatProperties.Width, String.valueOf(video_format.getSize().width));
   	      setProperty(FormatProperties.Height, String.valueOf(video_format.getSize().height));
   	      setProperty(FormatProperties.FrameRate, String.valueOf(Math.ceil(video_format.getFrameRate())));
   	   }
      }
      else
      {
         track = getAudioTrack(processor);
         if(track == null)
         {
        	 return;
         }

         if(track.getFormat() instanceof AudioFormat)
         {
   	      AudioFormat audio_format = (AudioFormat)track.getFormat();
   	      setProperty(FormatProperties.Encoding, audio_format.getEncoding().toString());
   	      setProperty(FormatProperties.Channels, String.valueOf(audio_format.getChannels()));
   	      setProperty(FormatProperties.FrameRate, String.valueOf(audio_format.getFrameRate()));
   	      setProperty(FormatProperties.FrameSize, String.valueOf(audio_format.getFrameSizeInBits()));
   	      setProperty(FormatProperties.SampleRate, String.valueOf(audio_format.getSampleRate()));
   	      setProperty(FormatProperties.SampleSize, String.valueOf(audio_format.getSampleSizeInBits()));
   	      setProperty(FormatProperties.Signed, (audio_format.getSigned() == AudioFormat.UNSIGNED) ? "true" : "false");
   	      setProperty(FormatProperties.Endianness, (audio_format.getEndian() == AudioFormat.BIG_ENDIAN) ? "big-endian" : "little-endian");
   	     }
      }
      processor.close();
   }
   
   /**
    * Get the encapsulated format information for video data.
    * @param _processor a {@link Processor} to process and control time-based media data
    * @return the video data format information
    */
   private TrackControl getVideoTrack(Processor _processor)
   {
      TrackControl track_controls[] = _processor.getTrackControls();
      if(track_controls == null)
      {
    	  return null;
      }

      for(int i=0; i < track_controls.length; i++)
      {
         if(track_controls[i].getFormat() instanceof VideoFormat)
         {
        	 return track_controls[i];
         }
      }
      return null;
   }

   /**
    * Get the encapsulated format information for audio data.
    * @param _processor a {@link Processor} to process and control time-based media data
    * @return the audio data format information
    */
   private TrackControl getAudioTrack(Processor _processor)
   {
      TrackControl track_controls[] = _processor.getTrackControls();
      if(track_controls == null)
      {
    	  return null;
      }

      for(int i=0; i < track_controls.length; i++)
      {
         if(track_controls[i].getFormat() instanceof AudioFormat)
            return track_controls[i];
      }
      return null;
   }
}