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

import java.awt.Color;
import java.awt.Font;

import javax.swing.JTextField;

/**
 * The Checker class is responsible for the functions that validate textboxes.
 * This basically involved obtaining a textbox and a string containing all the
 * characters not allowed (or which must exist within the string).
 * 
 * The Checker class also contains a function to verify that a string is a valid
 * email address. This does not include any form of domain name checking or
 * verification past being in a valid format. (so 34234987@988989r87987w.commu
 * will be fine)
 * 
 * The fonts are described in the bottom, and textboxes are set as red when they
 * are invalid. This does not update the verification fields (whether or not
 * something in the context is valid), it only checks the strings.
 */
public class GriderDispatcherChecker {
	public GriderDispatcherChecker() {
	}

	public boolean check_against(String bad, JTextField t) {
		// check against all characters in bad, if any exist in this textbox,
		// set it to invalid

		boolean set = true; // return is initially true
		// t.setFont(this.good);
		t.setForeground(Color.BLACK);
		String list = t.getText();

		for (int i = 0; i < list.length(); i++) {
			for (int j = 0; j < bad.length(); j++) {
				// if there exists a bad character, set the return equal to
				// false
				if (list.charAt(i) == bad.charAt(j)) {
					set = false;
					// t.setFont(this.bad);
					t.setForeground(Color.RED);
					break; // break the loop
				}
			}
		}
		return set;
	}

	public boolean check_for(String allowed, JTextField t) {
		boolean set = true;
		String list = t.getText();

		// search for something in the text which is not in the list of allowed
		// elements
		for (int i = 0; i < list.length(); i++) {
			if (allowed.indexOf(list.charAt(i)) < 0)
				set = false;
		}

		if (set == true) {
			// t.setFont(this.good);
			t.setForeground(Color.BLACK);
		} else {
			// t.setFont(this.bad);
			t.setForeground(Color.RED);
		}

		return set;
	}

	public boolean check_email(String bad, JTextField t) {
		// Check to see if any invalid characters are present
		String list = t.getText();

		if (!check_against(bad, t)) {
			return false;
		} else {
			// t.setFont(this.bad);
			t.setForeground(Color.RED);
		}

		String[] useranddomain = list.split("@");
		if (useranddomain.length != 2) {
			return false;
		} // means that there is not exactly 1 @ sign, which would be bad.

		String[] domainandext = useranddomain[1].split("\\."); // funky escape
		// to split on a
		// period

		if (domainandext.length < 2) {
			return false;
		} // means that there is not exactly one period..is this needed?

		// t.setFont(this.good);
		t.setForeground(Color.BLACK);

		return true;
	}

	// define what a good and bad font looks like
	private Font good = new Font("Palatino Linotype", Font.PLAIN, 12);
	private Font bad = new Font("Palatino Linotype", Font.ITALIC, 12);
}