package testing.com.vtls.opensource.cql;

import java.io.IOException;

import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLParseException;

import com.vtls.opensource.cql.CQLLuceneBridge;

import junit.framework.TestCase;

public class CQLLuceneBridgeTestCase extends TestCase
{
   private CQLLuceneBridge m_lucene_map = null;
   
	public CQLLuceneBridgeTestCase(String name)
	{
		super(name);
	}

	protected void setUp()
	{
	   m_lucene_map = new CQLLuceneBridge();
	   m_lucene_map.put("srw.serverChoice", "Text");
	   m_lucene_map.put("dc.creator", "creator");
	}

	protected void tearDown()
	{
	}

	public void testGetLuceneQuery() throws CQLParseException, IOException
	{
   	CQLParser parser = new CQLParser();
   	String cql_query = "\"feathered dinosaur\" and (yixian or jehol) ";
   	String lucene_query = m_lucene_map.getLuceneQuery(parser.parse(cql_query));
      assertEquals("(Text:\"feathered dinosaur\" AND (Text:yixian OR Text:jehol))", lucene_query);
	}

	public static void main(String args[])
	{
		junit.textui.TestRunner.run(CQLLuceneBridgeTestCase.class);
	}
}
