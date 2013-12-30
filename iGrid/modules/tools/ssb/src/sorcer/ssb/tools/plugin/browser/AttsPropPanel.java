/*
 * Copyright 2009 the original author or authors.
 * Copyright 2009 SorcerSoft.org.
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

package sorcer.ssb.tools.plugin.browser;

import java.awt.BorderLayout;
import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.jini.core.entry.Entry;

public class AttsPropPanel extends JPanel {

	AttsTableModel _model;
	ArrayList _uiDescriptors;
	// Entry atts[];
	java.util.List data = new ArrayList();
	java.util.List fields = new ArrayList();

	AttsPropPanel(Entry attributes[]) {
		// atts=attributes;
		update(attributes);
		_model = new AttsTableModel(data, fields);
		JTable table = new JTable(_model);
		JScrollPane sp = new JScrollPane(table);
		setLayout(new BorderLayout());
		add(sp, BorderLayout.CENTER);
	}

	void update(Entry atts[]) {
		if (atts == null) {
			return;
		}
		data = new ArrayList();
		fields = new ArrayList();
		for (int j = 0; j < atts.length; j++) {
			if (atts[j] == null) {
				continue;
			}
			Class eClass = atts[j].getClass();
			String className = eClass.getName();
			Field f[] = eClass.getFields();
			for (int k = 0; k < f.length; k++) {
				String fName = f[k].getName();
				String displayName = className.substring(className
						.lastIndexOf(".") + 1, className.length());
				displayName = displayName + "." + fName;
				try {
					Object value = f[k].get(atts[j]);
					data.add(((Object) (new Object[] { displayName, 
							(value.getClass().isArray() ? Util.arrayToString(value) : value)})));
					// now store the actual class,field,isEditable

					fields.add(new Object[] { className, fName });
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		}
		if (_model != null) {
			_model.update(data, fields);

		}
	}

}
