package sorcer.arithmetic.requestor.monitor;

import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.core.event.RemoteEvent;
import net.jini.core.event.RemoteEventListener;
import net.jini.core.lease.Lease;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.LookupDiscovery;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.lease.LeaseRenewalManager;
import net.jini.lookup.ServiceDiscoveryManager;
import sorcer.core.Monitorable;
import sorcer.core.SORCER;
import sorcer.core.context.Contexts;
import sorcer.core.context.ServiceContext;
import sorcer.core.exertion.ServiceExertion;
import sorcer.core.monitor.MonitorManager;
import sorcer.core.monitor.MonitorSession;
import sorcer.core.provider.util.ProviderAccessor;
import sorcer.core.signature.ServiceSignature;
import sorcer.service.Context;
import sorcer.service.Exertion;
import sorcer.service.ServiceExertion;
import sorcer.service.Job;
import sorcer.service.Service;

public class ArithmeticMinitorableTester implements SORCER {

	static RemoteEventListener l;

	static LeaseRenewalManager lrm;

	public static void main(String[] args) throws Exception {
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new RMISecurityManager());

		LookupDiscovery disco = new LookupDiscovery(
				new String[] { "sorcer.DEV" });
		ServiceDiscoveryManager sdm = new ServiceDiscoveryManager(disco, null);
		ServiceItem item1 = null;
		ServiceItem item2 = null;

		while (item1 == null)
			item1 = sdm
					.lookup(
							new ServiceTemplate(
									null,
									new Class[] { sorcer.core.provider.monitor.MonitoringManagement.class },
									null), null);

		while (item2 == null)
			item2 = sdm.lookup(new ServiceTemplate(null,
					new Class[] { sorcer.arithmetic.ArithmeticRemote.class },
					null), null);

		lrm = new LeaseRenewalManager();

		l = (RemoteEventListener) (new MyListener("Job Listener").export());

		Job job = (Job) ((MonitorManager) item1.service)
				.register(l, new ArithmeticMinitorableTester().getJob(), 1000L);

		MonitorSession session = (MonitorSession) (job.monitorSession);
		System.out.println(session.getLease().getExpiration()
				- System.currentTimeMillis());

		lrm.renewUntil(session.getLease(), Lease.ANY, null);

		for (int i = 0; i < job.size(); i++) {
			System.out.println(" exertion " + i + " session="
					+ ((ServiceExertion) job.exertionAt(i)).monitorSession);
			new Thread(new TaskThread(job.exertionAt(i),
					(Monitorable) item2.service)).start();
		}

		while (true) {
		}

		// System.out.println( " a1 + a2 =" +
		// ((ArithmeticAddSubtractInterface)item.service).add(context));
		// System.out.println( " a1 + a2 =" +
		// ((ArithmeticAddSubtractInterface)item.service).subtract(context));
	}

	private Job service() throws Exception {

		Service servicer = ProviderAccessor.getJobber();

		// System.out.println(getJob());
		// getJob();
		// return null;
		return (servicer != null) ? (Job) servicer.service(getJob())
				: null;
	}

	private Job getJob() throws Exception {

		ServiceExertion task1 = getAddTask();

		ServiceExertion task2 = getMultiplyTask();

		Job job = new Job("Arithmetic");
		job.addExertion(task1);
		job.addExertion(task2);

		// public static void mapOutput(ServiceContext toContext, ServiceContext
		// fromContext, String toPath, String fromPath)
		// throws ContextException {
		Contexts.map(OUT_VALUE, task1.sc(), IN_VALUE + CPS + 1, task2.sc());

		return job;
	}

	private ServiceExertion getAddTask() throws Exception {

		ServiceContext context = new ServiceContext("test", "test");

		Contexts.putInValue(context, IN_VALUE + CPS + 0, "12");
		Contexts.putInValue(context, IN_VALUE + CPS + 1, "6");

		// We know that the output is gonna be placed in this path
		Contexts.putOutValue(context, OUT_VALUE, Context.EMPTY_LEAF);

		ServiceSignature method = new ServiceSignature("add",
				"sorcer.provider.arithmetic.ArithmeticRemote", null);

		ServiceExertion task = new ServiceExertion("arithmethic add", "arithmetic add",
				new ServiceSignature[] { method });
		task.setContext(context);

		return task;
	}

	private ServiceExertion getMultiplyTask() throws Exception {

		ServiceContext context = new ServiceContext("test", "test");

		Contexts.putInValue(context, IN_VALUE + CPS + 0, "12");
		// We want to stick in the result of add in here
		Contexts.putInValue(context, IN_VALUE + CPS + 1, "5");

		ServiceSignature method = new ServiceSignature("multiply",
				"sorcer.provider.arithmetic.ArithmeticRemote", null);
		ServiceExertion task = new ServiceExertion("arithmethic multiply",
				"arithmetic multiply", new ServiceSignature[] { method });
		task.setContext(context);

		return task;
	}

	private static class MyListener implements RemoteEventListener, Remote {

		String type;

		public MyListener() {

		}

		public MyListener(String type) {
			this.type = type;
		}

		public void notify(RemoteEvent re) throws RemoteException {
			System.out.println(type + ">>>>>>>>>>>>>>>>>>>" + re);
		}

		public Remote export() throws Exception {
			Exporter exp = new BasicJeriExporter(TcpServerEndpoint
					.getInstance(0), new BasicILFactory(), true, true);
			return exp.export(this);
		}

	}

	private static class TaskThread implements Runnable {

		Exertion ex;

		Monitorable servicer;

		public TaskThread(Exertion ex, Monitorable servicer) {
			this.ex = ex;
			this.servicer = servicer;
		}

		public void run() {
			try {
				MonitorSession session = (MonitorSession) (((ServiceExertion) ex).monitorSession);
				session.init(servicer, 1000L, 10000L);

				lrm.renewUntil(session.getLease(), Lease.ANY, null);

				Exertion result = servicer.service(ex);
				System.out.println(ex);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
