package testing.com.vtls.opensource.fedora;

import java.net.URL;
import java.util.Iterator;

import junit.framework.TestCase;
import com.vtls.opensource.fedora.FedoraObjectProxy;
import com.vtls.opensource.fedora.FedoraResourceIndex;
import com.vtls.opensource.fedora.FedoraSPARQLHandler;

public class FedoraResourceIndexTestCase extends TestCase
{
   private FedoraResourceIndex m_resource_index = null;
   private static final String m_host = "localhost";
   private static final int m_port = 8080;
   
	public FedoraResourceIndexTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	   m_resource_index = new FedoraResourceIndex(m_host, m_port);
	}

	protected void tearDown()
	{
	   m_resource_index = null;
	}

	public void testGetURLForQuery() throws Exception
	{
	   URL url = m_resource_index.getURLForQuery("select $object from <#ri> where $object <fedora-model:owner> 'fedoraAdmin'", "CSV");
		assertEquals(url, new URL("http://localhost:8080/fedora/risearch?type=tuples&lang=itql&format=CSV&distinct=on&dt=on&flush=true&limit=&query=select+%24object+from+%3C%23ri%3E+where+%24object+%3Cfedora-model%3Aowner%3E+%27fedoraAdmin%27"));
	}
	
	public void testParse() throws Exception
	{
	   FedoraSPARQLHandler handler = new FedoraSPARQLHandler();
	   final String query = "select $object $mDate " +
         "subquery ( select $title from <#ri> where $object <dc:title> $title order by $title limit 1 ) " +
         "subquery ( select $creator from <#ri> where $object <dc:creator> $creator limit 1 ) " +
         "subquery ( select $description from <#ri> where $object <dc:description> $description limit 1 ) " +
         "from <#ri> " +
         "where $object <rdf:type> <fedora-model:FedoraObject> " +
         "and $object <fedora-view:lastModifiedDate> $mDate " +
         "order by $mDate desc " +
         "limit 10";

	   m_resource_index.parse(query, handler);
	
      Iterator i = handler.getList().iterator();
      while(i.hasNext())
      {
         FedoraObjectProxy object = (FedoraObjectProxy)i.next();
         assertNotNull(object);
      }
   }

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(FedoraResourceIndexTestCase.class);
	}
}
