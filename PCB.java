
public class PCB {
	String status;
	int jobNumber;
	int priority;
	int jobSize;
	int maxCpuTime;
	int currentTime;
	int startingAddress;
	int needsIO;
	int timeCPU_Used;
	boolean termination;



	PCB(int jobNumber, int priority, int jobSize, int maxCpuTime, int currentTime, int startingAddress) {
		
		this.status = "CREATED";
		this.jobNumber = jobNumber;
		this.priority= priority;
		this.jobSize= jobSize;
		this.maxCpuTime= maxCpuTime;
		this.currentTime = currentTime;
		this.startingAddress= startingAddress;
		needsIO=0;
		timeCPU_Used=0;
	}
	public int MaxCpuTime()
	{
		return maxCpuTime;
	}
	
	public int TimeCPU_Used()
	{
		return timeCPU_Used;
	}
	
	public int NeedsIO()
	{
		return needsIO;
	}
	
	public void Termination()
	{
		termination=true;
	}
	public String toString() {
		return "Job#: " + this.jobNumber + ", priority: " + this.priority + ", jobSize: " + this.jobSize + ", maxCpuTime: " + this.maxCpuTime + ", currentTime: " + this.currentTime
		+ ", startingAddress: " + this.startingAddress;
	}

}
