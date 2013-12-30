/*
 * Copyright 2010 the original author or authors.
 * Copyright 2010 SorcerSoft.org.
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

package sorcer.util;

import java.io.IOException;
import java.util.Vector;

/**
 * The Protocol interface defines the basic operations for database
 * connectivity. GApp-based applications use jgapp.util.Protocol and its
 * different types of implementation (socket, servlet, HTTP tunneling) that
 * implement use commands for connection management, SQL queries management,
 * authentication, authorization, ACLs, logging. jgapp.util.GApp interface
 * provides some predefined commands and positions in database descriptors for
 * generic use in GApp to allow interoperability between different classes.
 * <p>
 */
public interface Protocol {
	/**
	 * Create a remote gapp.util.Command and execute doIt method with data, an
	 * array of strings. Returns the result of the command execution.
	 */
	public Vector executeCmd(int command, String[] data) throws IOException;

	/**
	 * Call the JDBC executeQuery method and returns the result set as a Vector
	 * of rows. Each row is Protocol.sep, use Util.tokenize(row, Protocol.sep)
	 * to get an array of items in the row.
	 */
	public Vector executeQuery(String sql);

	/**
	 * Call the JDBC updateQuery method and return the number of modified rows.
	 */
	public int executeUpdate(String sql);

	/**
	 * Call the JDBC executeQuery method on a PreparedStatement created for a
	 * string of parameters Protocol.sep separated. The first parameter is a
	 * query name, the remaining items are used as the prepared statement
	 * parameters. Returns the result set as a Vector of rows. Each row is
	 * Protocol.sep, use Util.tokenize(row, Protocol.sep) to get an array of
	 * items in the row.
	 */
	public Vector executeQueryFor(String parameters);

	/**
	 * Call the JDBC updateQuery method on a PreparedStatement created for a
	 * string of parameters Protocol.sep separated. The first parameter is a
	 * query name, the remaining items are used as the prepared statement
	 * parameters. Return the number of modified rows.
	 */
	public int executeUpdateFor(String parameters);

	public void connect();

	public void disconnect();

	public boolean connected();
}
