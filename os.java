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
	public static Swapper Swapper;
	public static Boolean isDrumBusy = false;
	static final int TIMESLICE = 100; //TBD

	public static void startup() {
		System.out.println("Startup()");
		jobTable = new LinkedList<PCB>();
		createdQueue= new LinkedList<PCB>();
		readyQueue= new LinkedList<PCB>();
		waitingQueue= new LinkedList<PCB>();
		ioQueue= new LinkedList<PCB>();
		Swapper = new Swapper();
		
		//sos.ontrace();
	}


	//Interrupt Handlers
	public static void Crint (int []a, int []p)  {
		System.out.println("Crint");
		BookKeeping(p[1],p[5],p[4]);

		//get starting address from Swapper. Hardcoding values for now.
		int startingAddress = Swapper.addJobToMemory(p[3]);
		if (startingAddress < 0){
			System.out.println("Unable to find free spce... need to solve this");
			Swapper.printFreeSpaceTable();
		}
		Swapper.printFreeSpaceTable();

		//create new PCB and add to jobTable
		PCB pcb = new PCB(p[1], p[2], p[3], p[4], p[5], startingAddress);
		System.out.println(pcb.toString());
		jobTable.add(pcb);
		createdQueue.add(pcb);
		if (!os.isDrumBusy && pcb.startingAddress > -1){
			os.isDrumBusy = true;
			sos.siodrum(pcb.jobNumber, pcb.jobSize, pcb.startingAddress, 0);
		}
		trace();
		RunOSTasks(a, p);
	}

	public static void Dskint (int []a, int []p)  {
		System.out.println("Disk Interrupt");
		BookKeeping(p[1],p[5],p[4]);
		PCB pcb = ioQueue.poll();
		System.out.println("Job #" + pcb.jobNumber + " finished doing I/O ");
		pcb.ioRequestCompleted();

		RunOSTasks(a, p);
	}

	public static void Drmint (int []a, int []p)  {
		System.out.println("Drum Interrupt");
		BookKeeping(p[1],p[5],p[4]);
		PCB pcb = createdQueue.poll();
		System.out.println("Changing state of Job #" + pcb.jobNumber + " from " + pcb.status + " to " + "READY");
		isDrumBusy = false;
		pcb.status = PCB.READY;
		readyQueue.add(pcb);
		trace();
		RunOSTasks(a, p);
	}

	public static void Tro (int []a, int []p)     {
		System.out.println("tro interrupt");
		BookKeeping(p[1],p[5],p[4]);
		if (runningJob.cpuTimeUsed < runningJob.maxCpuTime) {
			System.out.println("Running Job is about to get ready ");
			runningJob.status = PCB.READY;
		} else {
			System.out.println("Running Job is scheduled for termination ");
			Scheduler.terminateJob(runningJob);
		}
		RunOSTasks(a, p);
	}

	public static void Svc (int []a, int []p) throws Exception     {
		System.out.println("svc interrupt");
		BookKeeping(p[1],p[5],p[4]);

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

	static void BookKeeping(int jobnum,int p, int MaxTimeAllowed) {
		currentSystemTime = p;
		if (runningJob != null ){
			runningJob.incrementCpuTimeUsed(currentSystemTime - systemTimeWhenJobBeganToRun);
			System.out.println("Time used by job#"+jobnum+" "+ (currentSystemTime - systemTimeWhenJobBeganToRun)
			+" of "+ MaxTimeAllowed + " allowed timeSlice");

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
//			int foundSpace;
//			//find space in memory
//			foundSpace = Swapper.FindFreeSpace(runningJob.jobSize, runningJob.jobNumber);
//
//			//call siodrum()
//			if(foundSpace != -1){
//				System.out.println("Beginning drum transfer");
//				sos.siodrum(runningJob.jobNumber, runningJob.jobSize, runningJob.startingAddress, foundSpace);
//			}
//			else{
//				System.out.println("No space for job.");
//			}
	}

	static void Scheduler(int[] a, int[] p) {
		System.out.println("Running Scheduler");
		Scheduler.Schedule(a, p);
	}

	public static void RunJob(int[] a, int[] p) {
		System.out.println("Run job" );
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
		System.out.println("Created Queue: " + printQueue (createdQueue));
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
