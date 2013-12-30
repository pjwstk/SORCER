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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class SaveOutput extends PrintStream {
	static OutputStream logfile;
	static PrintStream oldStdout;
	static PrintStream oldStderr;

	SaveOutput(PrintStream ps) {
		super(ps);
	}

	// Starts copying stdout and
	// stderr to the file f.
	public static void start(String f) throws IOException {
		// Save old settings.
		oldStdout = System.out;
		oldStderr = System.err;

		// Create/Open logfile.
		logfile = new PrintStream(new BufferedOutputStream(
				new FileOutputStream(f)));

		// Start redirecting the output.
		System.setOut(new SaveOutput(System.out));
		System.setErr(new SaveOutput(System.err));
	}

	// Restores the original settings.
	public static void stop() {
		System.setOut(oldStdout);
		System.setErr(oldStderr);
		try {
			logfile.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// PrintStream override.
	public void write(int b) {
		try {
			logfile.write(b);
		} catch (Exception e) {
			e.printStackTrace();
			setError();
		}
		super.write(b);
	}

	// PrintStream override.
	public void write(byte buf[], int off, int len) {
		try {
			logfile.write(buf, off, len);
		} catch (Exception e) {
			e.printStackTrace();
			setError();
		}
		super.write(buf, off, len);
	}
}
