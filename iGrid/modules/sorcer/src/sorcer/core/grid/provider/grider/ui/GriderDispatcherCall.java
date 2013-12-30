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

public class GriderDispatcherCall {
	public GriderDispatcherCall() {
		para = new GriderDispatcherParameter();

		progname = jobsize = notify = "";
		location = host = "";
		nodename = "";
		include = "";
		executecommand = "";
	}

	public String getProgname() {
		return progname;
	}

	public String getJobsize() {
		return jobsize;
	}

	public String getNotify() {
		return notify;
	}

	public String getLocation() {
		return location;
	}

	public String getHost() {
		return host;
	}

	public String getNodename() {
		return nodename;
	}

	public String getInclude() {
		return include;
	}

	public String getExecutecommand() {
		return executecommand;
	}

	public void setProgname(String s) {
		progname = s;
	}

	public void setJobsize(String s) {
		jobsize = s;
	}

	public void setNotify(String s) {
		notify = s;
	}

	public void setLocation(String s) {
		location = s;
	}

	public void setHost(String s) {
		host = s;
	}

	public void setNodename(String s) {
		nodename = s;
	}

	public void setInclude(String s) {
		include = s;
	}

	public void setExecutecommand(String s) {
		executecommand = s;
	}

	public String view() {
		StringBuilder v = new StringBuilder();
		v.append("Program Name: " + progname + "\n");
		v.append("Job Size: " + jobsize + "\n");
		v.append("Notify: " + notify + "\n\n");
		v.append("Location: " + location + "\n");
		v.append("Host: " + host + "\n");
		v.append("Node Name: " + nodename + "\n");
		v.append("Include: \n" + include + "\n");
		v.append(para.view());
		v.append("\n");
		v.append("Executable Command: " + executecommand + "\n");
		return v.toString();
	}

	private String progname;
	private String jobsize;
	private String notify;
	private String location;
	private String host;
	private String nodename;
	private String include;
	public GriderDispatcherParameter para;
	private String executecommand;
}