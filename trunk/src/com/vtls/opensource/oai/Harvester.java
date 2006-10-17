package com.vtls.opensource.oai;

import java.io.IOException;

import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.JDOMException;

import org.apache.log4j.Logger;
import com.vtls.opensource.logging.Log4JLogger;
import com.vtls.opensource.oai.handlers.Handler;

/**
 * This object will connect to an oai provider and harvest its records.
 * It can harvest an entire repository, records between certain dates, and/or records in certain sets.
 * 
 * @author Chris Hall
 */
public class Harvester {
	/**
	 * The logger.
	 */
	protected static final Logger m_logger = Log4JLogger.getLogger(Harvester.class);
	
	/**
	 * The Provider.
	 */
	protected Provider provider = null;
	
	/**
	 * The Handler which will process the record Elements.
	 */
	protected Handler handler = null;
	
	public Harvester(Handler _handler, String baseURL)
			throws JDOMException, IOException {
		this(_handler, new Provider(baseURL));
	}
	
	/**
	 * Constructor.  Sets the baseURL and grabs and sets up the listener.
	 * 
	 * @param _handler a {@link Handler} to process the record Elements.
	 * @param _provider a {@link Provider} instance.
	 */
	public Harvester(Handler _handler, Provider _provider) {
		this.handler = _handler;
		this.provider = _provider;
	}
	
	/**
	 * Harvests a repository.
	 * 
	 * @param _from Date to start harvesting.
	 * @param _metadataPrefix The prefix of the desired metadata.
	 * @param _until Date to stop harvesting.
	 * @param _set The subset of the repository to harvest.
	 * 
	 * @return The number of records harvested.
	 */
	public boolean harvest(String _from, String _until, String _metadataPrefix, String _set)
			throws IOException, NoSuchFieldException, JDOMException {
		//Inform the handler of the initial start of the harvest.
		if (this.handler.initial()) {
			//Start harvesting.
			Element verb = this.provider.listRecords(_from, _until, _metadataPrefix, _set);
			if (this.provider.hasError()) {
				m_logger.info(provider.getErrorCode() + " - " + provider.getErrorMessage());
				return false;
			}
			while (verb != null) {
				//Inform the handler of the start of a new request.
				if (this.handler.start()) {
					//Iterate through all the records.
					Iterator record_iterator = verb.getChildren("record", verb.getNamespace()).iterator();
					while (record_iterator.hasNext()) {
						Element current_record = (Element) record_iterator.next();
						//Pass a copy of the record Elements to the handler.
						if (handler.handle((Element) current_record.clone())) {
							//Successful handling of record.
						} else {
							//Unsuccessful handling of record.
						}
					}
					
					//Inform the handler of the end of the request.
					if (this.handler.end()) {					
						//Get some more records.
						verb = this.provider.listRecords();
					} else {
						//Handler errored.
						return false;
					}
				}
			}
			//Inform the handler of the end of the harvest.
			if (this.handler.finish()) {
				//Finalization succeeded.
				return true;
			} else {
				//Finalization failed.
				return false;
			}
		} else {
			//Initialization failed.
			return false;
		}
	}
	
}
