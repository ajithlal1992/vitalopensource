package com.vtls.opensource.cql;

/**
 * Copyright (c) 2000-2006 OCLC Online Computer Library Center,
 * Inc. and other contributors. All rights reserved.  The contents of this
 * file, as updated from time to time by the OCLC Office of Research, are
 * subject to OCLC Research Public License Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may obtain
 * a current copy of the License at http://purl.oclc.org/oclc/research/ORPL/.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.  This software consists of voluntary contributions made by many
 * individuals on behalf of OCLC Research. For more information on OCLC
 * Research, please see http://www.oclc.org/oclc/research/.
 *
 * The Original Code is __SRWLuceneDatabase.java_.
 * The Initial Developer of the Original Code is __Ralph LeVan__.
 *
 * Portions created by VTLS Inc. are
 * Copyright (C) 2006. All Rights Reserved.
 * Contributor(s): Shad Gilley, Joseph Liversedge.
 */
 
import java.util.HashMap;
import org.z3950.zing.cql.CQLAndNode;
import org.z3950.zing.cql.CQLBooleanNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLNotNode;
import org.z3950.zing.cql.CQLOrNode;
import org.z3950.zing.cql.CQLTermNode;

/**
 * Handles the mapping between CQL indexes and a Lucene index. (For example,
 * we might map 'srw.serverChoice' to 'Text'.) We also manage the conversion
 * from the CQL search syntax to Lucene.
 */
public class CQLLuceneBridge extends HashMap
{
   /**
    * Returns the mapped string for a given search index.
    * @param _index A {@link String} representing a search index.
    * @return A string with the mapped index, if any, or the given term if no mappings exist.
    * @todo We may want to refactor this class along with the Lucene code to actually match up the existing fields.
    */
   private String getIndex(String _index)
   {
      String value = (String)get(_index);
      return (value != null) ? value : _index;
   }
   
   /**
    * Obtains a Lucene-style query from a root {@link CQLNode}
    * @param _node a CQLNode
    * @return a string of the Lucene-style query 
    */
   public String getLuceneQuery(CQLNode _node)
   {
      // Set up a StringBuffer for use by the recursive method.
      StringBuffer _return = new StringBuffer();
      getLuceneQuery(_node, _return);
      return _return.toString(); 
   }
   
   /**
    * Recursives function which performs the main work of {@link getLuceneQuery(CQLNode)}.
    * @param _node a CQLNode
    * @param _string_buffer a StringBuffer to contain the converted lucene query
    */
   private void getLuceneQuery(CQLNode _node, StringBuffer _string_buffer)
   {
      if(_node instanceof CQLTermNode)
      {
         CQLTermNode term_node = (CQLTermNode)_node;
         String relation = term_node.getRelation().getBase();

         // Check the relationship.
         // TODO: Ranges, '>'?.
         if(relation.equals("=") || relation.equals("scr"))
         {
            // Get the matching Lucene index.
            String index = getIndex(term_node.getQualifier());
            
            // Append the 'field:term' separator for non-empty indexes.
            if(!index.equals(""))
               _string_buffer.append(index).append(":");

            // Add "quotation marks" for terms with "internal" spaces.
            String term = term_node.getTerm();
            if(term.indexOf(" ") >= 0)
               _string_buffer.append("\"").append(term).append("\"");
            else
               _string_buffer.append(term);
         }
      }
      else if(_node instanceof CQLBooleanNode)
      {
         CQLBooleanNode boolean_node = (CQLBooleanNode)_node;
         
         // Open parenthesis.
         _string_buffer.append("(");

         // Follow the left side of the expression.
         getLuceneQuery(boolean_node.left, _string_buffer);
         
         // Append based on the boolean term.
         if(_node instanceof CQLAndNode)
            _string_buffer.append(" AND ");
         else if(_node instanceof CQLNotNode)
            _string_buffer.append(" NOT ");
         else if(_node instanceof CQLOrNode)
            _string_buffer.append(" OR ");

         // Follow the right side of the expression.
         getLuceneQuery(boolean_node.right, _string_buffer);

         // Open parenthesis.
         _string_buffer.append(")");
      }
   }
}