import java.util.ArrayList;

public class Swapper {
	public static int[] fst;
	public static ArrayList<FreeSpace> freeSpaceTable = new ArrayList<FreeSpace>();

	Swapper(){
		FreeSpace fs = new FreeSpace(0, 100);
		freeSpaceTable.add(fs);
	}

	public FreeSpace FindFreeSpace(int jobSize) {
		for (int i = 0, l = freeSpaceTable.size(); i < l; i++){
			if (freeSpaceTable.get(i).size >= jobSize){
				return freeSpaceTable.get(i);
			}
		}
		return null;
	}
	public int addJobToMemory(int jobSize){
		FreeSpace fs = FindFreeSpace(jobSize);
		if (fs != null){
			int address = fs.address;
			//update the existing free space
			fs.address = fs.address + jobSize;
			fs.size = fs.size - jobSize;
			return address;
		} 

		System.out.println("Unable to find memory");
		return -1;
	}

	public static int FindFreeSpace(int jobSize, int jobNumber){
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
			return -1;
		}
		return 0;
	}

	public static void addJobToMemory(int startAddress, int jobSize, int jobNumber){
		//add job to memory
		for(int i = startAddress; i < startAddress + jobSize; i++){
			fst[i] = jobNumber;
		}
	}

	public static void removeJobFromMemory(int jobNumber){
		//remove job from memory
		for(int i = 0; i< 100; i++){
			if(fst[i] == jobNumber){
				fst[i] = 0;
			}
		}
	}
}
