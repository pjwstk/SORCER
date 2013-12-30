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

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Frame;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import sorcer.util.CallbackModel;
import sorcer.util.SorcerUtil;

abstract public class View extends Panel implements Observer {
	private static Logger logger = Logger.getLogger(View.class.getName());
	protected Observer parent = null;
	public CallbackModel model = null;
	// A frame the view might be palced in, is used by applets if needed.
	// AppletFrame stores itself for applets
	protected Frame frame;

	public void initView(Observer view, CallbackModel model) {
		this.parent = view;
		this.model = model;
		setLayout(new BorderLayout());

	}

	public void cleanup() {
		// if (model instanceof Model)
		// ((Model)model).cleanup();
		// model = null;
	}

	public void update(Observable o, Object arg) {
		// Util.debug(this, "Launcher>>update:o: " + o + ", arg: " + arg);

		if (arg instanceof Hashtable) {
			Hashtable args = (Hashtable) arg;
			Object aspect = args.get("aspect");

			if (aspect instanceof Button) {
				if (args.size() == 2)
					acceptButton(((Button) aspect).getLabel(), args.get("arg1"));
				else if (args.size() == 3)
					acceptButton(((Button) aspect).getLabel(),
							args.get("arg1"), args.get("arg2"));
				else if (args.size() == 4)
					acceptButton(((Button) aspect).getLabel(),
							args.get("arg1"), args.get("arg2"), args
									.get("arg3"));
			} else if (aspect instanceof Choice) {
				if (args.size() == 2)
					acceptChoice(((Choice) aspect).getSelectedItem(), args
							.get("arg1"));
				else if (args.size() == 3)
					acceptChoice(((Choice) aspect).getSelectedItem(), args
							.get("arg1"), args.get("arg2"));
				else if (args.size() == 4)
					acceptChoice(((Choice) aspect).getSelectedItem(), args
							.get("arg1"), args.get("arg2"), args.get("arg3"));
			} else if (aspect instanceof MenuItem) {
				if (args.size() == 2)
					acceptMenuItem(((MenuItem) aspect).getLabel(), args
							.get("arg1"));
				else if (args.size() == 3)
					acceptMenuItem(((MenuItem) aspect).getLabel(), args
							.get("arg1"), args.get("arg2"));
				else if (args.size() == 4)
					acceptMenuItem(((MenuItem) aspect).getLabel(), args
							.get("arg1"), args.get("arg2"), args.get("arg3"));
			} else if (args.size() == 2)
				update(o, aspect, args.get("arg1"));
			else if (args.size() == 3)
				update(o, aspect, args.get("arg1"), args.get("arg2"));
			else if (args.size() == 4)
				update(o, aspect, args.get("arg1"), args.get("arg2"), args
						.get("arg3"));
		}
	}

	public void update(String aspect, String[] args) {
		// implemented by subclasses
		logger.info("Launcher>>update:aspect: " + aspect + " args: "
				+ SorcerUtil.arrayToString(args));
	}

	public void update(Observable o, Object aspect, Object arg1) {
		// implemented by subclasses
		logger.info("Launcher>>update:aspect: " + aspect + " arg1: " + arg1);
	}

	public void update(Observable o, Object aspect, Object arg1, Object arg2) {
		// implemented by subclasses
		logger.info("Launcher>>update:aspect: " + aspect + " arg1: " + arg1
				+ " arg2: " + arg2);
	}

	public void update(Observable o, Object aspect, Object arg1, Object arg2,
			Object arg3) {
		// implemented by subclasses
		logger.info("Launcher>>update:aspect: " + aspect + " arg1: " + arg1
				+ " arg2: " + arg2 + " arg3: " + arg3);
	}

	public void acceptButton(String buttonLabel, Object arg1) {
		// implemented in subclasses
		logger.info("acceptButton:buttonLabel: " + buttonLabel + " arg1: "
				+ arg1);
	}

	public void acceptButton(String buttonLabel, Object arg1, Object arg2) {
		// implemented in subclasses
		logger.info("acceptButton:buttonLabel: " + buttonLabel + " arg1: "
				+ arg1 + " arg2: " + arg2);
	}

	public void acceptButton(String buttonLabel, Object arg1, Object arg2,
			Object arg3) {
		// implemented in subclasses
		logger.info("acceptButton:buttonLabel: " + buttonLabel + " arg1: "
				+ arg1 + " arg2: " + arg2 + " arg3: " + arg3);
	}

	public void acceptChoice(String choice, Object arg1) {
		// implemented in subclasses
		logger.info("acceptChoice:choice: " + choice + " arg1: " + arg1);
	}

	public void acceptChoice(String choice, Object arg1, Object arg2) {
		// implemented in subclasses
		logger.info("acceptChoice:choice: " + choice + " arg1: " + arg1
				+ " arg2: " + arg2);
	}

	public void acceptChoice(String choice, Object arg1, Object arg2,
			Object arg3) {
		// implemented in subclasses
		logger.info("acceptChoice:choice: " + choice + " arg1: " + arg1
				+ " arg2: " + arg2 + " arg3: " + arg3);
	}

	public void acceptMenuItem(String menuItem, Object arg1) {
		// implemented in subclasses
		logger.info("acceptMenuItem:menuItem: " + menuItem + " arg1: " + arg1);
	}

	public void acceptMenuItem(String menuItem, Object arg1, Object arg2) {
		// implemented in subclasses
		logger.info("acceptMenuItem:menuItem: " + menuItem + " arg1: " + arg1
				+ " arg2: " + arg2);
	}

	public void acceptMenuItem(String menuItem, Object arg1, Object arg2,
			Object arg3) {
		// implemented in subclasses
		logger.info("acceptMenuItem:menuItem: " + menuItem + " arg1: " + arg1
				+ " arg2: " + arg2 + " arg3: " + arg3);
	}

	public void mouseClicked(MouseEvent e) {
		// implement in subclasses
	}

	public void mouseEntered(MouseEvent e) {
		// implement in subclasses
	}

	public void mouseExited(MouseEvent e) {
		// implement in subclasses
	}

	public void mousePressed(MouseEvent e) {
		// implement in subclasses
	}

	public void mouseReleased(MouseEvent e) {
		// implement in subclasses
	}

	/**
	 * Return the location of component relative to screen.
	 * 
	 * @param comp
	 *            component as starting point of search.
	 * @return location related to screen.
	 */
	public static Point absLocation(Component comp) {
		Point fp = findWindow(comp).location();
		Point p = locationInWindow(comp);
		return new Point(fp.x + p.x, fp.y + p.y);
	}

	/**
	 * Return the location of component in relative to parent window.
	 * 
	 * @param comp
	 *            component as starting point of search.
	 * @return location related to window.
	 */
	public static Point locationInWindow(Component comp) {
		Point p = comp.location();
		for (Component parent = comp.getParent(); !(parent instanceof Window); parent = parent
				.getParent()) {
			p.translate(parent.location().x, parent.location().y);
		}
		return p;
	}

	/**
	 * Find the closest window parent from a component.
	 * 
	 * @param comp
	 *            component as starting point of search.
	 * @return nearest window.
	 */
	public static Window findWindow(Component comp) {
		for (Component parent = comp; parent != null; parent = parent
				.getParent()) {
			if (parent instanceof Window) {
				return (Window) parent;
			}
		}
		return null;
	}

	/**
	 * Returns a a parent Frame of aComponent being a child
	 */
	public static Frame getFrame(Component aComponent) {
		Component currentParent = aComponent;
		Frame outFrame = null;
		while (currentParent != null) {
			if (currentParent instanceof Frame) {
				outFrame = (Frame) currentParent;
				break;
			}
			currentParent = currentParent.getParent();
		}
		return outFrame;
	}
}
