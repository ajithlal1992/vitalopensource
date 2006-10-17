package testing.com.vtls.opensource.validation;

import junit.framework.TestCase;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.ValidatorAction;
import org.apache.commons.validator.ValidatorException;
import org.apache.commons.validator.ValidatorResult;
import org.apache.commons.validator.ValidatorResources;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.vtls.opensource.validation.ValidationHandler;
import com.vtls.opensource.validation.ValidationRules;

import com.vtls.opensource.logging.Log4JLogger;

public class ValidationTestCase extends TestCase implements ValidationHandler
{
   // Log4j instance.
   private static final Logger m_logger = Log4JLogger.getLogger(ValidationRules.class);

	public ValidationTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	}

	protected void tearDown()
	{
	}

	public void testAssert() throws FileNotFoundException, ValidatorException, IOException, SAXException
	{
	   Properties properties = new Properties();
	   properties.setProperty("name", "Username");
	   properties.setProperty("email", "1-800-323-2121");
	   
	   // Production: ClassLoader.getSystemResourceAsStream("validation/validation.xml")
	   ValidationRules rules = new ValidationRules(new FileInputStream("src/testing/data/validation.xml"), this);
	   assertFalse(rules.validate(properties, "testForm"));

	   properties.setProperty("emailAddress", "user@hostname.com");
	   assertTrue(rules.validate(properties, "userRegistration"));

		assertTrue(true);
	}

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
      }
      
      return _return;
   }

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(ValidationTestCase.class);
	}
}
