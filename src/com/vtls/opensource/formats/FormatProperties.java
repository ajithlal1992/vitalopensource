package com.vtls.opensource.formats;

import java.util.Enumeration;
import java.util.Map;

/**
 * This interface provides the property names and functions
 * for Property extended classes representing the useful
 * information for files in different formats. 
 */
public interface FormatProperties
{
   public static final String AspectRatio = "AspectRatio";
   public static final String Author = "Author";
   public static final String BitDepth = "BitDepth";
   public static final String BitsPerPixel = "BitsPerPixel";
   public static final String Channels = "Channels";
   public static final String Colorspace = "Colorspace";
   public static final String Comments = "Comments";
   public static final String Components = "Components";
   public static final String Compression = "Compression";
   public static final String ContentCreator = "Creator";
   public static final String CreationDate = "CreationDate";
   public static final String DateTime = "DateTime";
   public static final String Description = "Description";
   public static final String Dimensions = "Dimensions";
   public static final String Encoding = "Encoding";
   public static final String Encrypted = "Encrypted";
   public static final String Endianness = "Endianness";
   public static final String Errors = "Errors";
   public static final String EXIFVersion = "EXIFVersion";
   public static final String ExposureBias = "ExposureBias";
   public static final String ExposureProgram = "ExposureProgram";
   public static final String ExposureTime = "ExposureTime";
   public static final String FileSource = "FileSource";
   public static final String Flash = "Flash";
   public static final String FNumber = "FNumber";
   public static final String FocalLength = "FocalLength";
   public static final String Format = "Format";
   public static final String FrameLength = "FrameLength";
   public static final String FrameRate = "FrameRate";
   public static final String FrameSize = "FrameSize";
   public static final String Height = "Height";
   public static final String ISOSpeed = "ISOSpeed";
   public static final String Keywords = "Keywords";
   public static final String LightSource = "LightSource";
   public static final String Make = "Model";
   public static final String MaxAperture = "MaxAperture";
   public static final String Metadata = "Metadata";
   public static final String Metering = "Metering";
   public static final String MIMEType = "MIMEType";
   public static final String Model = "Model";
   public static final String ModificationDate = "ModDate";
   public static final String NamespaceURI = "NamespaceURI";
   public static final String NumberOfImages = "NumberOfImages";
   public static final String OperatingSystem = "OperatingSystem";
   public static final String Pages = "Pages";
   public static final String Paragraphs = "Paragraphs";
   public static final String PDFProducer = "Producer";
   public static final String PhysicalDimensions = "PhysicalDimensions";
   public static final String Progression = "Progression";
   public static final String Progressive = "Progressive";
   public static final String QualityLevels = "QualityLevels";
   public static final String Resolution = "Resolution";
   public static final String ResolutionLevels = "ResolutionLevels";
   public static final String Revisions = "Revisions";
   public static final String RootElement = "RootElement";
   public static final String SampleRate = "SampleRate";
   public static final String SampleSize = "SampleSize";
   public static final String SceneType = "SceneType";
   public static final String Signed = "Signed";
   public static final String Size = "Size";
   public static final String Software = "Software";
   public static final String Tampered = "Tampered";
   public static final String Tiles = "Tiles";
   public static final String Title = "Title";
   public static final String Type = "Type";
   public static final String Version = "Version";
   public static final String Width = "Width";
   public static final String Words = "Words";
   
   /**
    * Gets a format property
    * @param _property the name of the format property
    * @return a string of the property value 
    */
   public String getProperty(String _property);
   
   /**
    * Sets a value to a format property
    * @param _property the name of the format property
    * @param _value a specific value for the property
    * @return the previous value of the specified property, or null 
    *         if it did not have one
    */  
   public Object setProperty(String _property, String _value);
   
   /**
    * Returns the number of format properties.
    * @return the number of format properties
    */
   public int size();
   
   /**
    * Return true if a specific property exists in the collection of 
    * format properties, otherwise false.
    * @return true if a property exists in the properties hashtable.
    */
   public boolean contains(Object _property);
   
   /**
    * Get a enumeration of the format property names.
    * @return a {@link Enumeration}
    */
   public Enumeration propertyNames();
   
   /**
    * Remove all format properties.
    */
   public void clear();
}
