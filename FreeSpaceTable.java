import java.util.*;


public class FreeSpaceTable {
	
	private static int MemorySize=99;
    static int[] FreeSpaceTable;
	int i;
	
	
	  public FreeSpaceTable(int MemorySize)
	  {
        FreeSpaceTable = new int[MemorySize]; 
		
        this.MemorySize = MemorySize;
        
       
        for(int i = 0; i <= 99; i++) 
            FreeSpaceTable[i] = 0;
            
        
    }
	
	
	
	
	
	public static void MakeFreeSpace(PCB job)
	{
        for(int i = job.getAddress(); i < job.getAddress() + job.getjobSize() ; i++)
				FreeSpaceTable[i] = 0;
            
        job.setAddress(-1);    
            
        //calculate Space 
    }

}
