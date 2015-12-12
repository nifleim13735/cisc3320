
public class FreeSpace implements Comparable<FreeSpace> {
	int address;
	int size;
	public FreeSpace(int address, int size) {
		this.address = address;
		this.size = size;
	}
	public int endAddress () {
		return this.address + this.size;
	}
	
	public void setStartAddress(int newAddress) {
		int difference =  this.address - newAddress;
		this.size += difference;
		this.address = newAddress;
	}
	public String toString() {
		return "Start: " +  this.address + ", end: " + this.endAddress() + ", size: " +this.size;
	}
	@Override
	public int compareTo(FreeSpace o) {
		// TODO Auto-generated method stub
		return  this.address - o.address;
	}
}
