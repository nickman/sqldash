/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.heliosapm.sqldash.util;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.PooledConnection;

import com.heliosapm.utils.config.ConfigurationHelper;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;

/**
 * <p>Title: DBConn</p>
 * <p>Description: Simple connection factory for the default connection factory</p> 
 * <p>Company: Helios Development Group LLC</p>
 * @author Whitehead (nwhitehead AT heliosdev DOT org)
 * <p><code>com.heliosapm.sqldash.util.DBConn</code></p>
 */

public class DBConn {
	/** The singleton instance */
	private static volatile DBConn instance = null;
	/** The singleton instance ctor lock */
	private static final Object lock = new Object();
	
	private final String userName;
	private final String password;
	private final String url;

	private final OracleConnectionPoolDataSource ocpds;

	/**
	 * Creates a new DBConn
	 */
	private DBConn() {
		try {
			userName = ConfigurationHelper.getSystemThenEnvProperty("sqldash.datasource.username.default", "tqreactor");
			password = ConfigurationHelper.getSystemThenEnvProperty("sqldash.datasource.password.default", "tq");
			url = ConfigurationHelper.getSystemThenEnvProperty("sqldash.datasource.url.default", "jdbc:oracle:thin:@//localhost:1521/XE");			
			ocpds = new OracleConnectionPoolDataSource();
			ocpds.setURL(url);
			ocpds.setUser(userName);
			ocpds.setPassword(password);
		} catch (Exception ex) {
			throw new RuntimeException("Failed to initialize default datasource", ex);
		}
		
	}
	
	/**
	 * Acquires and returns the DBConn singleton
	 * @return the DBConn singleton
	 */
	public static DBConn getInstance() {
		if(instance==null) {
			synchronized(lock) {
				if(instance==null) {
					instance = new DBConn();
				}
			}
		}
		return instance;
	}
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		return ocpds.getConnection();
	}
	
	/**
	 * @return
	 * @throws SQLException
	 */
	public PooledConnection getPooledConnection() throws SQLException {
		return ocpds.getPooledConnection();
	}
	
	public static void main(String[] args) {
		try {
			log("DBConn Test");
			Connection c = getInstance().getPooledConnection().getConnection();
			log("Conn: [" + c.getClass().getName() + "]");
			c.close();
		} catch (Exception ex) {
			ex.printStackTrace(System.err);
			System.exit(-1);
		}
	}
	
	public static void log(Object msg) {
		System.out.println(msg);
	}
	

}
