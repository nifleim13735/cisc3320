
public class PCB {
	public final static String CREATED = "CREATED";
	public final static String READY = "READY";
	public final static String RUNNING = "RUNNING";
	public final static String WAITING = "WAITING";
	public final static String TERMINATED = "TERMINATED";
	
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
	}

	public void ioRequestCompleted() {
		this.outstandingIoRequests -= 1;
		os.ioQueue.remove(this);
		if (this.outstandingIoRequests == 0 ){
			System.out.println("All requests for job " + this.jobNumber + " completed");
			if (this.isBlocked) { this.unblockJob(); }
			if (this.markedForTermination){
				Scheduler.terminateJob(this);
			} else {
				this.status= READY;
				os.readyQueue.add(this);
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
		return "\n Job#: " + this.jobNumber + "\n status:" + this.status + "\n priority: " + this.priority + "\n jobSize: " + this.jobSize + "\n maxCpuTime: " + this.maxCpuTime 
				+ "\n cpu time used: " + this.cpuTimeUsed + "\n cpu time remaining: " + this.cpuTimeRemaining() 
				+ "\n timeSlice on next tick: " + this.timeSlice + "\n outstanding I/O requests: " + this.outstandingIoRequests
				+ "\n startingAddress: " + this.startingAddress ;
	}

}
