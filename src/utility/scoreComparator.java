package utility;

import java.util.AbstractMap.SimpleEntry;
import java.util.Comparator;

public class scoreComparator implements Comparator<SimpleEntry<String,Double>>{
	
	@Override
	public int compare(SimpleEntry<String, Double> o1, SimpleEntry<String, Double> o2) {
//		 if(o1.getValue() < o2.getValue()){
//	            return 1;
//	        } else {
//	            return -1;
//	        }
		 
		 long thisBits = Double.doubleToLongBits(o1.getValue());
		 long anotherBits = Double.doubleToLongBits(o2.getValue());
		 return (thisBits == anotherBits ?  0 : 
			  (thisBits < anotherBits ? 1 : 
		      -1));  
	}
}
