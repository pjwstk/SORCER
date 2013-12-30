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

package sorcer.core.grid.provider.dispatcher;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.jini.core.event.RemoteEventListener;
import net.jini.core.lookup.ServiceItem;


public class GridDispatcherAttribUI extends JFrame implements ActionListener {
    
    private JTextField locationTfld, hostnameTfld, opSysTfld, callPrvTfld;           
    private JComboBox locationCbx, hostnameCbx, opSysCbx, callPrvCbx;
    
    private JButton okBtn, cancelBtn;
    
    //private ProthRemote proth;
    private ServiceItem item;
    private RemoteEventListener listener;
    
    //----------------------------------BUILD GUI BEGIN------------------------------------
    public GridDispatcherAttribUI(){//Object obj) {
	super();
	try {
            
	    //this.item = (ServiceItem)obj;	    
	    // proth = (ProthRemote)item.service;	    
	    
	    //JPanel topPnl = new JPanel(new BorderLayout()) ;	    
	    JPanel mainPnl = new JPanel(new GridLayout(2,1));// Main Panel on which all panels to be added!!
	    setTitle("Specify Attributes");
	    //getContentPane().setLayout();
	    getContentPane().setLayout(new BoxLayout(this.getContentPane(),BoxLayout.Y_AXIS));
	    getContentPane().add(getAttribUI());
	    //getContentPane().add(getHostnameUI());
	    //getContentPane().add(getOpSysUI());
	    //getContentPane().add(getCallPrvUI());
	    getContentPane().add(getBtnUI());
	    //Display the window.
	    pack();
	    this.setResizable(false);
	    setVisible(true);
	} catch (Exception e) { 
	    System.out.println("Exception in Constructor");
	    e.printStackTrace(); }
    }
    public void actionPerformed(ActionEvent ae){
    }

    JPanel getAttribUI(){
	JPanel tempPnl = new JPanel(new GridLayout(5,2));
	JLabel locationLbl = new JLabel("Location: ",JLabel.RIGHT);//9
	locationTfld=new JTextField(10);
	locationCbx = new JComboBox();
	locationCbx.addItem("*");
	//Add all the locations found for the callers

	//

	JLabel hostnameLbl = new JLabel("Host Name: ",JLabel.RIGHT);//9
	hostnameTfld=new JTextField(10);	
	hostnameCbx = new JComboBox();
	hostnameCbx.addItem("*");
	//Add all hostnames found for callers

	//

	JLabel opSysLbl = new JLabel("OS: ", JLabel.RIGHT);//17
	opSysTfld=new JTextField(10);
	opSysCbx = new JComboBox();
	opSysCbx.addItem("*");
	//Add all Operating Systems found for callers as registered with lookup service

	//

	JLabel callPrvLbl = new JLabel("Node Name: ",JLabel.RIGHT);//18
	callPrvTfld=new JTextField(10);
	callPrvCbx = new JComboBox();
	callPrvCbx.addItem("*");
	//Add all Node Names for the callers as registered with the lookup service

	//

	tempPnl.add(new JLabel(" "));
	tempPnl.add(new JLabel(" "));
	tempPnl.add(locationLbl);
	tempPnl.add(locationCbx);
	//tempPnl.add(locationTfld);
	tempPnl.add(hostnameLbl);
	tempPnl.add(hostnameCbx);
	//tempPnl.add(hostnameTfld);
	tempPnl.add(opSysLbl);
	//tempPnl.add(opSysTfld);
	tempPnl.add(opSysCbx);
	tempPnl.add(callPrvLbl);
	//tempPnl.add(callPrvTfld);
	tempPnl.add(callPrvCbx);
	return tempPnl;
    }

    JPanel getBtnUI(){
	JPanel tempPnl = new JPanel();
	JButton cancelBtn = new JButton("Cancel");
	cancelBtn.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent ae) {
		    //System.exit(1);
		    setVisible(false);
		}
	    });
	JButton okBtn = new JButton("Save");
	okBtn.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent ae){
		}
	    });
	tempPnl.add(okBtn);
	tempPnl.add(cancelBtn);
	return tempPnl;
    }


}
    
