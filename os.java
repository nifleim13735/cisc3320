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
		BookKeeping(p[5]);

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
		BookKeeping(p[5]);
			 PCB pcb = ioQueue.poll();
			 System.out.println("Job #" + pcb.jobNumber + " finished doing I/O ");
			 pcb.status = "READY";
			 readyQueue.add(pcb);  // put job completed I/O to the readyQueue
			 
		RunOSTasks(a, p);
	}
	
	public static void Drmint (int []a, int []p)  {
		System.out.println("Drum Interrupt");
		BookKeeping(p[5]);
		PCB pcb = createdQueue.poll();
		System.out.println("Changing state of Job #" + pcb.jobNumber + " from " + pcb.status + " to " + "READY");
		pcb.status = "READY";
		readyQueue.add(pcb);
		RunOSTasks(a, p);
	}
	
	public static void Tro (int []a, int []p)     {
		System.out.println("tro");
		BookKeeping(p[5]);
			if (a[0]==5)
		{
			//job termination
			p[4]=0; // job is done
			//PCB runningJob = new PCB(p[1], p[2], p[3], p[4], p[5]);  
			jobTable.remove(runningJob); //remove job from job table												
			//runningJob.termination(); //terminate job
			
			//FreeSpaceTable.AddFreeSpace(runningJob);
		}			
		else
		{
			//put a job into ready queue again because not enough time-slice (p[4]) to finish 
			p[5]=p[5]-p[4];   // CPU time still needed to finish a job, so set p[5]=0 again
			//PCB runningJob = new PCB(p[1], p[2], p[3], p[4], p[5]);  
			//readyQueue.add(runningJob.jobNumber);  //put a job to the ready queue again
		}
		RunOSTasks(a, p);
	}
	
	public static void Svc (int []a, int []p) throws Exception     {
		System.out.println("svc interrupt");
		BookKeeping(p[5]);
		
		switch(a[0]) {
			case 5: 
				System.out.println("Running job ( job# " + runningJob.jobNumber + " ) wants to terminate");
				runningJob.status = "TERMINATED";
				break;
			case 6: 
				System.out.println("Running job ( job# " + runningJob.jobNumber + " ) wants to do disk io");
				ioQueue.add(runningJob);  // job needs to do I/O
				sos.siodisk(runningJob.jobNumber); 
				break; 
			case 7: 
				System.out.println("Running job ( job# " + runningJob.jobNumber + " ) wants to be blocked");
				runningJob.status= "WAITING";
		}
		
		RunOSTasks(a, p);
	}

	static void ontrace() {
		System.out.println("Running ontrace");
	} 
	
	static void BookKeeping(int time) {
		System.out.println("Running BookKeeping tasks");
		//
		if(runningJob.status.equals("RUNNING")){
			//get time job entered CPU
			int timeInCPU = time - runningJob.currentTime;
			runningJob.currentTime = runningJob.maxCpuTime;
			runningJob.maxCpuTime = runningJob.maxCpuTime - timeInCPU;
		}
		
	}

	static void RunOSTasks(int[] a, int[] p) {
		Swapper();
		Scheduler(a, p);
		RunJob(a, p);
		//trace();

	}

	static void Swapper () {
		System.out.println("Running Swapper");
		int foundSpace;
		//find space in memory
		foundSpace = Swapper.FindFreeSpace(runningJob.jobSize, runningJob.jobNumber);
		
		//call siodrum()
		if(foundSpace != -1){
			System.out.println("Beginning drum transfer");
			sos.siodrum(runningJob.jobNumber, runningJob.jobSize, runningJob.startingAddress, foundSpace);
		}
		else{
			System.out.println("No space for job.");
		}
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
