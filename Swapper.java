
public class Swapper {
	public static FreeSpaceTable fst;
	
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
