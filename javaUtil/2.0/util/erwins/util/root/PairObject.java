package erwins.util.root;

import java.util.Comparator;


public class PairObject implements Pair{
	
	/** 이름으로 정렬 */
	public static final Comparator<PairObject> BY_NAME =  new Comparator<PairObject>() {
        @Override
        public int compare(PairObject o1, PairObject o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };
	
	private String name;
	private String value;
	
	public PairObject(){}
	public PairObject(String name,String value){
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	/** 해당하는 Pair를 찾는다 */
	public static Pair getPairByName(Pair[] pairs,String name) {
        for(Pair each : pairs) if(each.getName().equals(name)) return each;
        return null;
    }
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		PairObject other = (PairObject) obj;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (value == null) {
			if (other.value != null) return false;
		} else if (!value.equals(other.value)) return false;
		return true;
	}
	
	
	
	
}