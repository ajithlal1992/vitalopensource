package com.vtls.opensource.text.xhtml;

import java.io.InputStream;
import java.io.IOException;

import org.apache.ecs.xml.XML;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.model.StyleDescription;
import org.apache.poi.hwpf.model.StyleSheet;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.ListEntry;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.ParagraphProperties;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;

/**
 * An experimental class to get an HTML representation of a Microsoft Word Document.
 */
public class MicrosoftWordXHTMLSource
{
   private HWPFDocument m_document = null;
   
   /**
    * Class constructor specifying a {@link HWPFDocument}
    * @param _document a specific {@link HWPFDocument} containing 
    * 				   Word data structures
    */
	public MicrosoftWordXHTMLSource(HWPFDocument _document)
	{
	   m_document = _document;
	}
	
	/**
	 * Class constructor specifying a {@link InputStream}
	 * @param _input_stream a input stream containing the Word document
	 *                      to be loaded to this HWPFDocument
	 */
	public MicrosoftWordXHTMLSource(InputStream _input_stream) throws IOException
	{
      this(new HWPFDocument(_input_stream));
	}
	
	/**
	 * Gets a {@link XML} document of the XHTML representation 
	 * of the source Microsoft Word Document
	 * @return a {@link XML} document
	 */
   public XML getXHTML() throws IOException
   {
      XML xhtml = new XML("html");
      xhtml.setPrettyPrint(true);

      XML body = new XML("body");
      body.setPrettyPrint(true);

      Range range = m_document.getRange();

      for(int i = 0; i < range.numSections(); i++)
      {
         Section section = range.getSection(i);
         XML section_xml = getXHTML(section);
         
         if(section_xml != null)
         {
        	 body.addElement(section_xml);
         }
      }
      
      xhtml.addElement(body);
      return xhtml;
   }
   
   /**
	 * Gets a {@link XML} document of the XHTML representation 
	 * of a {@link Section} in the source Microsoft Word Document
	 * @param _section a {@link Section} in the word document
	 * @return a {@link XML} document
	 */
   public XML getXHTML(Section _section)
   {
      XML _return = new XML("div");
      _return.setPrettyPrint(true);
      _return.addAttribute("class", "section");
      
      StyleSheet stylesheet = m_document.getStyleSheet();

      for(int i = 0; i < _section.numParagraphs(); i++)
      {
         Paragraph paragraph = _section.getParagraph(i);
         StyleDescription paragraph_style = stylesheet.getStyleDescription(paragraph.getStyleIndex());
               
         String style = paragraph_style.getName();
         if(style.startsWith("Heading") && style.length() >= 9)
         {
            XML heading = getParagraphText(paragraph, "h" + style.substring(8,9));
            if(heading != null)
               _return.addElement(heading);
         }
         // Handle tables.
         else if(paragraph.isInTable())
         {
            XML table = new XML("table");
            table.setPrettyPrint(true);

            XML tr = new XML("tr");
            tr.setPrettyPrint(true);

            while(i < _section.numParagraphs())
            {
               Paragraph local_paragraph = _section.getParagraph(i);
               
               if(!(local_paragraph.isInTable()))
                  break;
               else
               {
                  XML td = getParagraphText(local_paragraph, "td");
                  if(td != null)
                     tr.addElement(td);
               }

               if(local_paragraph.isTableRowEnd())
               {
                  table.addElement(tr);
                  tr = new XML("tr");
               }
               
               i++;
            }
            _return.addElement(table);
         }
         // Handle lists.
         else if(paragraph instanceof ListEntry)
         {
            XML ul = new XML("ul");
            ul.setPrettyPrint(true);
            ul.addAttribute("class", paragraph_style.getName().replaceAll("\\s", ""));
            
            int left_indent = 0;
            while(i < _section.numParagraphs())
            {
               Paragraph local_paragraph = _section.getParagraph(i);
               if(local_paragraph.getIndentFromLeft() > left_indent)
               {
                  left_indent = local_paragraph.getIndentFromLeft();
                  // TODO: Handle different levels of list elements.
               }

               if(local_paragraph.getIndentFromLeft() < left_indent)
               {
                  left_indent = local_paragraph.getIndentFromLeft();
                  // TODO: Handle different levels of list elements.
               }
               
               if(!(local_paragraph instanceof ListEntry))
                  break;

               XML li = getParagraphText(local_paragraph, "li");
               if(li != null)
               {
                  li.addAttribute("left", Integer.toString(local_paragraph.getIndentFromLeft()));
                  ul.addElement(li);
               }

               i++;
            }
            _return.addElement(ul);
         }
         else
         {
            XML content = getParagraphText(paragraph, "p");
            if(content != null)
            {
               content.addAttribute("class", paragraph_style.getName().replaceAll("\\s", ""));
               content.setPrettyPrint(true);
               _return.addElement(content);
            }
         }
      }
      return _return;
   }
   
   /**
	 * Gets a {@link XML} document of the XHTML representation 
	 * of a {@link Paragraph} in the source Microsoft Word Document
	 * @param _paragraph a Paragraph in the source Word document 
	 * @param _name a HTML tag name
	 * @return a {@link XML} document
	 */
   public XML getParagraphText(Paragraph _paragraph, String _name)
   {
      XML _return = new XML(_name);
      
      StyleSheet stylesheet = m_document.getStyleSheet();
      // TODO: ParagraphProperties paragraph_properties = stylesheet.getParagraphStyle(_paragraph.getStyleIndex());
      // TODO: switch(paragraph_properties.getJustification())

      StringBuffer text_buffer = new StringBuffer();
      for(int i = 0; i < _paragraph.numCharacterRuns(); i++)
      {
         CharacterRun character_run = _paragraph.getCharacterRun(i);
         text_buffer.append(character_run.text());
         
         if(character_run.isMarkedDeleted());
         else if(character_run.isFldVanished());
         else if(character_run.isItalic())
            _return.addElement(new XML("em").addElement(character_run.text().trim()));
         else if(character_run.isBold())
            _return.addElement(new XML("strong").addElement(character_run.text().trim()));
         else if(character_run.isHighlighted())
            _return.addElement(new XML("em").addElement(character_run.text().trim()).addAttribute("class", "highlight"));
         else
            _return.addElement(character_run.text().replaceAll("\\r", ""));
      }
   
      if(text_buffer.toString().trim().length() > 0)
      {
    	  return _return;
      }
      else
      {
    	  return null;
      }
   }
}
