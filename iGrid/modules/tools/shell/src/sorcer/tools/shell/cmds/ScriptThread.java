package sorcer.tools.shell.cmds;

import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;

import net.jini.config.ConfigurationException;
import net.jini.core.transaction.TransactionException;

import org.codehaus.groovy.control.CompilationFailedException;

import sorcer.core.deploy.Deployment;
import sorcer.core.provider.exerter.ServiceExerter;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.tools.shell.NetworkShell;
import sorcer.tools.shell.ShellStarter;

public class ScriptThread extends Thread {
	private String script;
	private File scriptFile;
	private Object result;
	private Object target = null;
	private GroovyShell gShell = new GroovyShell(ShellStarter.getLoader());

	public ScriptThread(String script) {
		this.script = script;
	}

	public ScriptThread(File file) {
		this.scriptFile = file;
	}

	public void run() {
		synchronized (gShell) {
			if (script != null) {
				target = gShell.evaluate(script);
			} else {
				try {
					target = gShell.evaluate(scriptFile);
				} catch (CompilationFailedException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (target instanceof Exertion) {
			ServiceExerter se = new ServiceExerter((Exertion) target);
			try {
				if (((Exertion) target).isProvisionable()) {
					String configFile = (String) NetworkShell
							.getConfiguration().getEntry(
									"sorcer.tools.shell.NetworkShell",
									"exertionDeploymentConfig", String.class,
									null);
					if (configFile != null)
						result = se.exert(new Deployment(configFile));
					else 
						result = se.exert();
				} else
					result = se.exert();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (TransactionException e) {
				e.printStackTrace();
			} catch (ExertionException e) {
				e.printStackTrace();
			} catch (ConfigurationException e) {
				e.printStackTrace();
			}
		} else {			
			result = target;
		}
	}

	public Object getResult() {
		return result;
	}

	public Object getTarget() {
		return target;
	}
}
