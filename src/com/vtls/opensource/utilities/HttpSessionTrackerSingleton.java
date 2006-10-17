package com.vtls.opensource.utilities;

import javax.servlet.http.HttpSession;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class HttpSessionTrackerSingleton extends LinkedList
{
	private static HttpSessionTrackerSingleton m_instance = null;

	private PreservingHashMap m_sessions = null;

	/**
	 * Private constructor.  Only used then no other {@link HttpSessionTrackerSingleton} exists in memory.
	 */
	private HttpSessionTrackerSingleton() {
		m_sessions = new PreservingHashMap();
	}

	/**
	 * Makes sure no other {@link HttpSessionTrackerSingleton} exists in memory.
	 */
	public static synchronized HttpSessionTrackerSingleton getInstance() {
		if(m_instance == null) {
			m_instance = new HttpSessionTrackerSingleton();
		}
		return m_instance;
	}

	/**
	 * Gets all sessions associated with an action.
	 * @param _action The action to look for.
	 * @return A {@link List} containing the sessions.
	 */
	public List get(String _action) {
		return (List) m_sessions.get(_action);
	}

	/**
	 * Stores a session based on its action.
	 * @param _action The action.
	 * @param _session The {@link HttpSession} to store.
	 */
	public void put(String _action, HttpSession _session) {
		m_sessions.put(_action, _session);
	}
	
	/**
	 * Gets a session based on an ID.
	 * @param _id The ID of the session.
	 * @return The {@link HttpSession} associated with the given ID.
	 */
	public HttpSession getSession(String _id) {
		Iterator mainSessionsIterator = this.getMap().values().iterator();
		while (mainSessionsIterator.hasNext()) {
			List actionSessions = (LinkedList) mainSessionsIterator.next();
			Iterator actionSessionsIterator = actionSessions.iterator();
			while (actionSessionsIterator.hasNext()) {
				HttpSession session = (HttpSession) actionSessionsIterator.next();
				try {
					if (_id.equals(session.getId())) {
						return session;
					}
				} catch (IllegalStateException ise) {
					//Invalidated session.  Remove it.
					actionSessionsIterator.remove();
				}
			}
		}
		return null;
	}

	/**
	 * Counts the number of sessions associated with a given action.
	 * @param _action The action to look for.
	 * @return The number of sessions associated with a given action.
	 */
	public int getCount(String _action) {
		return m_sessions.getCount(_action);
	}

	/**
	 * Returns a {@link Map} of the sessions.
	 */
	public Map getMap() {
		return m_sessions.getMap();
	}
	
	/**
	 * Returns a {@link List} of {@link Properties} obtained from the sessions based on the given attributes.
	 * @param _attributes A String[] of attributes to get.
	 * @return A {@link List} of {@link Properties}.
	 */
	public List getSummary(String[] _attributes) {
		List attributes = new LinkedList();
		for (int i=0; i<_attributes.length; i++) {
			attributes.add(_attributes[i]);
		}
		return this.getSummary(attributes);
	}
	
	/**
	 * Returns a {@link List} of {@link Properties} obtained from the sessions based on the given attribute.
	 * @param _attribute The attribute to get.
	 * @return A {@link List} of {@link Properties}.
	 */
	public List getSummary(String _attribute) {
		String[] attributes = {_attribute};
		return this.getSummary(attributes);
	}
	/**
	 * Returns a {@link List} of {@link Properties} obtained from the sessions based on the given attributes.
	 * @param _attributes A {@link List} of attributes to get.
	 * @return A {@link List} of {@link Properties}.
	 */
	public List getSummary(List _attributes) {
		List _return = new LinkedList();
		Iterator mainSessionsIterator = this.getMap().values().iterator();
		while (mainSessionsIterator.hasNext()) {
			List actionSessions = (LinkedList) mainSessionsIterator.next();
			Iterator actionSessionsIterator = actionSessions.iterator();
			while (actionSessionsIterator.hasNext()) {
				HttpSession session = (HttpSession) actionSessionsIterator.next();
				try {
					Properties attributes = new Properties();
					Iterator attributeIterator = _attributes.iterator();
					while (attributeIterator.hasNext()) {
						String attribute = (String) attributeIterator.next();
						Object value = session.getAttribute(attribute);
						if (value != null) {
							if (value instanceof String) {
//								if (!((String) value).endsWith("TrackingAction")) {
									attributes.setProperty(attribute, (String) value);
//								}
							} else if (value instanceof Long) {
								attributes.setProperty(attribute, String.valueOf((Long) value));
							}
						}
//						else {
//							attributes.setProperty(attribute, "[null]");
//						}
					}
					_return.add(attributes);
				} catch (IllegalStateException ise) {
					//Invalidated session.  Remove it.
					actionSessionsIterator.remove();
				}
			}
		}
		return _return;
	}
}
