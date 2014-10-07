/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.businessjournal.beans;

import com.datastax.driver.core.Session;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.jdo.datastore.JDOConnection;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.it.lecm.businessjournal.remote.BusinessJournalStoreRecord;
import ru.it.lecm.businessjournal.remote.RecordsCount;

/**
 *
 * @author ikhalikov
 */
public class BJMessageListener implements MessageListener {

	private final static Logger logger = LoggerFactory.getLogger(BJMessageListener.class);

	PersistenceManagerFactory pmf;

	private String store;

	public void setStore(String store) {
		this.store = store;
	}

	public void init() {
		pmf = JDOHelper.getPersistenceManagerFactory("datanucleus.properties");
	}

	@Override
	public void onMessage(final Message message
	) {
		PersistenceManager entityManager = pmf.getPersistenceManager();
		Transaction tr = entityManager.currentTransaction();
		try {
			tr.begin();
			ObjectMapper mapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			String textMessage = ((TextMessage) message).getText();
			BusinessJournalStoreRecord rec = mapper.readValue(textMessage, BusinessJournalStoreRecord.class);
			entityManager.makePersistent(rec);

			if ("cassandra".equals(store)) {
				JDOConnection conn = entityManager.getDataStoreConnection();
				try {
					Session session = (Session) conn.getNativeConnection();
					session.execute("update bj.recordscount set count=count+1 where id=0");
				} finally {
					conn.close();
				}
			} else {
				try {
					RecordsCount count = entityManager.getObjectById(RecordsCount.class, 0);
					count.incr();
				} catch (JDOObjectNotFoundException ex) {
					logger.error("Row for storing records count not found, inserting one...");
					RecordsCount count = new RecordsCount();
					count.incr();
					entityManager.makePersistent(count);
				}
			}

			tr.commit();
		} catch (JMSException ex) {
			logger.error("Something gone wrong, while recieving message", ex);
		} catch (IOException ex) {
			logger.error("Something gone wrong, while recieving message", ex);
		} catch (Exception ex) {
			logger.error("Something gone wrong, while recieving message", ex);
		} finally {
			if (tr.isActive()) {
				tr.rollback();
			}
			entityManager.close();
		}

	}

}
