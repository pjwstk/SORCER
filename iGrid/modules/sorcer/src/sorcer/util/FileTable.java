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
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.rmi.MarshalledObject;
import java.util.Enumeration;
import java.util.Hashtable;

import net.jini.id.Uuid;

public class FileTable implements Runnable {

	// Object File
	ObjectFile ofl;
	// Index File
	ObjectFile ifl;

	String fileName;

	private Hashtable table;

	private static int WAITING_TIME = 1 * 30 * 1000; // 5 min

	boolean running = true;

	public FileTable(String fileName) throws IOException {

		this.fileName = fileName;

		ofl = new ObjectFile(fileName + ".obf");
		ifl = new ObjectFile(fileName + "_index.obf");

		try {
			table = (Hashtable) ifl.readObject(0);
		} catch (Exception e) {
		}

		if (table == null)
			table = new Hashtable();

		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	public synchronized final void close() throws IOException {
		running = false;
		ofl.close();
		ifl.close();
	}

	public synchronized final void put(Uuid cookie, Serializable o)
			throws IOException {
		Long oldPos = (Long) table.get(cookie);
		long newPos;
		if (oldPos == null)
			newPos = ofl.writeObject(o);
		else
			newPos = ofl.writeObject(o, oldPos.longValue());

		table.put(cookie, new Long(newPos));
		ifl.rewriteObject(0, table);
	}

	public synchronized final Object get(Uuid cookie) throws IOException {
		Long pos = (Long) table.get(cookie);
		if (pos == null)
			return null;
		else
			return ofl.readObject(pos.longValue());
	}

	public Enumeration keys() {
		return table.keys();
	}

	public boolean containsKey(Object key) {
		return table.containsKey(key);
	}

	public synchronized final void remove(Uuid cookie) throws IOException {
		table.remove(cookie);
		ifl.rewriteObject(0, table);
	}

	public synchronized void cleanup() throws IOException {
		ObjectFile tmp = new ObjectFile(fileName + "_temp.obf");
		Hashtable newTable = new Hashtable();

		Object key;
		for (Enumeration e = table.keys(); e.hasMoreElements();) {
			key = e.nextElement();
			newTable.put(key, new Long(tmp
					.writeObject((Serializable) get((Uuid) key))));
		}

		table = newTable;
		ifl.rewriteObject(0, table);

		ofl.close();
		tmp.close();

		new File(fileName + "_temp.obf").renameTo(new File(fileName + ".obf"));

		ofl = new ObjectFile(fileName + ".obf");
	}

	public void run() {
		while (running) {
			try {
				Thread.sleep(WAITING_TIME);
				cleanup();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class ObjectFile {
		RandomAccessFile dataFile;

		public ObjectFile(String fileName) throws IOException {
			dataFile = new RandomAccessFile(fileName, "rw");
		}

		public byte[] getBytes(Serializable obj) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(new MarshalledObject(obj));
			oos.flush();

			byte[] b = baos.toByteArray();
			baos = null;
			oos = null;

			return b;
		}

		// returns file position object was written to.
		public synchronized long writeObject(Serializable obj)
				throws IOException {
			// write at end
			return writeObject(getBytes(obj), dataFile.length());
		}

		// returns file position object was written to.
		public synchronized long writeObject(Serializable obj, long lPos)
				throws IOException {

			dataFile.seek(lPos);
			int oldlen = dataFile.readInt();

			byte[] b = getBytes(obj);
			int newlen = b.length;

			return (oldlen >= newlen) ? writeObject(b, lPos) : writeObject(b,
					dataFile.length());
		}

		private synchronized long writeObject(byte[] b, long pos)
				throws IOException {

			int datalen = b.length;

			dataFile.seek(pos);

			// write the length of the output
			dataFile.writeInt(datalen);
			dataFile.write(b);

			return pos;
		}

		public synchronized Object readObject(long lPos) throws IOException {
			dataFile.seek(lPos);
			int datalen = dataFile.readInt();
			if (datalen > dataFile.length())
				throw new IOException("Data file is corrupted. datalen: "
						+ datalen);
			byte[] data = new byte[datalen];
			dataFile.readFully(data);

			ByteArrayInputStream bais = new ByteArrayInputStream(data);
			ObjectInputStream ois = new ObjectInputStream(bais);
			MarshalledObject o;

			try {
				o = (MarshalledObject) ois.readObject();
				bais = null;
				ois = null;
				data = null;

				return o.get();
			} catch (ClassNotFoundException cnfe) {
				cnfe.printStackTrace();
				throw new IOException("Class Not found Exception msg:"
						+ cnfe.getMessage());
			}

		}

		public synchronized void rewriteObject(long pos, Serializable obj)
				throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(new MarshalledObject(obj));
			oos.flush();

			int datalen = baos.size();

			// insert record
			dataFile.seek(pos);

			// write the length of the output
			dataFile.writeInt(datalen);
			dataFile.write(baos.toByteArray());

			baos = null;
			oos = null;
		}

		public void close() throws IOException {
			dataFile.close();
		}
	}

	public static void main(String[] args) throws Exception {

		Uuid cookie = net.jini.id.UuidFactory.generate();
		System.out.println("::::::::::::::::::::::" + cookie);
		cookie = net.jini.id.UuidFactory.generate();
		System.out.println("::::::::::::::::::::::" + cookie);
		FileTable table = new FileTable("test");
		table.put(cookie, "String22222");
		Enumeration e = table.keys();
		Uuid key;
		while (e.hasMoreElements()) {
			key = (Uuid) e.nextElement();
			table.put(key, "string22222");
			System.out.println(table.get(key));
		}
		// new File("null.obf").renameTo(new File("test.obf"));
		// table.cleanup();
		table.close();
	}

}
