
public class TrainingSlot {
	private int week;
	private int day;
	private int no;
	
	public String toString() {
		return week +"."+day+"."+no;
	}
	
	public TrainingSlot(int w,int d, int n) throws Exception {
		if (w < ProblemParameters.WEEKS)
			week = w;
		else
			throw  new Exception("Invalid week");
		
		if (d <7)
			day = d;
		else
			throw new Exception("Invalid day");
		
		if (n < ProblemParameters.TRAINING_PLACES)
			no = n;
		else
			throw new Exception("Invalid place");
	}
	
	public int getWeek() {
		return week;
	}
	
	public int getDay() {
		return day;
	}
	
	public int getNo() {
		return no;
	}
	
	@Override
    public boolean equals(Object object)
    {
        boolean sameSame = false;

        if (object != null && object instanceof TrainingSlot)
        {
        	TrainingSlot other = (TrainingSlot) object;
            sameSame = ((this.day == other.day)&&(this.week==other.week)&&(this.no==other.no));
        }

        return sameSame;
    }
}
