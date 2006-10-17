package com.vtls.opensource.validation;

import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.util.ValidatorUtils;

/**
 * This class offers two validation functions to validate
 * the required field and email field.
 */
public class Validator
{
	/**
	 * Checks if a field is a valid regex.
	 * @param _object a bean object
	 * @param _field a specific field containing the property
	 * @return true if the required property is a regex,
	 *              otherwise false
	 */
	public static boolean validateRegex(Object _object, Field _field)
	{
		String string = ValidatorUtils.getValueAsString(_object, _field.getProperty());
		String expression = _field.getVarValue("expression");
		return GenericValidator.matchRegexp(string, expression);
	}

	/**
	 * Checks if a field has a non-blank value.
	 * @param _object a bean object
	 * @param _field a specific field containing the property
	 * @return true if the required property is not null or blank,
	 *              otherwise false
	 */
	public static boolean validateRequired(Object _object, Field _field)
	{
		String string = ValidatorUtils.getValueAsString(_object, _field.getProperty());
		return !GenericValidator.isBlankOrNull(string);
	}

	/**
	 * Checks if a field has a valid e-mail address.
	 * @param _object a bean object
	 * @param _field a specific field containing the property
	 * @return true if the field has a valid email address,
	 *              otherwise false
	 */
	public static boolean validateEmail(Object _object, Field _field)
	{
		String string = ValidatorUtils.getValueAsString(_object, _field.getProperty());
		return GenericValidator.isEmail(string);
	}
}