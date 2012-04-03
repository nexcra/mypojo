package erwins.util.temp;

/** 나중에 StringUtil에 합치자. */
public class StringTemp{
	
	/** null을 고려한 compare
	 * ex) int order = 0;
		order = StringTemp.compareWith(order, price, o.price, false);
		order = StringTemp.compareWith(order, cardName, o.cardName, true); */
	public static <T extends Comparable<T>> int compareWith(int exist,T a,T b,boolean asd){
		if(exist!=0) return exist;
		int order = 0;
		if(a==null && b==null) order =  0;
		else if(a==null && b!=null) order =  -1;
		else if(a!=null && b==null) order =  1;
		else order = a.compareTo(b);
		if(asd) return order;
		else return order * -1;
	}

}
