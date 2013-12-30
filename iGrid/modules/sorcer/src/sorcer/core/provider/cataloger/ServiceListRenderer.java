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
package sorcer.core.provider.cataloger;

import java.awt.Color;
import java.awt.Component;
import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import net.jini.core.entry.Entry;
import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.entry.ServiceType;
import sorcer.core.provider.Provider;
import sorcer.jini.lookup.entry.SorcerServiceInfo;

public class ServiceListRenderer extends JLabel implements ListCellRenderer {

    public ServiceListRenderer() {
        setOpaque(true);
    }

    public ServiceType getServiceType(ServiceItem si) {
        ServiceType st = null;
        Entry[] attribs = si.attributeSets;
        for (int i = 0; i < attribs.length; i++) {
            if (attribs[i] instanceof ServiceType) {
                st = (ServiceType) attribs[i];
                break;
            }
        }
        return st;
    }

    public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {

        if (!(value instanceof ServiceItem)) {
            throw new RuntimeException(
                    "This renderer is for ServiceItem instances only.");
        }

        ServiceItem si = (ServiceItem) value;
        Object obj = si.service;
        String text = "";
        String displayName;
        ServiceType st = getServiceType(si);

        if (obj instanceof Provider) {
            if (st != null) {
                displayName = "SORCER service: "
                        + ((SorcerServiceInfo) st).getProviderName()
                        + ", provider: " + st.getDisplayName();
                if (displayName != null) {
                    text = displayName;
                }
            }
        } else {
            if (st != null) {
                displayName = st.getDisplayName();
                if (displayName != null) {
                    text = displayName;
                }
            }
        }

        if (st == null)
            text = si.service.getClass().getName();
        else {
            String tooltip = st.getShortDescription();
            if (tooltip == null) {
                tooltip = "";
            }
            setToolTipText(tooltip);

            Image im = st.getIcon(0);
            Icon icon = null;
            if (im != null) {
                icon = new ImageIcon(im);
            }
            setIcon(icon);
        }

        // boolean isComponent = (si.service instanceof CompoundService);
        boolean isComponent = (si.service instanceof Provider);
        Color selectedColor = isComponent ? Color.red : Color.blue;
        Color unSelectedColor = isComponent ? Color.black : Color.gray;
        Color foreground = isSelected ? selectedColor : unSelectedColor;

        setBackground(Color.white);
        setForeground(foreground);
        setText(text);

        return this;
    }
}
