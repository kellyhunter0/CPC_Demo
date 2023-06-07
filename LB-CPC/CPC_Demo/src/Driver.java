

public class Driver {
	private String ID;//Unique id
	private int expiresWeek;//week of license expiry
	private boolean finalYear;//True if license expires this year
	private Group group;
	private int[] duties;//Duties by week
	
	public Driver(String ID, int expiresWeek, boolean finalYear, Group group, int[] duties) throws Exception {
		this.ID = ID;
		while(this.ID.length()<5)
			this.ID = " "+this.ID;
		
		if ((expiresWeek >=0) &&(expiresWeek < ProblemParameters.WEEKS))
			this.expiresWeek = expiresWeek;
		else
			throw new Exception("Invalid expiry week - driver"+expiresWeek);
		
		this.duties = duties;
		this.finalYear = finalYear;
		this.group = group;

	}
	
	public Driver (String csv) {
		//Create a new Driver from the CSV
		String[] data = csv.split(",");
		String id = data[0];
		String g = data[1];
		Group group;
		if (g.equals("OFF"))
			group = Group.OFF;
		else if (g.equals("CEN"))
			group = Group.CEN;
		else if (g.equals("EBT"))
			group = Group.EBT;
		else if (g.equals("ECB"))
			group = Group.ECB;
		else if (g.equals("LCB"))
			group = Group.LCB;
		else if (g.equals("LMC"))
			group = Group.LMC;
		else if (g.equals("LON"))
			group = Group.MAR;
		else if (g.equals("LCB"))
			group = Group.MAR;
		else {
			System.out.println("Group not recognised :" + g);
		}
		String[] sduties = data[2].split(":");
		int[] duties = new int[sduties.length];
		for (int c=0; c < sduties.length;c++) {
			duties[c] = Integer.parseInt(sduties[c]);
		}
		
			
			
	}
	public String toCSV() {
		return (this.getID()+","+
				this.group()+","+
				this.printDuties()+","+
				this.finalYear() +"," +
				this.getExpiresWeek()
				);
	}

	public String printDuties() {
		String buffer = "";
		for(int duty : duties) {
			buffer = buffer + duty +":";
		}
		return buffer;
	}
	public int getDuty(int week) {
		return duties[week];
	}
	
	public String toString() {
		return ID +"," + expiresWeek +"," + finalYear  + "," +group;
	}
	
	public int getExpiresWeek() {
		return expiresWeek;
	}
	
	public boolean finalYear() {
		return finalYear;
	}
	
	public Group group() {
		return group;
	}
	
	public String getID(){
		return ID;
	}
}
