package sorcer.util.bdb.provider;

import static sorcer.eo.operator.context;
import static sorcer.eo.operator.in;
import static sorcer.eo.operator.job;
import static sorcer.eo.operator.out;
import static sorcer.eo.operator.path;
import static sorcer.eo.operator.pipe;
import static sorcer.eo.operator.sig;
import static sorcer.eo.operator.task;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Set;

import net.jini.core.transaction.TransactionException;
import sorcer.arithmetic.provider.Adder;
import sorcer.arithmetic.provider.Multiplier;
import sorcer.arithmetic.provider.Subtractor;
import sorcer.service.ContextException;
import sorcer.service.Exertion;
import sorcer.service.ExertionException;
import sorcer.service.Job;
import sorcer.service.SignatureException;
import sorcer.service.Task;
import sorcer.util.bdb.exertion.SorcerDatabase;
import sorcer.util.bdb.exertion.SorcerDatabaseViews;

import com.sleepycat.collections.TransactionRunner;
import com.sleepycat.collections.TransactionWorker;
import com.sleepycat.je.DatabaseException;

/**
 * ExerterDatabaseRunner is the main entry point for the program and may be run as
 * follows:
 * 
 * <pre>
 * java sorcer.util.bdb.provider
 *      [-h <home-directory> ]
 * </pre>
 * 
 * <p>
 * The default for the home directory is ./tmp -- the tmp subdirectory of the
 * current directory where the ServiceProviderDB is run. To specify a different
 * home directory, use the -home option. The home directory must exist before
 * running the sample. To recreate the sample database from scratch, delete all
 * files in the home directory before running the sample.
 * </p>
 * 
 * @author Mike Sobolewski
 */
public class ExerterDatabaseRunner {

    private final SorcerDatabase db;
    private final SorcerDatabaseViews views;

    /**
     * Run the sample program.
     */
    public static void main(String[] args) {
    	if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());
        System.out.println("\nRunning sample: " + ExerterDatabaseRunner.class);

        // Parse the command line arguments.
        //
        String homeDir = "./tmp";
        for (int i = 0; i < args.length; i += 1) {
            if (args[i].equals("-h") && i < args.length - 1) {
                i += 1;
                homeDir = args[i];
            } else {
                System.err.println("Usage:\n java " + ExerterDatabaseRunner.class.getName() +
                                   "\n  [-h <home-directory>]");
                System.exit(2);
            }
        }

        // Run the sample.
        //
        ExerterDatabaseRunner runner = null;
        try {
            runner = new ExerterDatabaseRunner(homeDir);
            runner.run();
        } catch (Exception e) {
            // If an exception reaches this point, the last transaction did not
            // complete.  If the exception is RunRecoveryException, follow
            // the Berkeley DB recovery procedures before running again.
            e.printStackTrace();
        } finally {
            if (runner != null) {
                try {
                    // Always attempt to close the database cleanly.
                    runner.close();
                } catch (Exception e) {
                    System.err.println("Exception during database close:");
                    e.printStackTrace();
                }
            }
        }
    }

	/**
	 * Open the database and views.
	 */
	private ExerterDatabaseRunner(String homeDir) throws DatabaseException {

		db = new SorcerDatabase(homeDir);
		views = new SorcerDatabaseViews(db);
	}

    /**
     * Close the database cleanly.
     */
    private void close()
        throws DatabaseException {

        db.close();
    }

    /**
     * Run two transactions to populate and print the database.  A
     * TransactionRunner is used to ensure consistent handling of transactions,
     * including deadlock retries.  But the best transaction handling mechanism
     * to use depends on the application.
     */
	private void run() throws Exception {
		TransactionRunner runner = new TransactionRunner(db.getEnvironment());
		runner.run(new PopulateDatabase());
		runner.run(new PrintDatabase());
	}

    /**
     * Populate the database in a single transaction.
     */
    private class PopulateDatabase implements TransactionWorker {

        public void doWork() {
        	try {
				addExertions();
			} catch (ExertionException e) {				
				e.printStackTrace();
			} catch (SignatureException e) {
				e.printStackTrace();
			} catch (ContextException e) {
				e.printStackTrace();
			}
        }
    }

    /**
     * Print the database in a single transaction.  All entities are printed
     * and the indices are used to print the entities for certain keys.
     *
     * <p> Note the use of special iterator() methods.  These are used here
     * with indices to find the runtimes for certain providers.</p>
     */
    private class PrintDatabase implements TransactionWorker {

        public void doWork() {
            printValues("Exertions",
                        views.getExertionSet().iterator());
            printValues("Runtime",
                        views.getRuntimeSet().iterator());
//            printValues("Runtimes for the provider",
//                        views.getRuntimeByProviderNameMap().duplicates(
//                                            "Arithmetic").iterator());
        }
    }

    /**
     * Populate the exertion entities in the database.  If the exertion set is not
     * empty, assume that this has already been done.
     * @throws ContextException 
     * @throws SignatureException 
     * @throws ExertionException 
     */
    @SuppressWarnings("unchecked")
	private void addExertions() throws ExertionException, SignatureException, ContextException {
    	String arg = "arg", result = "result";
		String x1 = "x1", x2 = "x2", y = "y";
        Set exertions = views.getExertionSet();
        if (exertions.isEmpty()) {
            System.out.println("Adding Exertions");
            
            // add three task exertions
            exertions.add(task("f3", sig("subtract", Subtractor.class), 
         		   context("subtract", in(path(arg, x1), null), in(path(arg, x2), null),
         			      out(path(result, y), null))));
            exertions.add(task("f5", sig("multiply", Multiplier.class), 
 				   context("multiply", in(path(arg, x1), 10.0), in(path(arg, x2), 50.0),
 					      out(path(result, y), null))));
            exertions.add(task("f4", sig("add", Adder.class), 
         		   context("add", in(path(arg, x1), 20.0), in(path(arg, x2), 80.0),
         			      out(path(result, y), null))));

            // add job exertion
    		Task f3 = task("f3", sig("subtract", Subtractor.class), 
    		   context("subtract", in(path(arg, x1), null), in(path(arg, x2), null),
    		      out(path(result, y), null)));
    		
    		Task f4 = task("f5", sig("multiply", Multiplier.class), 
    				   context("multiply", in(path(arg, x1), 10.0), in(path(arg, x2), 50.0),
    				      out(path(result, y), null)));
    		
    		Task f5 = task("f4", sig("add", Adder.class), 
    		   context("add", in(path(arg, x1), 20.0), in(path(arg, x2), 80.0),
    		      out(path(result, y), null)));

    		// Function Composition f3(f4(x1, x2), f5(x1, x2))
    		// Service Composition f1(f2(f4(x1, x2), f5(x1, x2)), f3)
    		//Job f1= job("f1", job("f2", f4, f5, strategy(Flow.PARALLEL, Access.PULL)), f3,
    		Job f1= job("f1", job("f2", f4, f5), f3,
    		   pipe(out(f4, path(result, y)), in(f3, path(arg, x1))),
    		   pipe(out(f5, path(result, y)), in(f3, path(arg, x2))));
    		
    		exertions.add(f1);
        }
    }

    /**
     * Print the objects returned by an iterator of entity value objects.
     * @throws ExertionException 
     * @throws TransactionException 
     * @throws RemoteException 
     */
	private void printValues(String label, Iterator iterator) {

		System.out.println("\n--- " + label + " ---");
		while (iterator.hasNext()) {
			Exertion xrt = (Exertion) iterator.next();
			if (xrt.getName().equals("f1")) {
				try {
					xrt = xrt.exert(null);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (TransactionException e) {
					e.printStackTrace();
				} catch (ExertionException e) {
					e.printStackTrace();
				}
			}
			System.out.println(xrt);
		}
	}
}
