package sorcer.account.provider.ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Label;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.jini.core.lookup.ServiceItem;
import net.jini.lookup.entry.UIDescriptor;
import net.jini.lookup.ui.MainUI;
import sorcer.core.provider.Provider;
import sorcer.service.Service;
import sorcer.ui.serviceui.UIComponentFactory;
import sorcer.ui.serviceui.UIDescriptorFactory;
import sorcer.util.Sorcer;

public class HelloWorldTemplateUI extends JPanel {

	private final static Logger logger = Logger.getLogger(HelloWorldTemplateUI.class
			.getName());

	private ServiceItem item;
    // This variable gives access to the provider who invoked this UI.
	private Service provider;

	public HelloWorldTemplateUI(Object obj) {
		super();
		getAccessibleContext().setAccessibleName("HelloWorld UI");
		try {
			item = (ServiceItem) obj;
			logger.info("service class: " + item.service.getClass().getName()
					+ "\nservice object: " + item.service);

			if (item.service instanceof Provider) {
				provider = (Provider) item.service;
			}

            SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					createUI();
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void createUI() {
		setBackground(Color.yellow); // Set white background
		setLayout(new FlowLayout());
		Label label3 = new Label("This is a sample Window that you can extend or replace using your own implementation.");
        Label label4 = new Label("This implementation is contained in the 'ex0-sui' module in the 'HelloWorldImplUI.java' file.");
        add(label3);
        add(label4);
		validate();
	}

    /**
     * Returns a service UI descriptor of this service. Usually this method is
     * used as an entry in provider configuration files when smart proxies are
     * deployed with a standard off the shelf {@link sorcer.core.provider.ServiceProvider}.
     *
     * @return service UI descriptor
     */
    public static UIDescriptor getUIDescriptor() {
        UIDescriptor uiDesc = null;
        try {
            URL uiUrl = new URL(Sorcer.getWebsterUrl() + "/" + "");
            uiDesc = UIDescriptorFactory.getUIDescriptor(MainUI.ROLE,
                    new UIComponentFactory(new URL[] {uiUrl}, HelloWorldTemplateUI.class.getName()));
        } catch (Exception ex) {
            logger.severe("HelloWorldImplUI, Problem loading SUI: " +  ex.getMessage());
        }
        return uiDesc;
    }
}
