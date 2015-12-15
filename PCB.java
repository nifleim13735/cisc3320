
public class PCB {
	public final static String CREATED = "CREATED";
	public final static String READY = "READY";
	public final static String RUNNING = "RUNNING";
	public final static String WAITING = "WAITING";
	public final static String TERMINATED = "TERMINATED";
	public final static String SWAPPEDOUT = "SWAPPEDOUT";

	String status;
	int jobNumber;
	int priority;
	int jobSize;
	int maxCpuTime;
	int currentTime;
	int startingAddress;
	int cpuTimeUsed;
	int timeSlice;
	int outstandingIoRequests;
	Boolean isBlocked;
	Boolean markedForTermination;
	Boolean isSwappedOut;

	PCB(int jobNumber, int priority, int jobSize, int maxCpuTime, int currentTime, int startingAddress) {
		this.status = CREATED;
		this.jobNumber = jobNumber;
		this.priority= priority;
		this.jobSize= jobSize;
		this.maxCpuTime= maxCpuTime;
		this.currentTime = currentTime;
		this.startingAddress= startingAddress;
		this.cpuTimeUsed = 0;
		this.outstandingIoRequests = 0;
		this.isBlocked = false;
		this.markedForTermination = false;
		this.timeSlice = 0;
		this.isSwappedOut = false;
		this.setTimeSlice();
	}

	public void incrementCpuTimeUsed(int time){
		if (!(this.isBlocked)){
			this.cpuTimeUsed += time;
		}
		setTimeSlice();
	}

	public void ioRequested() {
		this.outstandingIoRequests += 1;				
		os.ioQueue.add(this);  // job needs to do I/O
		//os.readyQueue.remove(this);
	}

	public static void ioRequestCompleted() {
		PCB job = os.jobCurrentlyDoingIo; // os.ioQueue.peek();
		job.outstandingIoRequests -= 1;
		if (job != null){
			if (job.outstandingIoRequests == 0 ){
				System.out.println("All requests for job " + job.jobNumber + " completed");
				if (job.isBlocked) { job.unblockJob(); }
				if (job.markedForTermination){
					Scheduler.terminateJob(job);
					os.ioQueue.remove(job);
				} else {
					if (job.status != READY){
						System.out.println("Changing status of job # " + job.jobNumber + " from " + job.status + " to READY --------------->>>> and added to ready queue");
						job.status= READY;
						os.readyQueue.add(job);
						//os.trace();
					}
					os.ioQueue.remove(job);
				}
			}
		}
	}

	public void blockJob() {
		this.status = WAITING;
		this.isBlocked = true;
	}

	public void unblockJob() {
		this.isBlocked = false;
	}

	public void setTimeSlice(){
		this.timeSlice = (this.cpuTimeRemaining() < os.TIMESLICE) ? this.cpuTimeRemaining() : os.TIMESLICE;
	}


	public int cpuTimeRemaining()  {
		return maxCpuTime - cpuTimeUsed;
	}

	public void markForTermination() {
		this.markedForTermination = true;
	}

	public String toString() {
		return " Job#: " + this.jobNumber + " status:" + this.status + " priority: " + this.priority + " jobSize: " + this.jobSize + " maxCpuTime: " + this.maxCpuTime 
				+ " cpu time used: " + this.cpuTimeUsed + " cpu time remaining: " + this.cpuTimeRemaining() 
				+ " timeSlice on next tick: " + this.timeSlice + " outstanding I/O requests: " + this.outstandingIoRequests
				+ " startingAddress: " + this.startingAddress ;
	}
}
