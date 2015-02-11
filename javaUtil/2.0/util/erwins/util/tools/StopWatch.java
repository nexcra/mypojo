
package erwins.util.tools;

import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

import erwins.util.dateTime.TimeString;
import erwins.util.number.MathUtil;
import erwins.util.text.FormatUtil;

/**
 * @author  Rod Johnson
 * @author  Juergen Hoeller
 * @since  May 2, 2001
 * @author  erwins : Rod Johnson이 만든것을 수정했다. 스레드당 하나의 객체를 생성한다.
 * @since  2008/9/10
 * 쓰지말것!
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
    	if(isRunning()) this.stop();
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
            return FormatUtil.INT.get(nanoTime / 1000);
        }
    }

    /**
     * 각 태스크의 percent를 리턴한다. 간이 메소드..
     */
    public String getPercent(int i) {
        return MathUtil.getRate(totalNanoTime,taskList.get(i).getNanoTime(),2) + "%";
    }

    @Override
    public String toString() {
    	if(isRunning()) stop();
        StringBuilder str = new StringBuilder("============== StopWatch =============== \n");
        for (int i = 0, j = getTaskInfo().size(); i < j; i++) {
        	TimeString time = new TimeString(getTaskInfo().get(i).getNanoTimeLong() / 1000 / 1000);
            str.append(MessageFormat.format("{0}.{1} : {2}({3})", i, getTaskInfo().get(i).getTaskName(),time.toString(), getPercent(i)));
            str.append("\n");
        }
        return str.toString();
    }
    

    // ===========================================================================================
    //                                    static
    // ===========================================================================================    
    /** run이 가동한 시간을 측정한다. */
    public static StopWatch load(Runnable command) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();   
        command.run();
        stopWatch.stop();
        return stopWatch;
    }
    

}