import java.util.Queue;

public class Scheduler {
	public static void Schedule(int[] a, int[] p) {
		scheduleIo();
		if (os.runningJob != null){
			if (os.runningJob.status.equals(PCB.RUNNING)){
				//System.out.println("Moving runningjob to readyQueue ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				os.runningJob.status = PCB.READY;
				os.readyQueue.add(os.runningJob);
				os.runningJob = null;
			}
			scheduleNextFromReadyQueue();
		} 
		else {
			scheduleNextFromReadyQueue();
		}
	}


	public static void scheduleNextFromReadyQueue(){
		PCB next = os.readyQueue.peek();
		if (next != null){
			if (next.cpuTimeRemaining() > 20000 && os.readyQueue.size() > 2 && next.outstandingIoRequests == 0){
				System.out.println("Swapping out long jobs");
				if (!os.isDrumBusy){
					os.Swapper.swapOut(next);
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
			os.trace();
		}
	}

	public static void scheduleIo() {
		PCB next = os.ioQueue.peek();
		if (next != null){
			//if next job is swapped out swap it back in
			if (next.startingAddress < 0){
				if (!os.isDrumBusy){
					System.out.println("job that wants to do io is not incore.. need to swap it in Job #" + next.jobNumber);
					os.jobThatNeedsToBeSwappedInBecauseItNeedsToDoIo = next;
					os.Swapper.swapIn(next);
				}
				//while the next job is being swapped in there could be other jobs that need to do io
				//try to find those jobs
				next = null;
				for (int i = 0, l = os.jobTable.size(); i < l; i++){
					PCB job = os.jobTable.get(i);
					if (job.status == PCB.READY &&  job.outstandingIoRequests > 0 && job.isSwappedOut == false && job != os.jobThatNeedsToBeSwappedInBecauseItNeedsToDoIo){
						os.ioQueue.remove(job);
						System.out.println(job.toString());
						next = job;
						System.out.println(os.printJobTable());
						break;
					}
				}

				if (os.ioQueue.size() > 1){
					os.ioQueue.add(os.ioQueue.poll());
				}
			}
			if (next != null){
				dispatchIo(next);
			} else {
				System.out.println("No io job scheduled");
				os.trace();
			}
		}
	}

	public static void dispatchIo(PCB next) {
		if (!os.isDiskBusy && next.isSwappedOut == false){
			System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++----------------------------------> schduled cpu to do io for job" + next.jobNumber + ", " + next.status);
			os.isDiskBusy = true;
			os.jobCurrentlyDoingIo = next;
			sos.siodisk(next.jobNumber);
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
			os.Swapper.removeJobFromMemory(job.startingAddress, job.jobSize);
		}
	}
}
