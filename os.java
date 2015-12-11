import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;

public class os {

	public static ArrayList<PCB> jobTable;
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
	public static Boolean isDiskBusy = false;
	static int TIMESLICE = 100; //TBD

	public static void startup() {
		System.out.println("Startup()");
		jobTable = new ArrayList<PCB>();
		os.createdQueue= new LinkedList<PCB>();
		readyQueue= new LinkedList<PCB>();
		waitingQueue= new LinkedList<PCB>();
		ioQueue= new LinkedList<PCB>();
		Swapper = new Swapper();
		
		sos.ontrace();
	}


	//Interrupt Handlers
	public static void Crint (int []a, int []p)  {
		System.out.println("Crint");
		BookKeeping(p[5]);

		//get starting address from Swapper. Hardcoding values for now.
		int startingAddress = -1 ;// Swapper.addJobToMemory(p[3]);
		//if (startingAddress < 0){
			//System.out.println("Unable to find free spce... need to solve this");
			//Swapper.printFreeSpaceTable();
		

		//create new PCB and add to jobTable
		PCB pcb = new PCB(p[1], p[2], p[3], p[4], p[5], startingAddress);
		System.out.println(pcb.toString() + "\n\n\n");
		jobTable.add(pcb);
		//if (pcb.startingAddress > -1){
		os.createdQueue.add(pcb);
		System.out.println(printQueue(os.createdQueue));
		Swapper.swapIn();
	//	}
		//trace();
		RunOSTasks(a, p);
	}

	public static void Dskint (int []a, int []p)  {
		System.out.println("Disk Interrupt");
		BookKeeping(p[5]);
		PCB pcb = ioQueue.peek();
		System.out.println("Job #" + pcb.jobNumber + " finished doing I/O ");
		pcb.ioRequestCompleted();
		os.isDiskBusy = false;
		Scheduler.scheduleNextFromIoQueue();
		RunOSTasks(a, p);
	}

	public static void Drmint (int []a, int []p)  {
		System.out.println("Drum Interrupt");
		BookKeeping(p[5]);
		PCB pcb = os.createdQueue.poll();
		System.out.println("Changing state of Job #" + pcb.jobNumber + " from " + pcb.status + " to " + "READY");
		isDrumBusy = false;
		pcb.status = PCB.READY;
		readyQueue.add(pcb);
		trace();
		RunOSTasks(a, p);
	}

	public static void Tro (int []a, int []p)     {
		System.out.println("tro");
		//trace();
		BookKeeping(p[5]);
		if (runningJob.cpuTimeUsed < runningJob.maxCpuTime) {
			runningJob.status = PCB.READY;
			readyQueue.add(runningJob);
		} else {
			Scheduler.terminateJob(runningJob);
		}
		RunOSTasks(a, p);
	}

	public static void Svc (int []a, int []p) throws Exception     {
		System.out.println("svc interrupt");
		BookKeeping(p[5]);

		switch(a[0]) {
		case 5: 
			System.out.println("job " + runningJob.jobNumber + " wants to terminate");
			Scheduler.terminateJob(runningJob);
			break;
		case 6: 
			System.out.println("Running job ( job# " + runningJob.jobNumber + " ) wants to do disk io");
			//runningJob.status = PCB.READY;
			runningJob.ioRequested();
			//sos.siodisk(runningJob.jobNumber);
			break; 
		case 7: 
			System.out.println("Running job ( job# " + runningJob.jobNumber + " ) wants to be blocked");
			if (runningJob.outstandingIoRequests > 0){
				runningJob.blockJob();
			}
			break;
		}

		RunOSTasks(a, p);
	}

	static void BookKeeping(int p) {
		currentSystemTime = p;
		if (runningJob != null ){
			runningJob.incrementCpuTimeUsed(currentSystemTime - systemTimeWhenJobBeganToRun);

		}
	}



	static void RunOSTasks(int[] a, int[] p) {
		//trace();
		Swapperr();
		Scheduler(a, p);
		RunJob(a, p);
		trace();

	}

	static void Swapperr () {
		//System.out.println("Running Swapper");
		Swapper.scheduleNextFromCreatedQueue();
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
		//System.out.println("Running Scheduler");
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
		//	System.out.println("Running scheduled job #" + runningJob.jobNumber);
			nextScheduledJob = null;
		} else {
			a[0] =1;
			System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!No job to run");
			runningJob= null;
		}
	}

	/*
	 *  methods for debugging
	 */
	public static void trace() {
		System.out.println("\n\n\n************* Status ***********************************************");
		System.out.println("Created Queue: " + printQueue (os.createdQueue));
		System.out.println("Ready Queue: " + printQueue (readyQueue));
		System.out.println("IO Queue: " + printQueue (ioQueue));
		//	System.out.println("Waiting Queue: " + printQueue (waitingQueue));
		System.out.println("Currently executing:" + ((runningJob != null) ? runningJob.toString() : " None"));
		Swapper.printFreeSpaceTable();
		System.out.println(printJobTable());
	//	System.out.println("\n\n*****************************");

	}

	static String printQueue(Queue q) {
		StringBuilder sb = new StringBuilder();
		sb.append("-----------------------------" + q.size());
		for (int i = 0, l = q.size(); i < l; i++ ){
		//	sb.append(q.peek().toString());
		}
		return sb.toString();
	}
	
	static String printJobTable (){
		System.out.println("Job Table:");
		StringBuilder sb = new StringBuilder();
		for (int i = 0, l = jobTable.size(); i < l; i++){
			sb.append(jobTable.get(i).toString());
			sb.append('\n');
		}
		return sb.toString();
	}
}
