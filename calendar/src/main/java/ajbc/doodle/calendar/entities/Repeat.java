package ajbc.doodle.calendar.entities;

public enum Repeat {
	
	NONE(0), DAILY_30_DAYS(30), WEEKLY_30_WEEKS(30);
	
	int quantity;
	
	Repeat(int quantity){
		this.quantity = quantity;
	}
}
