
public class PCB {
	String status;
	int jobNumber;
	int priority;
	int jobSize;
	int maxCpuTime;
	int currentTime;
	int startingAddress;



	PCB(int jobNumber, int priority, int jobSize, int maxCpuTime, int currentTime, int startingAddress) {
		this.status = "CREATED";
		this.jobNumber = jobNumber;
		this.priority= priority;
		this.jobSize= jobSize;
		this.maxCpuTime= maxCpuTime;
		this.currentTime = currentTime;
		this.startingAddress= startingAddress;
	}
	
	public String toString() {
		return "Job#: " + this.jobNumber + ", priority: " + this.priority + ", jobSize: " + this.jobSize + ", maxCpuTime: " + this.maxCpuTime + ", currentTime: " + this.currentTime
		+ ", startingAddress: " + this.startingAddress;
	}

}
