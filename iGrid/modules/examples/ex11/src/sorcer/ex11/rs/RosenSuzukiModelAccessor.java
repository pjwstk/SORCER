package sorcer.ex11.rs;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;

import sorcer.core.requestor.ExertletRunner;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;

public class RosenSuzukiModelAccessor extends ExertletRunner {

	/* (non-Javadoc)
	 * @see sorcer.core.requestor.ExertionRunner#getExertion(java.lang.String[])
	 */
	@Override
	public Exertion getExertion(String... args) throws ExertionException {
		try {
			exertion = (Exertion)evaluate(new File(getProperty("model.exertion.filename")));
		} catch (CompilationFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return exertion;
	}

}