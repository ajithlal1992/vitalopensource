package com.vtls.opensource.validation;

import java.io.InputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Iterator;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Form;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResources;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResults;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.vtls.opensource.logging.Log4JLogger;

/**
 * This class handles a set of validation results using suitable
 * validation rules and log the validation message information
 */
public class ValidationRules implements ValidationHandler
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(ValidationRules.class);

   private ValidatorResources m_resources = null;
   private ValidationHandler m_handler = null;
   
   /** 
    * Class constructor specifying the validation rules and handler.
    * @param _rules an {@link InputStream} of the validation rules
    * @param _handler a {@link ValidationHandler} to handle the validation event
    * @throws IOException
    * @throws SAXException
    */
   public ValidationRules(InputStream _rules, ValidationHandler _handler) throws IOException, SAXException
   {
      m_resources = new ValidatorResources(_rules);
      m_handler = _handler;
   }
   
   /**
    * Class constructor specifying the validation rules.
    * @param _rules an {@link InputStream} of the validation rules
    * @throws IOException
    * @throws SAXException 
    */
   public ValidationRules(InputStream _rules) throws IOException, SAXException
   {
      m_resources = new ValidatorResources(_rules);
      m_handler = this;
   }
   
   /**
    * Validate a field and return true if the field satisfies the validation rule
    * @param _object a bean object
    * @param _name a string of the name for the validated field
    * @return true if the field satisfies the validation rule, otherwise false
    * @throws ValidatorException if any error occurs during validation
    */
   public boolean validate(Object _object, String _name) throws ValidatorException
   {
      boolean _return = true;

      Validator validator = new Validator(m_resources, _name);
      validator.setParameter(Validator.BEAN_PARAM, _object);

      ValidatorResults results = validator.validate();
      Form form = m_resources.getForm(Locale.getDefault(), _name);

      m_logger.debug("Object: " + _object);

      Iterator property_iterator = results.getPropertyNames().iterator();
      while(property_iterator.hasNext())
      {
         String property_name = (String)property_iterator.next();
         Field field = form.getField(property_name);
         ValidatorResult result = results.getValidatorResult(property_name);

         if(!m_handler.handleValidatorResult(m_resources, result));
            _return = false;
      }
      
      return _return;
   }
   
   /**
    * Go through a set of validation results processed on a JavaBean,
    * log each validation result with the INFO Level, and log the alert
    * message for failded validations
    * @param _resources a {@link ValidatorResources} configured through 
    *                   a validation.xml file
    * @param _result a {@link ValidatorResult} contains a set of validation results
    * @return true if the field satisfies the validation rule, otherwise false
    */  
   public boolean handleValidatorResult(ValidatorResources _resources, ValidatorResult _result)
   {
      boolean _return = true;
      
      Field field = _result.getField();
      
      Iterator iterator = _result.getActions();
      while(iterator.hasNext())
      {
         String name = (String)iterator.next();
         ValidatorAction action = _resources.getValidatorAction(name);

         m_logger.info(field.getKey() + ": " + (_result.isValid(name) ? "[PASSED]" : "[FAILED]"));

         if(!_result.isValid(name))
         {
            _return = false;
            String message = action.getMsg();
            Object[] args = { field.getArg(0).getKey() };
            m_logger.info(field.getKey() + ": " + MessageFormat.format(message, args));
         }
      }
      
      return _return;
   }
}
