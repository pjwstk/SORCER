package junit.sorcer.explorer.rs10a;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

import org.junit.Test;

import sorcer.service.ContextException;
import sorcer.service.ExertionException;
import sorcer.service.SignatureException;
import sorcer.util.Sorcer;
import sorcer.util.exec.ExecUtils.CmdResult;
import sorcer.vfe.evaluator.CmdEvaluator;

/**
 * @author Mike Sobolewski
 */

public class RosenSuzukiTest {
	private final static Logger logger = Logger
			.getLogger(RosenSuzukiTest.class.getName());
	
	@Test
	public void rsOptiTest() throws SignatureException,
			ExertionException, ContextException, IOException {
		String dir = Sorcer.getHome() 
			+ "/modules/examples/ex10a/src/junit/sorcer/explorer/rs10a/";
		
		CmdEvaluator cmd = new CmdEvaluator("ant -f " + dir + "rs-explorer-req-run.xml");
		//logger.info("result: " + cmd.getValue());
		CmdResult result = (CmdResult) cmd.getValue();
		//logger.info("cmd exit value: " + result.getExitValue() );
		if (result.getExitValue() != 0)
			throw new RuntimeException();
		
		File inputFile = new File(Sorcer.getHome() 
				+ "/modules/sorcer/test/rs/data/opti-test.out");
		Scanner scanner = new Scanner(inputFile);
		try {
			Double objectiveValue = new Double(scanner.nextLine());
			assertTrue(objectiveValue < 6.1 && objectiveValue > 5.9);
			//logger.info("opti objective value: " + objectiveValue);
		} finally {
			scanner.close();
			inputFile.delete();
		}
	}
	
}