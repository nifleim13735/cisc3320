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

	public static FreeSpace FindFreeSpace(int jobSize) {
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
		if (fs != null){
			System.out.println("Found free space at " + fs.toString());
			int address = fs.address;
			//update the existing free space 
			fs.setStartAddress(fs.address + jobSize);
			return address;
		} 

		System.out.println("Unable to find memory");
		return -1;
	}

	public static void removeJobFromMemory(int startAddress, int jobSize){
		System.out.println("Freeing memory");
		FreeSpace fs = new FreeSpace(startAddress, jobSize);
		mergeFreeSpace(fs);
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
	public static void printFreeSpaceTable() {
		System.out.println("Free Space Table");
		for (int i = 0, l = freeSpaceTable.size(); i < l; i++) {
			System.out.println(freeSpaceTable.get(i).toString());
		}
	}
	
	public static void scheduleNextFromCreatedQueue() {
		PCB next = os.createdQueue.peek();
		if (next != null){
			if (next.startingAddress < 0 ){
				next.startingAddress = addJobToMemory(next.jobSize);
				if (next.startingAddress < 0){
					System.out.println("Unable to find free spce... need to solve this");
					printFreeSpaceTable();
				}

			}
			if (!os.isDrumBusy && next.startingAddress > -1){
				os.isDrumBusy = true;
				sos.siodrum(next.jobNumber, next.jobSize, next.startingAddress, 0);
			}
		}

	}
	
}
