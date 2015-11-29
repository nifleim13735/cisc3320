import java.util.Queue;

public class Scheduler {
	public static void Schedule(int[] a, int[] p) {
		if (os.runningJob != null){
			if (os.runningJob.status.equals(PCB.READY)){
				os.nextScheduledJob = os.runningJob;
				os.runningJob = null;
			} else if (os.runningJob.status.equals(PCB.RUNNING)){
				System.out.println("Moving runningjob to readyQueue ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				os.runningJob.status = PCB.READY;
				os.readyQueue.add(os.runningJob);
				os.runningJob = null;
				scheduleNextFromReadyQueue();
			}
			else {
				System.out.println("Running job not READY ++++++++++++++++++++++++++++++++++++++++++++++++++++" + os.runningJob.status);
				scheduleNextFromReadyQueue();
			}
		} else {
			System.out.println("No running job ((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((");
			scheduleNextFromReadyQueue();

		}

	}


	public static void scheduleNextFromReadyQueue(){
		PCB next = os.readyQueue.peek();
		if (next != null){
			System.out.println("Scheduled job #" + next.jobNumber + " to run in the next tick");
			os.nextScheduledJob = next;
			os.readyQueue.poll();
		}
		else {
			System.out.println("No job scheduled");
			os.nextScheduledJob = null;
		}
	}


	public static void terminateJob(PCB job) {
		//can't terminate if any outstanding i/o requests
		if (job.outstandingIoRequests > 0){
			System.out.println("Job " + job.jobNumber + " marked for termination");
			job.status = "TERMINATED";
			job.markForTermination();
		}
		else {
			System.out.println("Job #" + job.jobNumber  + " TERMINATED");
			job.status = PCB.TERMINATED;
			System.out.println(job.toString());
			os.readyQueue.remove(job);
			os.jobTable.remove(job);
			Swapper.removeJobFromMemory(job.startingAddress, job.jobSize);
		}
	}
}
