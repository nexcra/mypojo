package erwins.util.number;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.google.common.base.Preconditions;

import erwins.util.root.NotThreadSafe;

/**
 * 스케일 정해놓고 계산하는 임시 계산기. 성능 무시 간단 계산시 사용
 * 빅데시말을 위임한다. 
 * 나누기 계산중에는 scale의 두배로 계산한다.
 * @author sin
 */
@SuppressWarnings("serial")
@NotThreadSafe
public class MutableDecimal extends Number{
	
	public static final BigDecimal N100 = new BigDecimal("100");
	
	private BigDecimal current = BigDecimal.ZERO;
	private int scale = 0;
	private RoundingMode roundingMode = RoundingMode.HALF_UP;
	
	public MutableDecimal(){};
	public MutableDecimal(int scale){
		this.scale = scale;
	}
	
	public MutableDecimal add(long value) {
		return add(new BigDecimal(value));
	}
	public MutableDecimal add(BigDecimal value) {
		current = current.add(value);
		return this;
	}
	
	public MutableDecimal neg(long value) {
		return neg(new BigDecimal(value));
	}
	/** 빼기 */
	public MutableDecimal neg(BigDecimal value) {
		current = current.add(value.negate());
		return this;
	}
	
	public MutableDecimal div(long mother) {
		return div(new BigDecimal(mother));
	}
	
	/** 분자(child) / 분모(mother) 를 구한다. 분모가 0이면 0을 리턴한다. */
    public MutableDecimal div(BigDecimal mother) {
    	Preconditions.checkNotNull(mother,"mother is required");
        if(MathUtil.isZero(mother)) current =  BigDecimal.ZERO;
        else current =  current.divide(mother,scale*2,roundingMode); //임시로 이렇게 놓는다.
        return this;
    }
    
    public MutableDecimal mul(BigDecimal multiplicand) {
    	current =  current.multiply(multiplicand);
    	return this;
    }
	
	public MutableDecimal scale(int scale) {
		this.scale = scale;
		return this;
	}

	public MutableDecimal roundingMode(RoundingMode roundingMode) {
		this.roundingMode = roundingMode;
		return this;
	}
	
	/** 계산중에 적용하지 않고 마지막에 적용한다. */
	public BigDecimal decimal() {
		return current.setScale(scale, roundingMode);
	}

	public BigDecimal getDecimal() {
		return current;
	}

	@Override
	public double doubleValue() {
		return current.doubleValue();
	}

	@Override
	public float floatValue() {
		return current.floatValue();
	}

	@Override
	public int intValue() {
		return current.intValue();
	}

	@Override
	public long longValue() {
		return current.longValue();
	}
	


}
