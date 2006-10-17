package com.vtls.opensource.oai.handlers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.File;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.PrintStream;

import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;
import org.jdom.output.Format;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;

/**
 * A reference implementation of a Handler which can easily be extended from.
 * 
 * @author Chris Hall
 */
public class DefaultHandler extends Properties implements Handler {
	/**
	 * The logger.
	 */
	private static final Logger m_logger = Log4JLogger.getLogger(DefaultHandler.class);
	
	/**
	 * Writes the harvested metadata.
	 */
	protected PrintStream out = null;
	
	/**
	 * How many times have we tried to process metadata?
	 */
	protected int attempt = 0;
	
	/**
	 * Keeps count of the records harvested.
	 */
	protected int harvested = 0;
	
	/**
	 * Keeps count of the records to be deleted.
	 */
	protected int delete = 0;
	
	/**
	 * Logs the start time of the program.
	 */
	protected long initial;
	
	/**
	 * Logs the start time of the current interval.
	 */
	protected long interval;
	
	/**
	 * What to divide the time by.
	 */
	protected static double divisor = 1000;
	
	/**
	 * Default constructor.
	 */
	public DefaultHandler() {
		super();
	}
	
	/**
	 * Stores the properties and initializes the output.
	 * 
	 * @param _properties Contains the incoming settings.
	 * @param _out Where the output is written to.
	 */
	public void initialize(Properties _properties, OutputStream _out) {
		this.putAll(_properties);
		this.out = new PrintStream(_out, true);
	}
	
	/**
	 * Records the start time and prints a starting message.
	 */
	public boolean initial() {
		this.initial = new Date().getTime();
		System.out.println("Beginning harvest...");
		return true;
	}
	
	/**
	 * Records the time and prints how many records have been processed.
	 */
	public boolean start() {
		this.interval = new Date().getTime();
		System.out.print(this.attempt + "\t");
		return true;
	}
	
	/**
	 * Extracts and stores all info from the record Element.
	 * 
	 * @param _record The record Element.
	 * 
	 * @return The success of the handle.
	 */
	public boolean handle(Element _record) {
		//Obligatory null check.
		if (_record != null) {
			//We NEED the namespace.		
			Namespace namespace = _record.getNamespace();
			
			//Grab the header.
			Element header = _record.getChild("header", namespace);
			String identifier = null;
			String datestamp = null;
			String status = null;
			String setSpec = null;
			
			//Now extract its contents.
			if (header != null) {
				header = (Element) header.clone();
				identifier = header.getChild("identifier", namespace).getValue();
				datestamp = header.getChild("datestamp", namespace).getValue();
				status = header.getAttributeValue("status");
				setSpec = ((header.getChild("setSpec", namespace) != null)? header.getChild("setSpec", namespace).getValue(): null);
			}
			
			//Grab and copy the contents of the metadata Element.
			Document metadata = null;
			if (_record.getChild("metadata", namespace) != null) {
				metadata = new Document((Element) ((Element) _record.getChild("metadata", namespace).getChildren().get(0)).clone());
			}
			
			//Grab and copy the about Element.
			Element about = _record.getChild("about", namespace);
			if (about != null) {
				about = new Element("about").addContent(about.cloneContent());
			}
			
			//Store all String information for later.
			this.setProperty("last.identifer", identifier);
			this.setProperty("last.datestamp", datestamp);
			if (status != null) {
				this.setProperty("last.status", status);
			} else {
				this.remove("last.status");
			}
			if (status != null) {
				this.setProperty("last.setSpec", setSpec);
			} else {
				this.remove("last.setSpec");
			}
			
			//Pass off execution to the processor for easy extention.
			return this.process(identifier, datestamp, status, setSpec, metadata, about);
		} else {
			return false;
		}
	}
	
	/**
	 * Simply outputs the contents.
	 * 
	 * @param _identifier The identifier of the record.
	 * @param _datestamp The datestamp of the record.
	 * @param _status The status of the record.
	 * @param _setSpec The setSpec of the record.
	 * @param _metadata The metadata of the record.
	 * @param _about The about of the record.
	 * 
	 * @return true if all went well.
	 */
	protected boolean process(String _identifier, String _datestamp, String _status, String _setSpec, Document _metadata, Element _about) {
		System.out.print(".");
		this.attempt++;
		if ("delete".equals(_status)) {
			this.delete++;
		} else {
			this.harvested++;
		}
		this.out.println("Processing [" + _identifier + "]...");
		this.out.println("-------------------------");
		this.out.println("status:\t\t" + ((_status != null)? _status: "[null]"));
		this.out.println("datestamp:\t" + _datestamp);
		this.out.println("setSpec:\t" + ((_setSpec != null)? _setSpec: "[null]"));
		this.out.println();
		this.out.println("metadata:\n" + ((_metadata != null)? new XMLOutputter(Format.getPrettyFormat()).outputString(_metadata).trim(): "[null]"));
		this.out.println();
		this.out.println("about:\n" + ((_about != null)? new XMLOutputter(Format.getPrettyFormat()).outputString(_about).trim(): "[null]"));
		this.out.println("-------------------------");
		this.out.println();
		return true;
	}
	
	/**
	 * Prints the number of records processed and the time it took to process them.
	 */
	public boolean end() {
		long current = new Date().getTime();
		System.out.println("\t" + this.attempt + " in " + ((current - this.interval) / this.divisor) + " seconds (" + ((current - this.initial) / this.divisor) + "sec)");
		return true;
	}
	
	/**
	 * Blank end-of-harvest finalization.
	 */
	public boolean finish() {
		this.out.close();
		System.out.println("Harvest finished.");
		System.out.println("Records harvested:\t" + this.harvested);
		System.out.println("Records to be deleted:\t" + this.delete);
		long current = new Date().getTime();
		System.out.println("Total:\t\t\t" + this.attempt + " (" + ((current - this.initial) / this.divisor) + "sec)");
		System.out.println();
		return true;
	}
}