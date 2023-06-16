
public enum Strategy {
 // Strategies of drivers for swapping high priority constraints with low priority ones to reduce the fitness of the solution
	
	SWAP_LOW, // swap low priority slot
	SWAP_MEDIUM, // swap medium priority slot
	SWAP_HIGH,  // swap high priority slot
	SWAP_FREE // swap free slot for a driver with no priorities set
}
