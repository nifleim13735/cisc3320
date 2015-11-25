
public class PCB {
	String status;
	int jobNumber;
	int priority;
	int jobSize;
	int maxCpuTime;
	int timeCPU_Used;
	int currentTime;
	int processingTime;
	int startingAddress;
	int address;
	boolean needsIO;
	boolean termination;
	



	PCB(int jobNumber, int priority, int jobSize, int maxCpuTime, int currentTime, int startingAddress) {
		
		this.status = "CREATED";
		this.jobNumber = jobNumber;
		this.priority= priority;
		this.jobSize= jobSize;
		this.maxCpuTime= maxCpuTime;
		this.currentTime = currentTime;
		this.startingAddress= startingAddress;
		needsIO=false;
		timeCPU_Used=0;
		address = -1;
		termination=false;
	}
	
	public void ProcessingTime(int processingTime){
        this.processingTime = processingTime;
    }
	
	public void ProcessTime(int currentTime)
	{
        timeCPU_Used = timeCPU_Used + currentTime - processingTime;
    }
	
	public int MaxCpuTime()
	{
		return maxCpuTime;
	}
	
	public int TimeCPU_Used()
	{
		return timeCPU_Used;
	}
	
	public boolean NeedsIO_Status()
	{
		return needsIO;
	}
	
	public void wantsIO_On()
	{
		needsIO=true;
	}
	
	public void wantsIO_Off()
	{
		needsIO=false;
	}
	
	public boolean Termination_Status()
	{
		return termination;
	}
	
	public void Termination_On()
	{
		termination=true;
	}
	
	public void Termination_Off()
	{
		termination=false;
	}
	
	public int getAddress()
	{
        return address;
    }
	
	public void setAddress(int address)
	{
        this.address = address;
    }
	
	public int getjobSize()
	{
        return jobSize;
    }
	
	
	
	public String toString() {
		return "Job#: " + this.jobNumber + ", priority: " + this.priority + ", jobSize: " + this.jobSize + ", maxCpuTime: " + this.maxCpuTime + ", currentTime: " + this.currentTime
		+ ", startingAddress: " + this.startingAddress;
	}

}
