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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.MissingResourceException;

import sun.rmi.server.MarshalInputStream;
import sun.rmi.server.MarshalOutputStream;

/**
 * Serializes an object to a byte array and stores in the specified table row
 * and column also retrieves the object via the get() function
 */
public class DatabaseObject implements Serializable {
	int objLen = 0;

	public void writeObject(Object o, String tableName, String columnName,
			String rowId, String priKeyName, Connection conn)
			throws StreamCorruptedException, EOFException, IOException,
			SQLException {
		InputStream is = null;
		PreparedStatement pstat = null;

		try {
			StringBuffer sb = new StringBuffer();
			sb.append("UPDATE ").append(tableName).append(" SET ").append(
					tableName).append(".").append(columnName).append(
					"= ? WHERE ").append(tableName).append(".").append(
					priKeyName).append("=").append(rowId);

			pstat = conn.prepareStatement(sb.toString());
			is = serializeObjectToInputStream(o);
			pstat.setBinaryStream(1, is, objLen);
			pstat.executeUpdate();
		} finally {
			if (is != null)
				is.close();
			if (pstat != null)
				pstat.close();
		}
	}

	public Object readObject(String tableName, String columnName, String rowId,
			String priKeyName, Connection conn)
			throws StreamCorruptedException, EOFException, IOException,
			SQLException, ClassNotFoundException {

		ResultSet res = null;
		Object o = null;
		MarshalInputStream mis = null;
		boolean isEOFException = false;
		// Util.debug(this, "::readObject() START.");
		Statement stat = null;
		try {
			stat = conn.createStatement();
			String sql = "SELECT " + columnName + " FROM " + tableName
					+ " WHERE " + priKeyName + "=" + rowId;
			// Util.debug(this, "::readObject() query:" + sql);
			res = stat.executeQuery(sql);
			if (!res.next())
				throw new MissingResourceException("Missing database object",
						tableName, columnName);
			try {
				InputStream is = res.getBinaryStream(columnName);
				mis = new MarshalInputStream(is);
				o = mis.readObject();
				// Util.debug(this, "Read Object get:o :" + o);
				res.close();
			} catch (StreamCorruptedException sce) {
				// sce.printStackTrace();
				isEOFException = true;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (stat != null)
				stat.close();
			try {
				if (mis != null)
					mis.close();
			} catch (IOException ioex) {
				if (isEOFException)
					throw new StreamCorruptedException();
				else
					throw ioex;
			}
		}
		return o;
	}

	private ByteArrayInputStream serializeObjectToInputStream(Object o)
			throws IOException {
		ByteArrayInputStream bais = null;
		byte[] tmp = null;
		MarshalOutputStream mos = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			mos = new MarshalOutputStream(baos);
			mos.writeObject(o);
			mos.flush();
			tmp = baos.toByteArray();
			objLen = tmp.length;
			bais = new ByteArrayInputStream(tmp);

		} finally {
			if (mos != null)
				mos.close();
		}
		return bais;
	}

	private String byteArrayToHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length);

		for (int i = 0; i < b.length; i++)
			sb.append(Integer.toHexString(b[i]));

		return sb.toString();
	}
}
