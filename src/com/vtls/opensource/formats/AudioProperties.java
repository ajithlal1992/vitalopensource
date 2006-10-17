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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
/**
 * This class is a collection of properties for a valid Audio file.
 */
public class AudioProperties extends Properties implements FormatProperties
{
	// Log4j instance.
	private static final Logger m_logger = Log4JLogger.getLogger(AudioProperties.class);
	
   /**
    * Class constructor specifying the source codestream and the file extention
    * and sets the useful information to the mapped properties
    * @param _stream the codestream of the source audio file
    * @throws IOException if an input or output error occurs
    */
	public AudioProperties(InputStream _stream) throws IOException
	{
		File file = File.createTempFile("vtls-audio-", "");
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

		setProperty(FormatProperties.Size, String.valueOf(file.length()));

		// Get an AudioInputStream from the file.
		try
		{
			AudioFileFormat audio_format = AudioSystem.getAudioFileFormat(file); 
			setProperty(FormatProperties.FrameLength, String.valueOf(audio_format.getFrameLength()));
			setProperty(FormatProperties.Type, audio_format.getType().toString());

			AudioFormat format = audio_format.getFormat();
			if(format != null)
			{
				setProperty(FormatProperties.Description, format.toString());
				setProperty(FormatProperties.Channels, String.valueOf(format.getChannels()));
				setProperty(FormatProperties.Encoding, format.getEncoding().toString());
				setProperty(FormatProperties.FrameRate, String.valueOf(format.getFrameRate()));
				setProperty(FormatProperties.FrameSize, String.valueOf(format.getFrameSize()));
				setProperty(FormatProperties.SampleRate, String.valueOf(format.getSampleRate()));
				setProperty(FormatProperties.SampleSize, String.valueOf(format.getSampleSizeInBits()));
				setProperty(FormatProperties.Endianness, (format.isBigEndian()) ? "big-endian" : "little-endian");
			}
		}
		catch(UnsupportedAudioFileException e)
		{
		}

		file.delete();
	}
}