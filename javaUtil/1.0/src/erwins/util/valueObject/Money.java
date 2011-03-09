package erwins.util.valueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import erwins.util.lib.Formats;


/**
 * 금액을 나타낸다. BigDecimal을 delegate한다.
 * @author  erwins(my.pojo@gmail.com)
 */
public class Money{

    private static final long serialVersionUID = 1L;
    
    /**
     * @uml.property  name="decimal"
     */
    private BigDecimal decimal;
    
    @Override
    public String toString(){
        return Formats.INT.get(decimal)+"원";
    }
    
    public String toWonString(){
        return new Won(decimal).toString();
    }
    
    /**
     * @return
     * @uml.property  name="decimal"
     */
    public BigDecimal getDecimal() {
        return decimal;
    }

    public void add(BigDecimal augend) {
        decimal =  decimal.add(augend);
    }

    //////////// 추후 모두 void로 바꾸자.
    
    public BigDecimal divide(BigDecimal divisor, int scale, RoundingMode roundingMode) {
        return decimal.divide(divisor, scale, roundingMode);
    }

    public BigDecimal divide(BigDecimal divisor, RoundingMode roundingMode) {
        return decimal.divide(divisor, roundingMode);
    }

    public BigDecimal divide(BigDecimal divisor) {
        return decimal.divide(divisor);
    }

    public BigDecimal[] divideAndRemainder(BigDecimal divisor) {
        return decimal.divideAndRemainder(divisor);
    }

    public BigDecimal multiply(BigDecimal multiplicand) {
        return decimal.multiply(multiplicand);
    }

    public BigDecimal plus() {
        return decimal.plus();
    }

    public BigDecimal pow(int n) {
        return decimal.pow(n);
    }

    public BigDecimal setScale(int newScale, RoundingMode roundingMode) {
        return decimal.setScale(newScale, roundingMode);
    }

    public BigDecimal setScale(int newScale) {
        return decimal.setScale(newScale);
    }

    public BigDecimal subtract(BigDecimal subtrahend) {
        return decimal.subtract(subtrahend);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((decimal == null) ? 0 : decimal.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Money other = (Money) obj;
        if (decimal == null) {
            if (other.decimal != null) return false;
        } else if (!decimal.equals(other.decimal)) return false;
        return true;
    }
    
    
   
    
}