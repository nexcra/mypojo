
package erwins.util.tools;

import java.text.MessageFormat;
import java.util.*;

import org.apache.ecs.wml.Td;
import org.apache.ecs.wml.Tr;

import erwins.util.lib.*;
import erwins.util.valueObject.ShowTime;

/**
 * @author  Rod Johnson
 * @author  Juergen Hoeller
 * @since  May 2, 2001
 * @author  erwins : Rod Johnson이 만든것을 수정했다. 스레드당 하나의 객체를 생성한다.
 * @since  2008/9/10
 */
public class StopWatch {

    /**
     * Identifier of this stop watch. Handy when we have output from multiple
     * stop watches and need to distinguish between them in log or console
     * output.
     */
    private final String id;

    private boolean keepTaskList = true;

    /** List of TaskInfo objects */
    private final List<TaskInfo> taskList = new LinkedList<TaskInfo>();

    /** Start time of the current task */
    private long startNanoTime;

    /**
     * Is the stop watch currently running?
     * @uml.property  name="running"
     */
    private boolean running;

    /** Name of the current task */
    private String currentTaskName;

    /**
     * @uml.property  name="lastTaskInfo"
     * @uml.associationEnd  
     */
    private TaskInfo lastTaskInfo;

    /**
     * @uml.property  name="taskCount"
     */
    private int taskCount;

    /**
     * Total running time
     * @uml.property  name="totalNanoTime"
     */
    private long totalNanoTime;

    /**
     * Construct a new stop watch. Does not start any task.
     */
    public StopWatch() {
        this.id = "";
    }

    /**
     * Construct a new stop watch with the given id. Does not start any task.
     * 
     * @param id
     *            identifier for this stop watch. Handy when we have output from
     *            multiple stop watches and need to distinguish between them.
     */
    public StopWatch(String id) {
        this.id = id;
    }

    /**
     * Determine whether the TaskInfo array is built over time. Set this to "false" when using a StopWatch for millions of intervals, or the task info structure will consume excessive memory. Default is "true".
     * @uml.property  name="keepTaskList"
     */
    public void setKeepTaskList(boolean keepTaskList) {
        this.keepTaskList = keepTaskList;
    }

    /**
     * Start an unnamed task. The results are undefined if {@link #stop()} or
     * timing methods are called without invoking this method.
     * 
     * @see #stop()
     */
    public void start() throws IllegalStateException {
        start("");
    }

    /**
     * Start a named task. The results are undefined if {@link #stop()} or
     * timing methods are called without invoking this method.
     * 
     * @param taskName
     *            the name of the task to start
     * @see #stop()
     */
    public void start(String taskName) throws IllegalStateException {
        if (this.running) { throw new IllegalStateException("Can't start StopWatch: it's already running"); }
        this.startNanoTime = System.nanoTime();
        this.running = true;
        this.currentTaskName = taskName;
    }

    /**
     * Stop the current task. The results are undefined if timing methods are
     * called without invoking at least one pair {@link #start()} /
     * {@link #stop()} methods.
     * 
     * @see #start()
     */
    public void stop() throws IllegalStateException {
        if (!this.running) { throw new IllegalStateException("Can't stop StopWatch: it's not running"); }
        long lastTime = System.nanoTime() - this.startNanoTime;
        this.totalNanoTime += lastTime;
        this.lastTaskInfo = new TaskInfo(this.currentTaskName, lastTime);
        if (this.keepTaskList) {
            this.taskList.add(lastTaskInfo);
        }
        ++this.taskCount;
        this.running = false;
        this.currentTaskName = null;
    }

    public void check(String taskName) {
        this.stop();
        this.start(taskName);
    }

    /**
     * Return whether the stop watch is currently running.
     * @uml.property  name="running"
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * Return the total time in milliseconds for all tasks.
     * @uml.property  name="totalNanoTime"
     */
    public long getTotalNanoTime() {
        return totalNanoTime;
    }

    /**
     * Return the number of tasks timed.
     * @uml.property  name="taskCount"
     */
    public int getTaskCount() {
        return taskCount;
    }

    /**
     * Return an array of the data for tasks performed.
     */
    public List<TaskInfo> getTaskInfo() {
        if (!this.keepTaskList) { throw new UnsupportedOperationException("Task info is not being kept!"); }
        return taskList;
    }

    /**
     * Return a short description of the total running time.
     */
    public String shortSummary() {
        return "StopWatch '" + this.id + "': running time (millis) = " + getTotalNanoTime();
    }

    /**
     * Inner class to hold data about one task executed within the stop watch.
     */
    public static class TaskInfo {

        /**
         * @uml.property  name="taskName"
         */
        private final String taskName;

        /**
         * @uml.property  name="nanoTime"
         */
        private final long nanoTime;

        private TaskInfo(String taskName, long timeMillis) {
            this.taskName = taskName;
            this.nanoTime = timeMillis;
        }

        /**
         * Return the name of this task.
         * @uml.property  name="taskName"
         */
        public String getTaskName() {
            return taskName;
        }

        /**
         * Return the time in milliseconds this task took. 반환값이 double이다!! %계산을 위해 변환해준다.
         * @uml.property  name="nanoTime"
         */
        public double getNanoTime() {
            return nanoTime;
        }
        
        /**
         * Return the time in milliseconds this task took. 반환값이 double이다!! %계산을
         * 위해 변환해준다.
         */
        public long getNanoTimeLong() {
            return nanoTime;
        }

        /**
         * Return the time in milliseconds this task took.
         */
        public String getMicroTimeStr() {
            return Formats.INT.get(nanoTime / 1000);
        }
    }

    /**
     * 각 태스크의 percent를 리턴한다. 간이 메소드..
     */
    public String getPercent(int i) {
        return Maths.getRate(totalNanoTime,taskList.get(i).getNanoTime(),2) + "%";
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("============== StopWatch =============== \n");
        for (int i = 0, j = getTaskInfo().size(); i < j; i++) {
            ShowTime time = new ShowTime(getTaskInfo().get(i).getNanoTimeLong());
            str.append(MessageFormat.format("{0}.{1} : {2}({3})", i, getTaskInfo().get(i).getTaskName(),time.toString(10), getPercent(i)));
            str.append("\n");
        }
        return str.toString();
    }
    

    // ===========================================================================================
    //                                    static
    // ===========================================================================================    

    private static ThreadLocal<StopWatch> local = new ThreadLocal<StopWatch>();
    /**
     * @uml.property  name="threadTime"
     */
    private static ThreadLocal<Long> threadTime = new ThreadLocal<Long>();
    
    public static void initThreadTime() {
        threadTime.set(0L);
    }
    public static void addThreadTime(Long time) {
        if(threadTime.get()!=null) threadTime.set(threadTime.get()+time);
    }
    /**
     * @return
     * @uml.property  name="threadTime"
     */
    public static ShowTime getThreadTime() {
        return new ShowTime(threadTime.get());
    }
    public static String getThreadTimeStr() {
        if(threadTime.get()==null || threadTime.get()==0) return "== no watch detected ==";
        return "== thread total time = "+StopWatch.getThreadTime().toString(60) + " ==";
    }

    /**
     * 현재 스래드의 StopWatch를 초기화한다.
     */
    public static void initAndStamp(String taskName) {
        local.set(null);
        stamp(taskName);
    }
    
    /**
     * 현재 스래드의 StopWatch를 초기화한다.
     */
    public static void init() {
        local.set(null);
    }

    /**
     * 어디에서나 StopWatch를 체크 가능
     */
    public static void stamp(String taskName) {
        StopWatch stopWatch = local.get();
        if (stopWatch == null) {
            stopWatch = new StopWatch("controller");
            stopWatch.start(taskName);
        } else {
            if (stopWatch.isRunning()) stopWatch.stop();
            stopWatch.start(taskName);
        }
        local.set(stopWatch);
    }

    /**
     * ThreadTime에 총 시간을 더한 후 stopWatch를 반환한다.
     */
    public static StopWatch stopMe() {
        StopWatch stopWatch = local.get();
        stopWatch.stop();
        addThreadTime(stopWatch.getTotalNanoTime());
        return stopWatch;
    }

    /**
     * stop 후 table을 리턴한다.
     */
    public static String makeTable() {
        StopWatch stopWatch = stopMe();
        List<Tr> list = new ArrayList<Tr>();
        
        for (int i = 0; i < stopWatch.getTaskInfo().size(); i++) {
            Tr tr = new Tr();
            
            Td td = new Td(String.valueOf(i + 1));
            td.addAttribute("align","center");
            tr.addElement(td);

            Td td2 = new Td(stopWatch.getTaskInfo().get(i).getTaskName());
            td2.addAttribute("align","center");
            tr.addElement(td2);

            Td td3 = new Td(stopWatch.getTaskInfo().get(i).getMicroTimeStr() + "<br> (" + stopWatch.getPercent(i) + ")");
            td3.addAttribute("align","center");
            tr.addElement(td3);
            
            list.add(tr);
        }
        return Strings.joinTemp(list,"");
    }

}