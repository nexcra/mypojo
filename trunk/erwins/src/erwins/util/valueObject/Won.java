package erwins.util.valueObject;

import java.math.BigDecimal;


/**
 * 금액을 나타낸다. 
 * Long.MAX_VALUE(9223372036854775807)를 넘을 수 없다.
 * @author erwins(my.pojo@gmail.com)
 */
public class Won{
    
    private static final int unit = 10000;
    
    private long money;
    private long temp;
    private int[] won = new int[4];
    
    public Won(BigDecimal money){
        if(money!=null) this.money = money.longValue(); 
        init();
    }
    public Won(String money){
        if(money!=null) this.money = Long.parseLong(money);
        init();
    }
    public Won(Long money){
        if(money!=null) this.money = money;
        init();
    }
    
    private void init() {        
        temp = money;
        for(int i=3;i>=0;i--){
            long tt = (long)Math.pow(unit,i);
            won[i] = (int) (temp / tt);
            temp -= won[i] * tt;
        }
    }
    
    @Override
    public String toString(){
        StringBuilder str = new StringBuilder();
        if(won[3]!=0) str.append(won[3]+"조");
        if(won[2]!=0) str.append(won[2]+"억");
        if(won[1]!=0) str.append(won[1]+"만");
        if(won[0]!=0) str.append(won[0]);
        str.append("원");
        return str.toString();
    }
}