import java.util.Queue;

public class Scheduler {
	public static void Schedule() {
		//TODO: run actual algorithm
		//os.readyQueue
		if (os.runningJob != null) {
			if (os.runningJob.status == "WAITING") {
				os.waitingQueue.add(os.runningJob);
				os.runningJob = null;
			}
			else {
				os.runningJob.status = "READY";
				os.readyQueue.add(os.runningJob);
				os.runningJob = null;
			}
		}
	}

}
