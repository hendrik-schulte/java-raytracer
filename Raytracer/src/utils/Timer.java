package utils;

public class Timer {

    private long timeStart;

    public Timer(){
        timeStart = System.currentTimeMillis();
    }

    /**
     * Starts the timer.
     */
    public void start(){
        timeStart = System.currentTimeMillis();
    }

    /**
     * Returns the time passed since the start method was called.
     **/
    public double stop() {
        long tEnd = System.currentTimeMillis();
        long tDelta = tEnd - timeStart;
        return tDelta / 1000.0;
    }
}
