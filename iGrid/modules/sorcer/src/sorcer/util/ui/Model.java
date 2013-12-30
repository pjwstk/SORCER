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

package sorcer.util.ui;

import java.util.Hashtable;
import java.util.Observable;

import sorcer.util.CallbackModel;

public class Model extends Observable implements CallbackModel {
	public String from, to;
	// private static Vector models = new Vector();
	// view is name of a view imbeded by applications into an applet by
	// AppletView
	// when the applet should be displayed in a frame
	static public String view;

	public Model() {
		// models.addElement(this);
	}

	// public static int getObserverCount() {
	// int count = 0;
	// for (int i=0;i<models.size();i++)
	// count+=((Observable)models.elementAt(i)).countObservers();
	// return count;
	// }

	public void cleanup() {
		// this.deleteObservers();
		// models.removeElement(this);
	}

	public static void printModels() {
		// StringBuffer sb = new StringBuffer();
		// for (int i=0;i<models.size();i++)
		// sb.append("\n"+models.elementAt(i)+" has "+
		// ((Observable)models.elementAt(i)).countObservers()+" Observers");
		// sb.append("\nTotal Observer Count = "+getObserverCount());
		// Util.debug(sb.toString());
	}

	public static void clearModels() {
		// for (int i=0;i<models.size();i++) {
		// if ( ((Observable)models.elementAt(i)).countObservers() == 0 )
		// models.removeElementAt(i);
		// }
		// System.gc();
	}

	public void changed(Object aspect) {
		update(aspect);
		setChanged();
		notifyObservers(aspect);
	}

	public void changed(Object aspect, Object arg) {
		update(aspect, arg);
		Hashtable args = new Hashtable();
		args.put("aspect", aspect);
		args.put("arg1", arg);
		setChanged();
		notifyObservers(args);
	}

	public void changed(Object aspect, Object arg1, Object arg2) {
		update(aspect, arg1, arg2);
		Hashtable args = new Hashtable();
		args.put("aspect", aspect);
		args.put("arg1", arg1);
		args.put("arg2", arg2);
		setChanged();
		notifyObservers(args);
	}

	public void changed(Object aspect, Object arg1, Object arg2, Object arg3) {
		update(aspect, arg1, arg2, arg3);
		Hashtable args = new Hashtable();
		args.put("aspect", aspect);
		args.put("arg1", arg1);
		args.put("arg2", arg2);
		args.put("arg3", arg3);
		setChanged();
		notifyObservers(args);
	}

	public void changed(Object aspect, Object arg1, Object arg2, Object arg3,
			Object arg4) {
		update(aspect, arg1, arg2, arg3, arg4);
		Hashtable args = new Hashtable();
		args.put("aspect", aspect);
		args.put("arg1", arg1);
		args.put("arg2", arg2);
		args.put("arg3", arg3);
		args.put("arg4", arg4);
		setChanged();
		notifyObservers(args);
	}

	public void update(Object obj) {
		// do nothing, implement in subclasses
	}

	public void update(Object aspect, Object arg) {
		// do nothing, implement in subclasses
	}

	public void update(Object aspect, Object arg1, Object arg2) {
		// do nothing, implement in subclasses
	}

	public void update(Object aspect, Object arg1, Object arg2, Object arg3) {

		// do nothing, implement in subclasses
	}

	public void update(Object aspect, Object arg1, Object arg2, Object arg3,
			Object arg4) {
		// do nothing, implement in subclasses
	}

	public boolean isUserVerified() {
		// do nothing, implement in subclasses
		return false;
	}

	protected String createQuery(String name) {
		// implemented by subclasses for a modee dependent queries
		return "";
	}

	protected void finalize() throws Throwable {
		super.finalize();
		deleteObservers();
	}
}
