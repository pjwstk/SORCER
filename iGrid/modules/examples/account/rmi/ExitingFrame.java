package rmi.account.client;

import javax.swing.*;
import java.awt.event.*;

public abstract class ExitingFrame extends JFrame {
    public ExitingFrame() {
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new ExitOnClose());
        buildGUI();
        doLayout();
        validate();
    }

    protected abstract void buildGUI();

    private class ExitOnClose extends WindowAdapter {
        public void windowClosed(WindowEvent event) {
            System.exit(0);
        }
    }
} 

