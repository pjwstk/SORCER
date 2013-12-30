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

public class GriderDispatcherProgram {
	public GriderDispatcherProgram() {
		windows = solaris = linux = true;
		winexecFiletype = "";
		winbinFolderpath = winbinFile = "";
		winlibFolderpath = winlibFile = "";
		solbinFolderpath = solbinFile = "";
		linbinFolderpath = linbinFile = "";
	}

	public boolean getWindows() {
		return windows;
	}

	public boolean getSolaris() {
		return solaris;
	}

	public boolean getLinux() {
		return linux;
	}

	public String getWinexecFiletype() {
		return winexecFiletype;
	}

	public String getWinbinPath() {
		return winbinFolderpath;
	}

	public String getWinbinFile() {
		return winbinFile;
	}

	public String getWinlibPath() {
		return winlibFolderpath;
	}

	public String getWinlibFile() {
		return winlibFile;
	}

	public String getSolbinPath() {
		return solbinFolderpath;
	}

	public String getSolbinFile() {
		return solbinFile;
	}

	public String getLinbinPath() {
		return linbinFolderpath;
	}

	public String getLinbinFile() {
		return linbinFile;
	}

	public void setWindows(boolean b) {
		windows = b;
	}

	public void setSolaris(boolean b) {
		solaris = b;
	}

	public void setLinux(boolean b) {
		linux = b;
	}

	public void setWinexecFiletype(String s) {
		winexecFiletype = s;
	}

	public void setWinbinPath(String s) {
		winbinFolderpath = s;
	}

	public void setWinbinFile(String s) {
		winbinFile = s;
	}

	public void setWinlibPath(String s) {
		winlibFolderpath = s;
	}

	public void setWinlibFile(String s) {
		winlibFile = s;
	}

	public void setSolbinPath(String s) {
		solbinFolderpath = s;
	}

	public void setSolbinFile(String s) {
		solbinFile = s;
	}

	public void setLinbinPath(String s) {
		linbinFolderpath = s;
	}

	public void setLinbinFile(String s) {
		linbinFile = s;
	}

	public String view() {
		StringBuilder v = new StringBuilder();
		v.append("Windows: " + windows + "\n");
		v.append("Windows Executable File Type: " + winexecFiletype + "\n");
		v.append("Windows Binary Folder Path: " + winbinFolderpath + "\n");
		v.append("Windows Binary File: " + winbinFile + "\n");
		v.append("Windows Library Folder Path: " + winlibFolderpath + "\n");
		v.append("Windows Library File: " + winlibFile + "\n\n");
		v.append("Solaris: " + solaris + "\n");
		v.append("Solaris Binary Folder Path: " + solbinFolderpath + "\n");
		v.append("Solaris Binary File: " + solbinFile + "\n\n");
		v.append("Linux: " + linux + "\n");
		v.append("Linux Binary Folder Path: " + linbinFolderpath + "\n");
		v.append("Linux Binary File: " + linbinFile + "\n");
		return v.toString();
	}

	private boolean windows;
	private boolean solaris;
	private boolean linux;
	private String winexecFiletype;
	private String winbinFolderpath;
	private String winbinFile;
	private String winlibFolderpath;
	private String winlibFile;
	private String solbinFolderpath;
	private String solbinFile;
	private String linbinFolderpath;
	private String linbinFile;
}