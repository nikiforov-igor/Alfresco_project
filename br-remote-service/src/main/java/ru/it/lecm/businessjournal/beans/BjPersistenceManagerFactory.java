/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.it.lecm.businessjournal.beans;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.metadata.PersistenceUnitMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ikhalikov
 */
public class BjPersistenceManagerFactory extends JDOPersistenceManagerFactory {

	private final static Logger logger = LoggerFactory.getLogger(BjPersistenceManagerFactory.class);

	private static Properties harvestProperties() throws Exception {
		Properties allProperties = new Properties();
		Properties result = new Properties();

		try {
			InputStream is;
			logger.info("Trying to harvest properties for connection...");
			allProperties.load(BjPersistenceManagerFactory.class.getResourceAsStream("/business-journal.properties"));
			if ((is = BjPersistenceManagerFactory.class.getResourceAsStream("/business-journal-global.properties")) != null) {
				logger.info("Founded business-journal-global.properties...");
				allProperties.load(is);
			}
			if ((is = BjPersistenceManagerFactory.class.getResourceAsStream("/alfresco-global.properties")) != null) {
				logger.info("Founded alfresco-global.properties...");
				allProperties.load(is);
			}

			String connectionURL = allProperties.getProperty("datanucleus.ConnectionURL");
			if (connectionURL == null) {
				throw new Exception("Property datanucleus.ConnectionDriverName not found. It's mandatory for connecting to postgreSQL");
			}
			logger.info("ConnectionURL is: " + connectionURL);
			result.put("datanucleus.ConnectionURL", connectionURL);

			String dataStore = allProperties.getProperty("businessjournal.store");
			if (dataStore == null) {
				logger.info("Property businessjournal.store not found, trying get properties for postgreSQL...");
				dataStore = "postgres";
			}

			result.put("datanucleus.generateSchema.database.mode", allProperties.getProperty("datanucleus.generateSchema.database.mode", "none"));
			result.put("datanucleus.generateSchema.scripts.create.source", allProperties.getProperty("datanucleus.generateSchema.scripts.create.source", "classpath:postgres.sql"));
			result.put("datanucleus.PersistenceUnitName", "my-pu");

			switch (dataStore) {
				case "postgres":
					String driver = allProperties.getProperty("datanucleus.ConnectionDriverName");
					if (driver == null) {
						throw new Exception("Property datanucleus.ConnectionDriverName not found. It's mandatory for connecting to postgreSQL");
					}
					logger.info("ConnectionDriverName is: " + driver);
					result.put("datanucleus.ConnectionDriverName", driver);

					String userName = allProperties.getProperty("datanucleus.ConnectionUserName");
					if (userName == null) {
						logger.error("datanucleus.ConnectionUserName not found");
						throw new Exception("Property datanucleus.ConnectionUserName not found. It's mandatory for connecting to postgreSQL");
					}
					logger.info("ConnectionUserName is: " + userName);
					result.put("datanucleus.ConnectionUserName", userName);

					String password = allProperties.getProperty("datanucleus.ConnectionPassword");
					if (password == null) {
						logger.error("datanucleus.ConnectionPassword not found");
						throw new Exception("Property datanucleus.ConnectionPassword not found. It's mandatory for connecting to postgreSQL");
					}
					logger.info("ConnectionPassword is: " + password);
					result.put("datanucleus.ConnectionPassword", password);
					break;
				case "cassandra":
					String schema = allProperties.getProperty("datanucleus.mapping.Schema");
					if (schema == null) {
						logger.error("datanucleus.mapping.Schema not found");
						throw new Exception("Property datanucleus.mapping.Schema not found. It's mandatory for connecting to cassandra");
					}
					logger.info("datanucleus.mapping.Schema is: " + schema);
					result.put("datanucleus.mapping.Schema", schema);
					break;

			}

		} catch (IOException ex) {
			logger.error("Failed to harvest properties", ex);
		}

		return result;
	}

	public BjPersistenceManagerFactory(PersistenceUnitMetaData pumd, Map overrideProps) throws Exception {
		super(harvestProperties());
//		this.setName("bj");
//		ClusterConfig config = new ClusterConfig(this);
//		config.setDeafultFetch(500);
	}

	public BjPersistenceManagerFactory() throws Exception {
		super(harvestProperties());
//		this.setName("bj");
//		ClusterConfig config = new ClusterConfig(this);
//		config.setDeafultFetch(500);
	}

	public BjPersistenceManagerFactory(Map props) throws Exception {
		super(harvestProperties());
//		this.setName("bj");
//		ClusterConfig config = new ClusterConfig(this);
//		config.setDeafultFetch(500);
	}

}
