import java.util.ArrayList;
import java.util.Collections;

public class Swapper {
	public static int[] fst;
	public static ArrayList<FreeSpace> freeSpaceTable = new ArrayList<FreeSpace>();

	Swapper(){
		FreeSpace fs = new FreeSpace(0, 100);
		freeSpaceTable.add(fs);
		printFreeSpaceTable();
		System.out.println("Freespace size: " + freeSpaceTable.size());
	}

	public FreeSpace findFreeSpace(int jobSize) {
		System.out.println("Looking for freespace: " + jobSize);
		for (int i = 0, l = freeSpaceTable.size(); i < l; i++){
			if (freeSpaceTable.get(i).size >= jobSize){
				return freeSpaceTable.get(i);
			}
		}
		//No free space available
		return null;
	}
	
	public int addJobToMemory(int jobSize){
		FreeSpace fs = findFreeSpace(jobSize);
		if (fs != null){
			System.out.println("Found free space at " + fs.toString());
			int address = fs.address;
			//update the existing free space 
			fs.setStartAddress(fs.address + jobSize);
			System.out.print("Starting address is: " + address);
			return address;
		} 

		System.out.println("Unable to find memory");
		return -1;
	}

	public void removeJobFromMemory(int startAddress, int jobSize){
		System.out.println("Freeing memory");
		FreeSpace fs = new FreeSpace(startAddress, jobSize);
		mergeFreeSpace(fs);
		Collections.sort(freeSpaceTable);
		printFreeSpaceTable();
	}
	
	void mergeFreeSpace(FreeSpace fs) {
		//combine free spaces
		int l = freeSpaceTable.size();
		for (int i = 0; i < l; i++){
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
	public void printFreeSpaceTable() {
		System.out.println("Free Space Table");
		System.out.println("Table size: " + freeSpaceTable.size());
		for (int i = 0, l = freeSpaceTable.size(); i < l; i++) {
			System.out.println(freeSpaceTable.get(i).toString());
		}
	}
	
	public void scheduleNextFromCreatedQueue() {
//		PCB next = os.createdQueue.peek();
//		if (next != null){
//			if (!os.isDrumBusy){
//				os.isDrumBusy = true;
//				sos.siodrum(next.jobNumber, next.jobSize, next.startingAddress, 0);
//			}
//		}

	}
	
	public int FindFreeSpace(int jobSize, int jobNumber){
		int freeSpaceBegins = 0;
		int freeSpaceEnds = 0;
		boolean freeSpaceFound = false;

		for(int i = 0; i < 100; i++){
			//search memory for free space and mark where free space begins
			if(fst[i] == 0 && freeSpaceFound == false){
				freeSpaceBegins = i;
				freeSpaceFound = true;
			}

			//if memory is taken, mark address where free space ends
			if(fst[i] > 0 && freeSpaceFound == true){
				freeSpaceEnds = i;

				//if size of free space fits job
				if( (freeSpaceEnds - freeSpaceBegins) >= jobSize ){
					//add job to memory @ address freeSpaceBegins
					addJobToMemory(freeSpaceBegins, jobSize, jobNumber);
					return freeSpaceBegins;
				}else{
					//keep looking for space
					freeSpaceFound = false;
				}
			}
			//there is no space for job
//			return -1;
		}
		return -1;
	}

	public void addJobToMemory(int startAddress, int jobSize, int jobNumber){
		//add job to memory
		for(int i = startAddress; i < startAddress + jobSize; i++){
			fst[i] = jobNumber;
		}
	}

	public void removeJobFromMemory(int jobNumber){
		//remove job from memory
		for(int i = 0; i< 100; i++){
			if(fst[i] == jobNumber){
				fst[i] = 0;
			}
		}
	}
}
