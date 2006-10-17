package com.vtls.opensource.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
/**
 * A collection of utility functions for Date. 
 */
public class DateUtilities
{
	/**
	 * Convert a string to a specific format {@link Date}
	 * @param _date A string representing a date
	 * @param _format  A string of the date format
	 * @return  a {@link Date} 
	 */
	public static Date getDateFromString(String _date, String _format)
	{
		// Parse the String into a Date object.
		Date _return = null;

		// Check for null.
		if(_date == null)
		{
			return _return;
		}

		// Try to parse what we have into a legitimate Date.
		try
		{
			SimpleDateFormat date_format = new SimpleDateFormat(_format);
			_return = date_format.parse(_date);
		}
		catch(java.text.ParseException e)
		{
			e.printStackTrace();
		}
		// Return the Date object.
		return _return;
	}

	/**
	 * Get a date as a String in a specific format if the date is not null.
	 * @param _date  A {@link Date}
	 * @param _format A specific string of the date format
	 * @return  A String representing the date in a certain format
	 */
	public static String getFormattedDate(Date _date, String _format)
	{
		// Return the date as a String if the date is not null.
		SimpleDateFormat date_format = new SimpleDateFormat(_format);
		return (_date != null) ? date_format.format(_date) : "";
	}

	/**
	 * Converts a GMT Date to a local Date at that specific date.
	 * @param _date A GMT {@link Date}
	 * @return A {@link Date} representing the local date.
	 */
	public static Date convertGMTtoLocal(Date _date) {
		if(_date == null) {
			return null;
		}
		//Get the GMT-offset for this timezone at that date.
		int offset = TimeZone.getDefault().getOffset(_date.getTime());
		//Calculate and return the new Date by adding the offset.
		return new Date(_date.getTime() + new Long(offset).longValue());
	}
}