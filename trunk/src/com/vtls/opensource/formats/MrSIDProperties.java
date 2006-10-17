package com.vtls.opensource.formats;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;
/**
 * This class is a collection of properties for a MrSID file.
 */
public class MrSIDProperties extends Properties implements FormatProperties
{
	// Log4j instance.
	private static final Logger m_logger = Log4JLogger.getLogger(MrSIDProperties.class);

	private static final byte[] SIGNATURE = {0x6d, 0x73, 0x69, 0x64};
	private static final int SIGNATURE_OFFSET = 0;

	/**
	 * Class constructor specifying the {@link InputStream}.
	 * Gets the useful information of the given MrSID file and sets 
	 * the mapped properties.
	 * @param _stream an {@link InputStream} of the source MrSID file 
	 * @throws IOException 
	 */
	public MrSIDProperties(InputStream _stream) throws IOException
	{
		byte[] header = new byte[32];
		int bytes = _stream.read(header);

		if(bytes != header.length && !MrSIDProperties.verifySignature(header))
		{
			return;
		}

		setProperty(FormatProperties.Format, "MrSID Image");
		setProperty(FormatProperties.BitsPerPixel, String.valueOf(getIntBigEndian(header, 7)));
		setProperty(FormatProperties.ResolutionLevels, String.valueOf(getIntBigEndian(header, 11)));
		setProperty(FormatProperties.Width, String.valueOf(getIntBigEndian(header, 15)));
		setProperty(FormatProperties.Height, String.valueOf(getIntBigEndian(header, 19)));

		StringBuffer dimensions = new StringBuffer();
		dimensions.append(getProperty(FormatProperties.Width)).append("x");
		dimensions.append(getProperty(FormatProperties.Height));
		setProperty(FormatProperties.Dimensions, dimensions.toString());
		setProperty(FormatProperties.Description, dimensions.toString());

		_stream.close();
	}

	/**
	 * Verfies the byte signature of MrSID files.
	 * @param bytes The byte signature.
	 * @return Whether the signature was verified or not.
	 */
	private static boolean verifySignature(byte[] bytes)
	{
		int signature_offset = SIGNATURE_OFFSET;
		int signature_length = SIGNATURE.length;
		int offset = 0;
		while(signature_length-- > 0)
		{
			if(bytes[offset++] != SIGNATURE[signature_offset++])
			{
				return false;
			}
		}

		return true;
	}

	///////////////////////////////////////////////////////////////////////////
	// Credit: Marco Schmidt (http://schmidt.devlib.org/image-info/) //////////
	/**
	 * Returns resolution in pixels.
	 * @param a The header of the MrSID file.
	 * @param x The starting location of the information.
	 * @return The resolution in pixels.
	 */
	private static int getIntBigEndian(byte[] a, int x)
	{
		return (a[x] & 0xff) << 24 | (a[x + 1] & 0xff) << 16 | (a[x + 2] & 0xff) << 8 | a[x + 3] & 0xff;
	}

	/**
	 * Returns resolution in pixels.
	 * @param a The header of the MrSID file.
	 * @param x The starting location of the information.
	 * @return The resolution in pixels.
	 */
	private static int getIntLittleEndian(byte[] a, int x)
	{
		return (a[x + 3] & 0xff) << 24 | (a[x + 2] & 0xff) << 16 | (a[x + 1] & 0xff) << 8 | a[x] & 0xff;
	}
}
