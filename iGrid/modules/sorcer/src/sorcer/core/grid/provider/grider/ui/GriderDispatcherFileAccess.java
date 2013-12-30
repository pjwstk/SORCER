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

package sorcer.core.grid.provider.grider.ui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import sorcer.core.grid.provider.grider.ui.GriderDispatcherStringEncrypter.EncryptionException;

public class GriderDispatcherFileAccess
/*
 * This FileAccess class allows the ServiceUI to save and retrieve its
 * configuration object with simple load and save function calls. The FileAccess
 * class uses a StringEncrypter class to encrypt the text before it is saved.
 * 
 * The FileAccessor class saves the configuration by assigning each object a
 * "tag". The object is saved with a object descriptor (such as "ExecComm" for
 * the Execution Command), a object tag type (such as "TextBox") for Execution
 * Command or "CheckBox" for isLinux. The value of the object is then saved
 * after that. The process of saving it goes in three steps: Splitting,
 * Encrypting, and Writing. The value is split into lengths of 50, then
 * encrypted, and then saved to file.
 * 
 * To retrieve, the FileAccessor reads the object descriptor and the object tag
 * to know which type of data is being loaded. It then reads the value, and
 * finally stores that back into the new config object.
 * 
 * The tags: TextBox, CheckBox, ComboBox (not implemented), Args, and Include.
 * 
 * TextBox just reads a single line. CheckBox reads something in as a true/false
 * value. ComboBox will store the selected index, but nothing currently uses
 * this. Args first has the number of arguments, and then that number of triplet
 * lines of the form (in, arg, out). Include, which for now must be read in
 * last, simply reads in lines until it reaches end of file.
 */
{
	private BufferedReader ifs;
	private BufferedWriter ofs;
	private String encryptionKey = "teamteamteamteamtukatukatukatuka";
	private String encryptionScheme = GriderDispatcherStringEncrypter.DES_ENCRYPTION_SCHEME;
	private GriderDispatcherStringEncrypter encrypter;

	public GriderDispatcherFileAccess() throws EncryptionException {
		encrypter = new GriderDispatcherStringEncrypter(encryptionScheme,
				encryptionKey);
	}

	public void saveConfig(String file, GriderDispatcherConfig sConfig) {
		String saveString;
		try {
			// Establish new file connection, and call the save functions for
			// all objects
			ofs = new BufferedWriter(new FileWriter(file));
			saveAllObjects(ofs, sConfig);
			saveArgs(ofs, sConfig);
			saveInclude(ofs, sConfig);
			// close the connection
			ofs.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void save(BufferedWriter ofs, String s) {
		try {

			// There is an annoying "feature" with the string encrypter class.
			// It does not allow
			// the length of the string to be encrypted to be larger than 55 or
			// so, and so what we do to solve this
			// is split every string to be written into smaller strings of
			// length 50 or less, and then save those
			// encrypted.
			String[] sfifty = substrings(s, 50);

			int i;
			for (i = 0; i < sfifty.length; i++) // save each string
			{
				// we prepend a "$" in order to solve another feature of the
				// string encrypter, which is
				// the fact that attempting to encrypt a null or empty string
				// results in an error.
				ofs.write(encrypter.encrypt("$" + sfifty[i]) + "\r\n");
			}

		} catch (Exception e) {
		}
	}

	public GriderDispatcherConfig load(String file) {
		GriderDispatcherConfig loadedConfig = new GriderDispatcherConfig();
		String dataType;
		try {
			ifs = new BufferedReader(new FileReader(file));
			while ((dataType = read(ifs)) != null) {
				// Textboxes, Comboboxes, and Checkboxes tags
				// are single line values, so they are all loaded in the same
				// function.
				if (dataType.equals("Textbox"))
					loadObject(ifs, loadedConfig);
				if (dataType.equals("Combobox"))
					loadObject(ifs, loadedConfig);
				if (dataType.equals("Checkbox"))
					loadObject(ifs, loadedConfig);
				// The "include" tag is for the include file, which is some
				// arbitrary length file.
				if (dataType.equals("Include"))
					loadInclude(ifs, loadedConfig);
				// The "args" tag is of the format:
				// Args
				// NumArgs
				// In Arg 1
				// Arg 2
				// Out Arg 2
				if (dataType.equals("Args"))
					loadArgs(ifs, loadedConfig);
			}
			// We've loaded it, time to close
			ifs.close();
		} catch (Exception e) {
		}
		// Return the context we've loaded, so that we can dump it into the UI
		return loadedConfig;
	}

	private void loadObject(BufferedReader ifs,
			GriderDispatcherConfig loadedConfig) throws IOException,
			EncryptionException {
		String tag = read(ifs); // Tag gets the type of object
		String value = read(ifs); // Value gets the object value
		store(loadedConfig, tag, value); // store maps the tag and value into
		// the correct location in the
		// config.
	}

	private void loadInclude(BufferedReader ifs,
			GriderDispatcherConfig loadedConfig) throws IOException,
			EncryptionException {
		StringBuilder b = new StringBuilder(); // Use a string builder because
		// strings are immutable
		String next = "";
		try {
			next = read(ifs); // read the next line..
			while (next != null) // This reads until the END OF THE FILE (maybe
			// not a good thing in the long run)
			{
				b.append(next); // add the string to the string builder
				next = read(ifs); // read the next string
			}
		} catch (Exception e) {
		} finally {
			loadedConfig.ca.setInclude(b.toString());
		}
	}

	private void loadArgs(BufferedReader ifs,
			GriderDispatcherConfig loadedConfig) throws IOException,
			EncryptionException {
		int i;
		int NumArg = Integer.valueOf((String) read(ifs)).intValue(); // Figure
		// out
		// the
		// number
		// of
		// arguments
		loadedConfig.ca.para.clear();
		for (i = 0; i < NumArg; i++) {
			loadedConfig.ca.para.addIn((String) read(ifs)); // read in input,
			// arg, output
			loadedConfig.ca.para.addArg((String) read(ifs));
			loadedConfig.ca.para.addOut((String) read(ifs));
		}
	}

	private void store(GriderDispatcherConfig loadedConfig, String object,
			String value) {
		// This isn't pretty right now, but right now it checks the tag and sets
		// based upon that.
		if (object.contains("ExecComm")) {
			loadedConfig.ca.setExecutecommand(value);
		}
		if (object.contains("ProgName")) {
			loadedConfig.ca.setProgname(value);
		}
		if (object.contains("Host")) {
			loadedConfig.ca.setHost(value);
		}
		if (object.contains("Jobsize")) {
			loadedConfig.ca.setJobsize(value);
		}
		if (object.contains("Location")) {
			loadedConfig.ca.setLocation(value);
		}
		if (object.contains("Notify")) {
			loadedConfig.ca.setNotify(value);
		}

		if (object.contains("LinBinFile")) {
			loadedConfig.prog.setLinbinFile(value);
		}
		if (object.contains("LinBinFolderFile")) {
			loadedConfig.prog.setLinbinPath(value);
		}
		if (object.contains("isLinux")) {
			loadedConfig.prog.setLinux(Boolean.valueOf(value).booleanValue());
		}

		if (object.contains("SolBinFile")) {
			loadedConfig.prog.setSolbinFile(value);
		}
		if (object.contains("SolBinFolderFile")) {
			loadedConfig.prog.setSolbinPath(value);
		}
		if (object.contains("isSolaris")) {
			loadedConfig.prog.setSolaris(Boolean.valueOf(value).booleanValue());
		}

		if (object.contains("WinBinFile")) {
			loadedConfig.prog.setWinbinFile(value);
		}
		if (object.contains("WinBinFolderFile")) {
			loadedConfig.prog.setWinbinPath(value);
		}
		if (object.contains("isWin")) {
			loadedConfig.prog.setWindows(Boolean.valueOf(value).booleanValue());
		}
		if (object.contains("WinExecType")) {
			loadedConfig.prog.setWinexecFiletype(value);
		}
		if (object.contains("WinLib")) {
			loadedConfig.prog.setWinlibFile(value);
		}
		if (object.contains("WinPathLib")) {
			loadedConfig.prog.setWinlibPath(value);
		}
	}

	private void saveAllObjects(BufferedWriter ofs,
			GriderDispatcherConfig sConfig) {
		// saves an object by assigning it an object type, a tag, and its
		// value..
		saveObject(ofs, "Textbox", "ExecComm", sConfig.ca.getExecutecommand());
		saveObject(ofs, "Textbox", "Host", sConfig.ca.getHost());
		saveObject(ofs, "Textbox", "Jobsize", sConfig.ca.getJobsize());
		saveObject(ofs, "Textbox", "Location", sConfig.ca.getLocation());
		saveObject(ofs, "Textbox", "Node", sConfig.ca.getNodename());
		saveObject(ofs, "Textbox", "Notify", sConfig.ca.getNotify());
		saveObject(ofs, "Textbox", "ProgName", sConfig.ca.getProgname());

		saveObject(ofs, "Textbox", "LinBinFile", sConfig.prog.getLinbinFile());
		saveObject(ofs, "Textbox", "LinBinFolderFile", sConfig.prog
				.getLinbinPath());
		saveObject(ofs, "Checkbox", "isLinux", String.valueOf(sConfig.prog
				.getLinux()));

		saveObject(ofs, "Textbox", "SolBinFile", sConfig.prog.getSolbinFile());
		saveObject(ofs, "Textbox", "SolBinFolderFile", sConfig.prog
				.getSolbinPath());
		saveObject(ofs, "Checkbox", "isSolaris", String.valueOf(sConfig.prog
				.getSolaris()));

		saveObject(ofs, "Textbox", "WinBinFile", sConfig.prog.getWinbinFile());
		saveObject(ofs, "Textbox", "WinBinFolderFile", sConfig.prog
				.getWinbinPath());
		saveObject(ofs, "Checkbox", "isWin", String.valueOf(sConfig.prog
				.getWindows()));
		saveObject(ofs, "Textbox", "WinExecType", sConfig.prog
				.getWinexecFiletype());
		saveObject(ofs, "Textbox", "WinLib", sConfig.prog.getWinlibFile());
		saveObject(ofs, "Textbox", "WinPathLib", sConfig.prog.getWinlibPath());
	}

	private void saveInclude(BufferedWriter ofs, GriderDispatcherConfig sConfig) {
		// include just dumps the file
		save(ofs, "Include");
		save(ofs, sConfig.ca.getInclude());
	}

	private void saveArgs(BufferedWriter ofs, GriderDispatcherConfig sConfig) {
		int i;
		// include determines the max size of the vector, and then saves the
		// number of args to the file
		int maxSize = max(max(sConfig.ca.para.getArg().size(), sConfig.ca.para
				.getIn().size()), sConfig.ca.para.getOut().size());
		save(ofs, "Args");
		save(ofs, String.valueOf(maxSize));
		// saves the arguments
		for (i = 0; i < maxSize; i++) {
			save(ofs, (String) sConfig.ca.para.getIn().elementAt(i));
			save(ofs, (String) sConfig.ca.para.getArg().elementAt(i));
			save(ofs, (String) sConfig.ca.para.getOut().elementAt(i));
		}
	}

	private void saveObject(BufferedWriter ofs, String objType, String objName,
			String valName) {
		// object type, object name, value
		save(ofs, objType);
		save(ofs, objName);
		save(ofs, valName);
	}

	private int max(int i, int i0) {
		if (i > i0)
			return i;
		return i0;
	}

	private String read(BufferedReader ifs) throws EncryptionException,
			IOException {
		String decryptedString = ifs.readLine(); // read in a full line of
		// encrypted text
		if (decryptedString == null)
			return null; // if there was no line, return that
		try {
			decryptedString = encrypter.decrypt(decryptedString);
		} catch (Exception e) {
		}
		return decryptedString.substring(1); // return the decrypted string, and
		// remove the prepended $ that
		// was tacked on when we saved to avoid the problem of trying to
		// decrypt null strings.
	}

	private String[] substrings(String s, int length) {
		int i = 0;
		int n = s.length() / length; // determine how many strings of length
		// "length"
		int m = s.length() % length; // determine the amount left over
		String[] ret = new String[n + 1];

		for (i = 0; i < n; i++) {
			ret[i] = s.substring(i * 50, (i * 50) + 49);

		}

		ret[i] = s.substring(i * 50, (i * 50) + m); // grab remaining text

		return ret; // return array
	}
}