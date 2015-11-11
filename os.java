import java.util.LinkedList;
import java.util.Queue;

public class os {

	public static Queue<PCB> jobTable;
	
	public static void startup() {
		System.out.println("Startup()");
		jobTable = new LinkedList<PCB>();
		sos.ontrace();
	}

	
	//Interrupt Handlers
	public static void Crint (int []a, int []p)   {
		System.out.println("Crint");
		BookKeeping();
		//create new PCB
		PCB pcb = new PCB(p[1], p[2], p[3], p[4], p[5]);
		jobTable.add(pcb);
		//get starting address from Swapper. Hardcoding values for now.
		int startingAddress = 0;
		sos.siodrum(pcb.jobNumber, pcb.jobSize, startingAddress, 0); 
		RunOSTasks();
	}
	public static void Dskint (int []a, int []p)  {
		System.out.println("Disk Interrupt");
		BookKeeping();
		/**
		 * 	Actual Interrupt handling code 
		 */
		RunOSTasks();
	}
	public static void Drmint (int []a, int []p)  {
		System.out.println("Drum Interrupt");
		BookKeeping();
		/**
		 * 	Actual Interrupt handling code 
		 */
		RunOSTasks();
	}
	public static void Tro (int []a, int []p)     {
		System.out.println("tro");
		BookKeeping();
		/**
		 * 	Actual Interrupt handling code 
		 */
		RunOSTasks();
	}
	public static void Svc (int []a, int []p)     {
		System.out.println("svc");
		BookKeeping();
		/**
		 * 	Actual Interrupt handling code 
		 */
		RunOSTasks();
	}
	
	static void ontrace() {
		System.out.println("Running ontrace");
	} 
	
	static void BookKeeping() {
		System.out.println("Running BookKeeping tasks");
	}
	
	static void RunOSTasks() {
		Swapper();
		Scheduler();
		//load and run?
	}
	
	static void Swapper () {
		System.out.println("Running Swapper");
		//find space in memory
		//call siodrum()
	}
	
	static void Scheduler() {
		System.out.println("Running Scheduler");
	}
}
