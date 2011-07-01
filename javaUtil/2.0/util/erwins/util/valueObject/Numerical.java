package erwins.util.valueObject;

import java.math.BigDecimal;

import erwins.util.lib.FormatUtil;


/**
 * 숫자형 객체를 나타낸다.
 */
public abstract class Numerical{
    
   /**
 * @uml.property  name="value"
 */
BigDecimal value ;
   
   public Numerical(BigDecimal value){
       this.value = value;
   }
   
   abstract protected String getUnit();
   
   @Override
   public String toString(){
       return FormatUtil.INT.get(value) + getUnit();
   }
   
   /**
 * @return
 * @uml.property  name="value"
 */
public BigDecimal getValue(){
       return value;
   }
   
	
}