import java.util.LinkedList;
import java.util.Queue;

public class os {

	public static Queue<PCB> jobTable;
	public static Queue<PCB> createdQueue;
	public static Queue<PCB> readyQueue;
	public static Queue<PCB> waitingQueue;
	public static Queue<PCB> ioQueue;
	public static PCB runningJob;
	static final int TIMESLICE = 10; //TBD

	public static void startup() {
		System.out.println("Startup()");
		jobTable = new LinkedList<PCB>();
		createdQueue= new LinkedList<PCB>();
		readyQueue= new LinkedList<PCB>();
		waitingQueue= new LinkedList<PCB>();
		ioQueue= new LinkedList<PCB>();
		//sos.offtrace();
	}


	//Interrupt Handlers
	public static void Crint (int []a, int []p)   {
		System.out.println("Crint");
		BookKeeping();

		//get starting address from Swapper. Hardcoding values for now.
		int startingAddress = 0;

		//create new PCB and add to jobTable
		PCB pcb = new PCB(p[1], p[2], p[3], p[4], p[5], startingAddress);
		jobTable.add(pcb);
		createdQueue.add(pcb);
		sos.siodrum(pcb.jobNumber, pcb.jobSize, startingAddress, 0); 
		RunOSTasks(a, p);
	}
	public static void Dskint (int []a, int []p)  {
		System.out.println("Disk Interrupt");
		BookKeeping();
		/**
		 * 	Actual Interrupt handling code 
		 */
		RunOSTasks(a, p);
	}
	public static void Drmint (int []a, int []p)  {
		System.out.println("Drum Interrupt");
		BookKeeping();
		PCB pcb = createdQueue.poll();
		System.out.println("Changing state of Job #" + pcb.jobNumber + " from " + pcb.status + " to " + "READY");
		pcb.status = "READY";
		readyQueue.add(pcb);

		/**
		 * 	Actual Interrupt handling code 
		 */
		RunOSTasks(a, p);
	}
	public static void Tro (int []a, int []p)     {
		System.out.println("tro");
		BookKeeping();
		/**
		 * 	Actual Interrupt handling code 
		 */
		RunOSTasks(a, p);
	}
	public static void Svc (int []a, int []p) throws Exception     {
		System.out.println("svc interrupt");
		BookKeeping();
		//Running job wants service a: 5 = terminate, 6 = diskIO, 7 = block
		System.out.println(runningJob.toString());
		if (a[0] == 5) {
			System.out.println("Running job ( job# " + runningJob.jobNumber + " ) wants to terminate");
		} else if (a[0] == 6){
			System.out.println("Running job ( job# " + runningJob.jobNumber + " ) wants to do disk io");
			ioQueue.add(runningJob);
			sos.siodisk(runningJob.jobNumber); 
		} else if (a[0] == 7) {
			System.out.println("Running job ( job# " + runningJob.jobNumber + " ) wants to be blocked");
			runningJob.status= "WAITING";
		} else throw new Exception("Unexpected svc code");

		RunOSTasks(a, p);
	}

	static void BookKeeping() {
		System.out.println("Running BookKeeping tasks");
		//
	}

	static void RunOSTasks(int[] a, int[] p) {
		Swapper();
		Scheduler(a, p);
		RunJob(a, p);
		//trace();

	}

	static void Swapper () {
		System.out.println("Running Swapper");
		//find space in memory
		//call siodrum()
	}

	static void Scheduler(int[] a, int[] p) {
		System.out.println("Running Scheduler");
		Scheduler.Schedule();
	}

	public static void RunJob(int[] a, int[] p) {
		System.out.println("Running scheduled job...");
		PCB pcb = os.readyQueue.peek();
		if (pcb != null && pcb.status.equals("READY")) {
			pcb.status = "RUNNING";
			os.runningJob =	os.readyQueue.poll();
			a[0] = 2;
			p[2] = pcb.startingAddress;
			p[3] = pcb.jobSize;
			p[4] = TIMESLICE;
		} else {
			a[0] =1;
			System.out.println("No job to run");
		}
	}

	/*
	 * Private methods for debugging
	 */
	private static void trace() {
		System.out.println("\n_____________Status__________________________________");
		System.out.println("Created Queue: " + printQueue (createdQueue));
		System.out.println("Ready Queue: " + printQueue (readyQueue));
		System.out.println("IO Queue: " + printQueue (ioQueue));
		System.out.println("\n____________________");

	}

	private static String printQueue(Queue q) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0, l = q.size(); i < l; i++ ){
			sb.append(q.peek().toString());
		}
		return sb.toString();
	}
}
