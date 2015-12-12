import java.util.ArrayList;
import java.util.Collections;

public class Swapper {
	public static int[] fst;
	public static ArrayList<FreeSpace> freeSpaceTable = new ArrayList<FreeSpace>();

	Swapper(){
		FreeSpace fs = new FreeSpace(0, 100);
		freeSpaceTable.add(fs);
		printFreeSpaceTable();
	}

	public static void swapIn() {
		swapInFromCreatedQueue();
	}


	public static void swapOut(PCB job){
		if (!os.isDrumBusy){
			System.out.println("Swapped out job #  " + job.jobNumber);
			job.status = PCB.SWAPPEDOUT;
			os.isDrumBusy = true;
			os.swapDirection = 1;
			Swapper.removeJobFromMemory(job.startingAddress, job.jobSize);
			sos.siodrum(job.jobNumber, job.jobSize, job.startingAddress, 1);
		} 
	}

	public static void swapOutFromReadyQueue(){
		PCB next  = os.readyQueue.peek();
		if (next != null) {
			if (next.cpuTimeRemaining() > 20000 && os.readyQueue.size() > 2 && next.outstandingIoRequests == 0){
				System.out.println("Swapping out long jobs");
				if (!os.isDrumBusy){
					Swapper.swapOut(next);
					next.startingAddress = -1;
					os.swappedOutQueue.add(os.readyQueue.poll());
				}
			}
		}
	}

	public static FreeSpace FindFreeSpace(int jobSize) {
		System.out.println("Trying to find free space ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" );

		for (int i = 0, l = freeSpaceTable.size(); i < l; i++){
			if (freeSpaceTable.get(i).size >= jobSize){
				return freeSpaceTable.get(i);
			}
		}
		//No free space available
		return null;
	}


	public static int addJobToMemory(int jobSize){
		FreeSpace fs = FindFreeSpace(jobSize);
		//removeFreeSpacesWithSizeZero();
		if (fs != null){
			System.out.println("Found free space at MMMM++++++++++++++++++++++++++++++++++++++++MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM" + fs.toString());
			int address = fs.address;
			//update the existing free space 
			fs.setStartAddress(fs.address + jobSize);
			if (fs.size == 0) freeSpaceTable.remove(fs);
			return address;
		} 

		//	System.out.println("Unable to find memory");
		return -1;
	}

	public static void removeJobFromMemory(int startAddress, int jobSize){
		System.out.println("Freeing memory");
		FreeSpace fs = new FreeSpace(startAddress, jobSize);
		mergeFreeSpace(fs);
		mergeFreeSpaces();
		Collections.sort(freeSpaceTable);
		printFreeSpaceTable();
	}
	static void mergeFreeSpace(FreeSpace fs) {
		//combine free spaces
		for (int i = 0, l = freeSpaceTable.size(); i < l; i++){
			FreeSpace current = freeSpaceTable.get(i);
			if (fs.address == current.endAddress()){
				System.out.println("merging with previous free space");
				current.size += fs.size;
				//mergeFreeSpace(fs);
				return;
			}
			if (fs.endAddress() == current.address){
				System.out.println("merging with next free space");
				current.setStartAddress(fs.address);
				return;
			}

		}
		freeSpaceTable.add(fs);
	}

	static void mergeFreeSpaces (){
		FreeSpace previous = null;
		FreeSpace current;

		ArrayList<FreeSpace> toRemove = new  ArrayList<FreeSpace>();

		for (FreeSpace fs : freeSpaceTable){
			current = fs;
			if (previous != null) {
				if (previous.endAddress() == current.address ){
					System.out.println("merging with previous free space -------------- and removing current free space");
					System.out.println("previous: " + previous.toString());
					System.out.println("current: " + current.toString());
					previous.size += current.size;
					toRemove.add(current);
				}
			}
			previous = current;
		}
		freeSpaceTable.removeAll(toRemove);

	}

	public static void printFreeSpaceTable() {
		System.out.println("Free Space Table");
		for (int i = 0, l = freeSpaceTable.size(); i < l; i++) {
			System.out.println(freeSpaceTable.get(i).toString());
		}
	}

	public static void swapInFromCreatedQueue() {
		PCB next = os.createdQueue.peek();

		if  (next != null){
			if (next.startingAddress < 0 ){
				next.startingAddress = addJobToMemory(next.jobSize);
				if (next.startingAddress < 0){
					System.out.println("Unable to find free spce... need to solve this ------------------------------------------------------------->>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + next.jobNumber);
					os.createdQueue.add(os.createdQueue.poll());
					printFreeSpaceTable();
				}
			}
			if (!os.isDrumBusy && next.startingAddress > -1){
				os.isDrumBusy = true;
				os.swapDirection = 0;
				sos.siodrum(next.jobNumber, next.jobSize, next.startingAddress, 0);
			}
		} 
	}

	//	public static void swapInFromSwappedOutQueue() {
	//		for (int i = 0, l = os.jobTable.size(); i < l; i++){
	//			PCB job = os.jobTable.get(i);
	//			System.out.println("PRingint this shit");
	//			System.out.println(job.toString());
	//			if (job.status == PCB.SWAPPEDOUT){
	//				System.out.println("Swapping job back in");
	//				if (!os.isDrumBusy && job.startingAddress > -1){
	//					os.isDrumBusy = true;
	//					os.swapDirection = 0;
	//					sos.siodrum(job.jobNumber, job.jobSize, job.startingAddress, 0);
	//					break;
	//				}
	//			}
	//		}
	//	}


}
