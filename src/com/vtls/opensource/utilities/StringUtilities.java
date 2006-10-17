package com.vtls.opensource.utilities;

/**
 * A collection of utility functions for String handling.
 */
public class StringUtilities
{
	/**
	 * Justify a String given some padding character.
	 */
	public static String justify(String _string, int _length, char _character, boolean _right)
	{
		StringBuffer _return = new StringBuffer(_string);
		while(_return.length() < _length)
		{
			if(_right)
				_return.insert(0, _character);
			else
				_return.append(_character);
		}

		return _return.toString();
	}

	/**
	 * Simple find and replace.
	 * @param _string The {@link String} to work with.
	 * @param _search What to find.
	 * @param _replace What to replace it with.
	 */
	public static String simpleReplace(final String _string, final String _search, final String _replace)
	{
		final StringBuffer _return = new StringBuffer();

		if(_string == null || _search == null || _search.equals(""))
			return _string;

		int position = 0;
		int lastPosition = 0;
		while ((lastPosition = _string.indexOf(_search, position)) >= 0)
		{
			_return.append(_string.substring(position, lastPosition));
			_return.append(_replace);
			position = lastPosition + _search.length();
		}

		_return.append(_string.substring(position));
		return _return.toString();
	}
}