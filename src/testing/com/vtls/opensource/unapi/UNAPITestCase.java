package testing.com.vtls.opensource.unapi;

import junit.framework.TestCase;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import com.vtls.opensource.unapi.UNAPI;
import com.vtls.opensource.unapi.UNAPIFormat;

public class UNAPITestCase extends TestCase
{
   private UNAPI m_unapi = null;
   
	public UNAPITestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	   m_unapi = new UNAPI();
	}

	protected void tearDown()
	{
	}
	
	public void testExtractDatastream()
	{
	   String datastream = "<info:fedora/demo:13/DS1>";
      Pattern pattern = Pattern.compile("(\\w+)>$", Pattern.CASE_INSENSITIVE);

      Matcher matcher = pattern.matcher(datastream);
      if(matcher.find())
      {
         datastream = matcher.group(1);
         assertEquals("DS1", datastream);
      }
         
      assertTrue(false);
   }

	public void testSetFormat()
	{
	   assertEquals(0, m_unapi.getFormats().size());

      // Add a shortcut UNAPIFormat.
	   m_unapi.setFormat(new UNAPIFormat("oai_dc", "application/xml").setAttribute(UNAPIFormat.NamespaceURI, "http://www.openarchives.org/OAI/2.0/oai_dc/"));
	   assertEquals(1, m_unapi.getFormats().size());

      // Add a constructed UNAPIFormat.
	   UNAPIFormat mods_format = new UNAPIFormat("mods", "application/xml");
	   mods_format.setAttribute(UNAPIFormat.Docs, "http://www.loc.gov/standards/mods/");
	   m_unapi.setFormat(mods_format);
	   assertEquals(2, m_unapi.getFormats().size());
	}
	
	public void testGetFormat()
	{
	   // Add a sample format.
	   UNAPIFormat mods_format = new UNAPIFormat("mods", "application/xml");
	   m_unapi.setFormat(mods_format);

      // Fetch using the 'getFormat' method and compare.
	   UNAPIFormat test_format = m_unapi.getFormat("mods");
	   assertEquals(mods_format, test_format);
   }

	public void testGetFormats()
	{
	   // Add a few formats.
	   m_unapi.setFormat(new UNAPIFormat("1", "2"));
	   m_unapi.setFormat(new UNAPIFormat("3", "4"));
	   m_unapi.setFormat(new UNAPIFormat("5", "6"));

	   Collection collection = m_unapi.getFormats();
	   assertEquals(3, collection.size());
   }

   public void testToString()
   {
      // Add a couple complete formats.
	   m_unapi.setFormat(new UNAPIFormat("1", "2").setAttribute(UNAPIFormat.Docs, "3").setAttribute(UNAPIFormat.NamespaceURI, "4").setAttribute(UNAPIFormat.SchemaLocation, "5"));
	   m_unapi.setFormat(new UNAPIFormat("6", "7").setAttribute(UNAPIFormat.Docs, "8").setAttribute(UNAPIFormat.NamespaceURI, "9").setAttribute(UNAPIFormat.SchemaLocation, "10"));

      // Condense the String and compare to the known value.
      String string = m_unapi.toString().replaceAll("\\p{Space}", "");
      assertEquals(string, "<formats><format><name>1</name><type>2</type><docs>3</docs><namespace_uri>4</namespace_uri><schema_location>5</schema_location></format><format><name>6</name><type>7</type><docs>8</docs><namespace_uri>9</namespace_uri><schema_location>10</schema_location></format></formats>");
   }
   
   public void testURI()
   {
      // Basic setup with a URI.
	   m_unapi.setFormat(new UNAPIFormat("1", "2"));
	   m_unapi.setURI("info:fedora/demo:12");
      String string = m_unapi.toString().replaceAll("\\p{Space}", "");
      assertEquals(string, "<formats><uri>info:fedora/demo:12</uri><format><name>1</name><type>2</type></format></formats>");
   }
	   
   public void testFormatRedirect()
   {
	   m_unapi.setFormat(new UNAPIFormat("1", "2"));
	   m_unapi.setURI("info:fedora/demo:12");
	   m_unapi.setFormatRedirect("oai_dc", "http://localhost:8080/fedora/get/demo:12/DC");
	   
	   // Here, we have a format set to 'oai_dc', but we don't have a format by
	   // that name. We should get 'null' here.
	   assertNull(m_unapi.getFormatRedirect());
	   m_unapi.setFormat(new UNAPIFormat("oai_dc", "application/xml"));
	   assertNotNull(m_unapi.getFormatRedirect());
   }
	   
	public static void main(String args[])
	{
		junit.textui.TestRunner.run(UNAPITestCase.class);
	}
}
