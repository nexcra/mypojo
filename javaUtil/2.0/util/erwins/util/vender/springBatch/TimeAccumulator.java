package erwins.util.vender.springBatch;

import java.util.Date;


/** 간단한 시간 누적기 - 스프링배치에서 사용
 * 작동시간이 긴 배치 / 현재 진행중인 배치 상태를 알기 위해 만들어졌다.
 * 로직의 시작시마다 호출해주자. 2번째 호출부터 남은시각을 알려준다.
 * 모든 로직은 시작할때 호출을 기준으로 작성되었다. 
 *  -- > 근데 사용안함. 일단 보류 */
public class TimeAccumulator{
    
    private long currentInterval = 0;
    private long beforeTime = 0;
    private long cumulatedTime = 0;
    private int times = 0;
    private boolean first = true;
    private Integer maxTime;
    private Date checkDate;
    
    /** 스레드 세이프를 위한 vo */
    public static class TimeAccumulatorInfo{
        public final long currentInterval;
        public final long remainTime;
        public final long avarageTime;
        public final int times;
        public final boolean first;
        public final Integer maxTime;
        public final Date checkDate;
        public TimeAccumulatorInfo(long currentInterval, long remainTime, long avarageTime, int times, boolean first, Integer maxTime,Date checkDate) {
            this.currentInterval = currentInterval;
            this.remainTime = remainTime;
            this.avarageTime = avarageTime;
            this.times = times;
            this.first = first;
            this.maxTime = maxTime;
            this.checkDate = checkDate;
        }
    }

    /** 시작할때 한번 찍어주고 다음 시작시 또 찍어서 측정한다. */
    public synchronized boolean check(){
        times++;
        checkDate = new Date();
        long now = System.currentTimeMillis();
        if(first) first = false;
        else{
            currentInterval = now - beforeTime;
            cumulatedTime += currentInterval;
        }
        beforeTime = now;
        return times > 1;
    }
    
    public synchronized TimeAccumulatorInfo info(){
        return new TimeAccumulatorInfo(currentInterval, getRemainTime(), getAvarageTime(),times, first,maxTime,checkDate);
    }
    
    /** 최초 접속 1회를 뺀다 */
    private long  getAvarageTime() {
        if(cumulatedTime==0) return 0;
        return cumulatedTime / (times-1) ;
    }

    /** check()가 먼저 실행되어야 함 */
    private long getRemainTime() {
        if(maxTime==null) return 0;
        return getAvarageTime() * (maxTime - times +1 );
    }

    public synchronized void setMaxTime(Integer maxTime) {
        this.maxTime = maxTime;
    }

}
