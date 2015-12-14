import java.util.ArrayList;
import java.util.Collections;

public class Swapper {
	public static int[] fst;
	public static ArrayList<FreeSpace> freeSpaceTable = new ArrayList<FreeSpace>();

	Swapper(){
		FreeSpace fs = new FreeSpace(0, 100);
		freeSpaceTable.add(fs);
		System.out.println("Free space table created.");
		printFreeSpaceTable();
		System.out.println("Freespace size: " + freeSpaceTable.size());
	}

	 public void swapIn() {
		 swapInFromCreatedQueue();
	 }
	 
	 	 
	 public void swapOut(PCB job){
			if (!os.isDrumBusy){
				System.out.println("Swapped out job #  " + job.jobNumber);
				job.status = PCB.SWAPPEDOUT;
				os.isDrumBusy = true;
				os.swapDirection = 1;
				removeJobFromMemory(job.startingAddress, job.jobSize);
				sos.siodrum(job.jobNumber, job.jobSize, job.startingAddress, 1);
			} 
	 }
	 
	public FreeSpace findFreeSpace(int jobSize) {
		System.out.println("Trying to find free space ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" );

		for (int i = 0, l = freeSpaceTable.size(); i < l; i++){
			if (freeSpaceTable.get(i).size >= jobSize){
				System.out.println("findfreespace Found freespace at: " + freeSpaceTable.get(i));
				System.out.println("for jobsize: " + jobSize);
				return freeSpaceTable.get(i);
			}
		}
		//No free space available
		return null;
	}

	public int addJobToMemory(int jobSize){
		System.out.println("Before addJob, freeSpaceTable contains:");
		printFreeSpaceTable();
		
		FreeSpace fs = findFreeSpace(jobSize);
		System.out.println("After findFreeSpace, freeSpaceTable contains:");
		printFreeSpaceTable();
		//removeFreeSpacesWithSizeZero();
		if (fs != null){
			System.out.println("Found free space at MMMM++++++++++++++++++++++++++++++++++++++++MMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM" + fs.toString());
			int address = fs.address;
			//update the existing free space 
			fs.setStartAddress(fs.address + jobSize);

			
//			if(fs.size == 0){
//				freeSpaceTable.remove(fs);
//			}
//			
//			System.out.println("Starting address is: " + address);
//			System.out.println("After addJob, freeSpaceTable contains:");
//			printFreeSpaceTable();

			if (fs.size == 0) freeSpaceTable.remove(fs);

			return address;
		} 

		//	System.out.println("Unable to find memory");
		return -1;
	}

	public void removeJobFromMemory(int startAddress, int jobSize){
		System.out.println("Freeing memory");
		FreeSpace fs = new FreeSpace(startAddress, jobSize);
		mergeFreeSpace(fs);
		mergeFreeSpaces();
		Collections.sort(freeSpaceTable);
		System.out.println("Free Space Table after removed job.");
		printFreeSpaceTable();
	}
	
	void mergeFreeSpace(FreeSpace fs) {
		System.out.println("Before merge, freeSpaceTable contains:");
		printFreeSpaceTable();
		//combine free spaces
		int l = freeSpaceTable.size();
		for (int i = 0; i < l; i++){
			FreeSpace current = freeSpaceTable.get(i);
			
			System.out.println("freespacefound " + current.toString());
			
			if (fs.address == current.endAddress()){
				System.out.println("merging with previous free space");
				System.out.println("prevfreespace " + current.toString());
				current.size += fs.size;
				//mergeFreeSpace(fs);
				return;
			}
			if (fs.endAddress() == current.address){
				System.out.println("merging with next free space");
				System.out.println("nextfreespace " + current.toString());
				current.setStartAddress(fs.address);
				return;
			}

		}
		
		System.out.println("No contiguous free space. Adding new free space to table: " + fs.toString());
		freeSpaceTable.add(fs);
		
		System.out.println("After merge, freeSpaceTable contains:");
		printFreeSpaceTable();
	}

	public void mergeFreeSpaces (){
		FreeSpace previous = null;
		FreeSpace current;
		
		 ArrayList<FreeSpace> toRemove = new ArrayList<FreeSpace>();
	
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

	public void printFreeSpaceTable() {
		System.out.println("Free Space Table");
		System.out.println("Table size: " + freeSpaceTable.size());
		for (int i = 0, l = freeSpaceTable.size(); i < l; i++) {
			System.out.println(freeSpaceTable.get(i).toString());
		}
	}

	public void swapInFromCreatedQueue() {
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
				System.out.println("Swapping from memory to core");
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
