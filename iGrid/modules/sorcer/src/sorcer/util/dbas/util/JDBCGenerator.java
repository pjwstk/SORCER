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

package sorcer.util.dbas.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import sorcer.util.Sorcer;

/**
 * A JDBC-based sequence generator that implements
 * <CODE>jgapp.dbas.util.SequenceGenerator</CODE> interface. To use this
 * sequence generator, your database must have the following data model:
 * 
 * <PRE>
 * CREATE TABLE [OWNER]_ORA_SEQGEN (
 *     NAME           VARCHAR(25)     NOT NULL PRIMARY KEY,
 *     NEXT_SEQ       BIGINT          NOT NULL DEFAULT 1,
 *     LUTS           BIGINT          NOT NULL);
 * 
 * CREATE UNIQUE INDEX SEQGEN_IDX ON {OWNER]_ORA_SEQGEN(NAME, LUTS);
 * </PRE>
 */
public class JDBCGenerator {
	static private long currentNode = -1L;
	static private JDBCGenerator generator = null;
	static private long nextID = -1L;

	public JDBCGenerator() {
		super();
	}

	static public synchronized long generateSequence(Connection connection,
			String seq) throws SequenceException {
		if (generator == null) {
			generator = new JDBCGenerator();
		}
		return generator.generate(connection, seq);
	}

	static public synchronized long nextObjectID(Connection connection)
			throws SequenceException {
		if (currentNode == -1L || nextID >= 99999L) {
			currentNode = generateSequence(connection, "node");
			if (currentNode < 1) {
				nextID = 1;
			} else {
				nextID = 0;
			}
		} else {
			nextID++;
		}
		return ((currentNode * 100000L) + nextID);
	}

	/**
	 * The SQL to insert a new sequence number in the table.
	 */
	static public final String INSERT = "INSERT INTO "
			+ Sorcer.tableName("ORA_SEQGEN") + " (NAME, NEXT_SEQ, LUTS) "
			+ "VALUES(?, ?, ?)";

	/**
	 * Selects the next sequence number from the database.
	 */
	static public final String SELECT = "SELECT NEXT_SEQ, LUTS " + "FROM "
			+ Sorcer.tableName("ORA_SEQGEN") + " WHERE NAME = ?";

	/**
	 * The SQL to one-up the current sequence number.
	 */
	static public final String UPDATE = "UPDATE "
			+ Sorcer.tableName("ORA_SEQGEN") + "SET NEXT_SEQ = ?, "
			+ "LUTS = ? " + "WHERE NAME = ? " + "AND LUTS = ?";

	/**
	 * Creates a new sequence.
	 * 
	 * @param conn
	 *            the JDBC connection to use
	 * @param seq
	 *            the sequence name
	 * @throws java.sql.SQLException
	 *             a database error occurred
	 */
	private void createSequence(Connection conn, String seq)
			throws SQLException {
		PreparedStatement stmt = conn.prepareStatement(INSERT);

		stmt.setString(1, seq);
		stmt.setLong(2, 1L);
		stmt.setLong(3, (new java.util.Date()).getTime());
		stmt.executeUpdate();
	}

	/**
	 * Generates a sequence for the specified sequence in accordance with the
	 * <CODE>SequenceGenerator</CODE> interface.
	 * 
	 * @param seq
	 *            the name of the sequence to generate
	 * @return the next value in the sequence
	 * @throws com.imaginary.lwp.SequenceException
	 *             an error occurred generating the sequence
	 */
	public synchronized long generate(Connection conn, String seq)
			throws SequenceException {
		try {
			PreparedStatement stmt;
			ResultSet rs;
			long nid, lut, tut;

			stmt = conn.prepareStatement(SELECT);
			stmt.setString(1, seq);
			rs = stmt.executeQuery();
			if (!rs.next()) {
				try {
					createSequence(conn, seq);
				} catch (SQLException e) {
					String state = e.getSQLState();

					// if a duplicate was found, retry sequence generation
					if (state.equalsIgnoreCase("SQL0803N")) {
						return generate(conn, seq);
					}
					throw new SequenceException("Database error: "
							+ e.getMessage());
				}
				return 0L;
			}
			nid = rs.getLong(1);
			lut = rs.getLong(2);
			tut = (new java.util.Date()).getTime();
			if (tut == lut) {
				tut++;
			}
			stmt = conn.prepareStatement(UPDATE);
			stmt.setLong(1, nid + 1);
			stmt.setLong(2, tut);
			stmt.setString(3, seq);
			stmt.setLong(4, lut);
			try {
				stmt.executeUpdate();
				conn.commit();
			} catch (SQLException e) {
				String state = e.getSQLState();

				// someone else grabbed the row,
				// we need to try again
				if (state.equals("SQL0100W")) {
					return generate(conn, seq);
				}
				throw new SequenceException("Database error: " + e.getMessage());
			}
			return nid;
		} catch (SQLException e) {
			throw new SequenceException("Database error: " + e.getMessage());
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}
	}
}
