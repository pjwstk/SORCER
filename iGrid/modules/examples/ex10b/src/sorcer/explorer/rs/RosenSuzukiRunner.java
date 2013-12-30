package sorcer.explorer.rs;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import sorcer.core.requestor.ExertletRunner;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.SignatureException;

public class RosenSuzukiRunner extends ExertletRunner {

	/* (non-Javadoc)
	 * @see sorcer.core.requestor.ExertionRunner#getExertion(java.lang.String[])
	 */
	@Override
	public Exertion getExertion(String... args) throws ExertionException {
		Object obj = null;
		try {
			obj = shell.evaluate(new File(System.getProperty("opti.exertlet.filename")));
			if (obj instanceof Exertion) {
				return (Exertion)obj;
			} else
				throw new RuntimeException("An Exertlet should return the object of Exertion type");
		} catch (CompilationFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void postprocess(String... args) throws ContextException {
		logger.info("<<<<<<<<<< Exceptions: \n" + exertion.getExceptions());
		logger.info("<<<<<<<<<< Ouput data context: \n" + exertion.getContext());
		logger.info("<<<<<<<<<< Control context: \n" + exertion.getControlContext());
	}
}