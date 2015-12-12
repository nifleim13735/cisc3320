import java.util.Queue;

public class Scheduler {
	public static void Schedule(int[] a, int[] p) {
		scheduleIo();
		if (os.runningJob != null){
			if (os.runningJob.status.equals(PCB.RUNNING)){
				System.out.println("Moving runningjob to readyQueue ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				os.runningJob.status = PCB.READY;
				os.readyQueue.add(os.runningJob);
				os.runningJob = null;

			}
		
			scheduleNextFromReadyQueue();
		} else {
			scheduleNextFromReadyQueue();
		}

	}


	public static void scheduleNextFromReadyQueue(){
		PCB next = os.readyQueue.peek();
		if (next != null){
			if (next.cpuTimeRemaining() > 20000 && os.readyQueue.size() > 2 && next.outstandingIoRequests == 0){
				System.out.println("Swapping out long jobs");
				if (!os.isDrumBusy){
					Swapper.swapOut(next);
					next.startingAddress = -1;
					os.swappedOutQueue.add(os.readyQueue.poll());
					scheduleNextFromReadyQueue();
					return;
				}
			}
			System.out.println("Scheduled job #" + next.jobNumber + " to run in the next tick");
			os.nextScheduledJob = next;
			os.readyQueue.poll();
		}
		else {
			//	System.out.println("No job scheduled");
			os.nextScheduledJob = null;
		}
	}

	public static void scheduleIo() {
		PCB next = os.ioQueue.peek();
		if (next != null){
				if (!os.isDiskBusy){
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++----------------------------------> schduled cpu to do io for job" + next.jobNumber + ", " + next.status);
					os.isDiskBusy = true;
					sos.siodisk(next.jobNumber);
			}
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
			int i = os.jobTable.indexOf(job);
			os.jobTable.remove(i);
			Swapper.removeJobFromMemory(job.startingAddress, job.jobSize);
		}
	}
}
