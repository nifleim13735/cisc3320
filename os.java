import java.util.LinkedList;
import java.util.Queue;

public class os {

	public static Queue<PCB> jobTable;
	public static Queue<PCB> createdQueue;
	public static Queue<PCB> readyQueue;
	public static Queue<PCB> waitingQueue;
	public static Queue<PCB> ioQueue;
	public static PCB runningJob;
	public static PCB nextScheduledJob;
	public static int currentSystemTime = 0;
	public static int systemTimeWhenJobBeganToRun = 0;
	static final int TIMESLICE = 10; //TBD

	public static void startup() {
		System.out.println("Startup()");
		jobTable = new LinkedList<PCB>();
		createdQueue= new LinkedList<PCB>();
		readyQueue= new LinkedList<PCB>();
		waitingQueue= new LinkedList<PCB>();
		ioQueue= new LinkedList<PCB>();
		//sos.ontrace();
	}


	//Interrupt Handlers
	public static void Crint (int []a, int []p)   {
		System.out.println("Crint");
		BookKeeping(p);

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
		BookKeeping(p);
			 PCB pcb = ioQueue.poll();
			 System.out.println("Job #" + pcb.jobNumber + " finished doing I/O ");
			 pcb.ioRequestCompleted();
			 
		RunOSTasks(a, p);
	}
	
	public static void Drmint (int []a, int []p)  {
		System.out.println("Drum Interrupt");
		BookKeeping(p);
		PCB pcb = createdQueue.poll();
		System.out.println("Changing state of Job #" + pcb.jobNumber + " from " + pcb.status + " to " + "READY");
		pcb.status = PCB.READY;
		readyQueue.add(pcb);
		RunOSTasks(a, p);
	}
	
	public static void Tro (int []a, int []p)     {
		System.out.println("tro");
		BookKeeping(p);
		if (runningJob.cpuTimeUsed < runningJob.maxCpuTime) {
			runningJob.status = PCB.READY;
		} else {
			Scheduler.terminateJob(runningJob);
		}
		RunOSTasks(a, p);
	}
	
	public static void Svc (int []a, int []p) throws Exception     {
		System.out.println("svc interrupt");
		BookKeeping(p);
		
		switch(a[0]) {
			case 5: 
				System.out.println("job " + runningJob.jobNumber + " wants to terminate");
				Scheduler.terminateJob(runningJob);
				break;
			case 6: 
				System.out.println("Running job ( job# " + runningJob.jobNumber + " ) wants to do disk io");
				runningJob.status = PCB.READY;
				runningJob.ioRequested();
				sos.siodisk(runningJob.jobNumber);
				break; 
			case 7: 
				System.out.println("Running job ( job# " + runningJob.jobNumber + " ) wants to be blocked");
				runningJob.blockJob();
				break;
		}
		
		RunOSTasks(a, p);
	}

	static void BookKeeping(int[] p) {
		currentSystemTime = p[5];
		if (runningJob != null ){
			runningJob.incrementCpuTimeUsed(currentSystemTime - systemTimeWhenJobBeganToRun);
			
		}
	}
	


	static void RunOSTasks(int[] a, int[] p) {
		//trace();
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
		Scheduler.Schedule(a, p);
	}

	public static void RunJob(int[] a, int[] p) {
		systemTimeWhenJobBeganToRun = currentSystemTime;
		if (nextScheduledJob != null ) {
			runningJob = nextScheduledJob;
			runningJob.status = PCB.RUNNING;
			a[0] = 2;
			p[2] = runningJob.startingAddress;
			p[3] = runningJob.jobSize;
			p[4] = runningJob.timeSlice;
			System.out.println("Running scheduled job #" + runningJob.jobNumber);
			nextScheduledJob = null;
		} else {
			a[0] =1;
			System.out.println("No job to run");
			runningJob= null;
		}
	}

	/*
	 * Private methods for debugging
	 */
	private static void trace() {
		System.out.println("\n\n\n************* Status ***********************************************");
	//	System.out.println("Created Queue: " + printQueue (createdQueue));
		System.out.println("Ready Queue: " + printQueue (readyQueue));
		System.out.println("IO Queue: " + printQueue (ioQueue));
	//	System.out.println("Waiting Queue: " + printQueue (waitingQueue));
		System.out.println("Currently executing:" + ((runningJob != null) ? runningJob.toString() : " None"));
		System.out.println("\n\n*****************************");

	}

	 static String printQueue(Queue q) {
		StringBuilder sb = new StringBuilder();
		sb.append(q.size());
		for (int i = 0, l = q.size(); i < l; i++ ){
			sb.append(q.peek().toString());
		}
		return sb.toString();
	}
}
