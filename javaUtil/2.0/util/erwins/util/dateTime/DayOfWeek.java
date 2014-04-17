package erwins.util.dateTime;


/** DOW의 3자리 영문표현 */
public enum DayOfWeek{
	SUN(0,"일"),
	MON(1,"월"),
	TUE(2,"화"),
	WED(3,"수"),
	THU(4,"목"),
	FRI(5,"금"),
	SAT(6,"토"),
	;
	/** new DateTime().getDayOfWeek() 하면 나오는 그거 */
	public final int index;
	/** 1자리 한글이름 */
	public final String name;
	private  DayOfWeek(int index,String name){
		this.index = index;
		this.name = name;
	}
	public static DayOfWeek getDayOfWeek(int index){
		for(DayOfWeek each : DayOfWeek.values()){
			if(each.index == index) return each;
		}
		throw new IllegalArgumentException("index가 잘못되었습니다. " + index);
	}
}