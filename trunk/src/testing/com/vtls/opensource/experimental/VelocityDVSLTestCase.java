package testing.com.vtls.opensource.experimental;

import junit.framework.TestCase;
import org.apache.tools.dvsl.DVSL;

import java.io.StringReader;
import java.io.StringWriter;

import org.apache.velocity.context.Context;
import org.apache.velocity.VelocityContext;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
  
public class VelocityDVSLTestCase extends TestCase
{
   private static final String DVSL_STYLESHEET = "#match(\"element\")Hello from $element! $node.value()#end";
   private static final String DVSL_SOURCE = "<?xml version=\"1.0\"?><document><element>Foo</element></document>";

	public VelocityDVSLTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	   /* Note: Converting DOM Document.
	   org.dom4j.Document doc = ...;
      org.w3c.dom.Document d = new org.dom4j.io.DOMWriter().write(doc);
      */
	}

	protected void tearDown()
	{
	}

	public void testTransform() throws DocumentException, Exception
	{
	   Context context = new VelocityContext();
	   context.put("element", "testing");
	   
      DVSL dvsl = new DVSL();
      dvsl.setStylesheet(new StringReader(DVSL_STYLESHEET));
      dvsl.setUserContext(context);

      SAXReader sax_reader = new SAXReader();
      Document document = sax_reader.read(new StringReader(DVSL_SOURCE));

      StringWriter string_writer = new StringWriter();
      dvsl.transform(document, string_writer);
      assertEquals(string_writer.toString(), "Hello from testing! Foo");
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(VelocityDVSLTestCase.class);
	}
}
