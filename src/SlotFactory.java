import java.util.ArrayList;

public class SlotFactory {
	/*
	 * Generate an ArrayList with a complete set of empty slots
	 */
	public static ArrayList<TrainingSlot> getSlotSet() throws Exception{
		ArrayList<TrainingSlot> set = new ArrayList<TrainingSlot>();
		for (int week = 0; week < ProblemParameters.WEEKS; week++) {
			if (ProblemParameters.WEEKDAYS_ONLY) {
				for (int day=1; day <6;day++) {
					for (int no = 0; no < ProblemParameters.TRAINING_PLACES; no++) {
						set.add(new TrainingSlot(week,day,no));
					}
				}
			}else{
				for (int day=0; day <7;day++) {
					for (int no = 0; no < ProblemParameters.TRAINING_PLACES; no++) {
						set.add(new TrainingSlot(week,day,no));
					}
				}
			}
		}
		return set;
	}
}
